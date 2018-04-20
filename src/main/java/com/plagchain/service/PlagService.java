package com.plagchain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.plagchain.Constants;
import com.plagchain.GenericResponse;
import com.plagchain.database.dbobjects.MinHashFeatures;
import com.plagchain.database.service.MinHashFeaturesService;
import com.plagchain.domain.ResponseItem;
import com.plagchain.domain.SimilarDocument;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Created by Jagrut on 22-05-2017.
 * Service class which will be used by the REST to transfer the task of plagiarism detection.
 */
@Service
public class PlagService {
    private final Logger log = LoggerFactory.getLogger(PlagService.class);
    private UtilService utilService;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    private MinHashFeaturesService minHashFeaturesService;
    private MongoTemplate mongoTemplate;

    public PlagService(MinHashFeaturesService minHashFeaturesService, UtilService utilService) {
        this.minHashFeaturesService = minHashFeaturesService;
        this.utilService = utilService;
    }

    /**
     * Extracts document features that was submitted for plagiarism check and find similar documents using simple
     * min hash algorithm.
     * @param file the test file
     * @param response containing initialized object which is passed from REST
     * @return list of document found similar
     */
    public ResponseItem extractDocFeaturesAndCheckSim(MultipartFile file, ResponseItem response) {
        log.info("Processing the pdf file for plagiarism check");

        //Parse pdf file for text and generate min hash
        String textFromFile = utilService.getTextFromDoc(file);
        String cleanedText = utilService.cleanText(textFromFile);
        cleanedText = utilService.removeAllWhiteSpaces(cleanedText);
        List<String> allShingles = new ArrayList<>();
        allShingles.addAll(utilService.createShingles(Constants.SHINGLE_LENGTH, cleanedText));
        int[] minHashFromShingles = utilService.generateMinHashSignature(allShingles);
        return runMinHashAlgorithm(Arrays.asList(ArrayUtils.toObject(minHashFromShingles)), response);
    }

    /**
     * Find similar items for test document in the present collection of MinHashFeatures min hash list in the Blockchain
     * that are confirmed and stored in DB.
     * @param plagCheckMinHashList list of min hash values from text whose signatures are not yet calculated
     * @param response Object of ResponseItem that should be populated and returned
     * @return {ResponseItem} object after populating with appropriate content
     */
    public ResponseItem runMinHashAlgorithm(List<Integer> plagCheckMinHashList, ResponseItem response) {
        log.info("Started Similarity Algorithm for Published work");
        initializeMongoConverter();
        List<SimilarDocument> similarDocuments = new ArrayList<>();
        DBCursor dbCursor = minHashFeaturesService.find();
        while(dbCursor.hasNext()) {
            MinHashFeatures singleDocumentDB = mongoTemplate.getConverter().read(MinHashFeatures.class, dbCursor.next());
            double similarityScore = MinHashAlgorithm(singleDocumentDB.getListMinHash(), plagCheckMinHashList);
            if(similarityScore > Constants.SIMSCORE_THRESHOLD) {
                similarDocuments.add(setFields(singleDocumentDB, similarityScore));
            }
        }
        response.setListOfSimilarDocuments(similarDocuments);
        response.setSuccess("success");
        return response;
    }

    /**
     * Simple Min Hash algorithm without performing LSH. This would work, but it is not efficient.
     * @param minHashListDB the list of min hash from DB to which the test document should be compared for similarity
     * @param plagCheckMinHashList the list of min hash from test document
     * @return {double} Similarity score between 0-1
     */
    private double MinHashAlgorithm(List<Integer> minHashListDB, List<Integer> plagCheckMinHashList) {
        if(minHashListDB.size() != plagCheckMinHashList.size()) {
            log.error("Size of signatures should be the same");
            return 0.0D;
        } else {
            double sim = 0.0D;

            for(int i = 0; i < minHashListDB.size(); i++) {
                if(minHashListDB.get(i).intValue() == plagCheckMinHashList.get(i).intValue()) {
                    ++sim;
                }
            }
            return sim / (double)minHashListDB.size();
        }
    }

    /**
     * Initializes mongo converter to be used by other methods to type cast the db cursor into required Java objects
     */
    private void initializeMongoConverter() {
        mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), databaseName));
        JaxbAnnotationModule jaxbAnnotationModule = new JaxbAnnotationModule();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(jaxbAnnotationModule);
    }

    /**
     * Set the field for similar document object from either MinHashFeatures Object
     * @param minHashFeatures MinHashFeatures object
     * @param similarityScore similarity score to set for this document
     * @return SimilarDocument object with populated fields
     */
    private SimilarDocument setFields(MinHashFeatures minHashFeatures, double similarityScore) {
        SimilarDocument addDoc = new SimilarDocument();
        addDoc.setSimilarityScore(similarityScore);
        addDoc.setFileName(minHashFeatures.getFileName());
        addDoc.setDocumentHash(minHashFeatures.getDocHashKey());
        addDoc.setContactInfo(minHashFeatures.getContactInfo());
        addDoc.setPublisherWalletAddress(minHashFeatures.getPublisherAddress());
        addDoc.setTimestamp(minHashFeatures.getTimestamp());
        return addDoc;
    }

    /**
     * Extract document features from the submitted file and store in DB and then publish to stream in blockchain
     * @param file the file to extract doc features from
     * @param contactInfo contactInfo, if provided by user
     * @param walletAddress plagchain wallet address of the user
     * @return GenericResponse, success or error while storing the doc features
     */
    public GenericResponse extractDocFeaturesAndStore(MultipartFile file, String contactInfo, String walletAddress) {
        GenericResponse response = new GenericResponse();
        //Calculate sha256 hash for document to be used as key
        List<String> sha256DocHash = new ArrayList<>();
        try {
            sha256DocHash = utilService.generateSHA256HashFromObjects(Arrays.asList(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String textFromPdf = utilService.parsePdf(file);
        String cleanedText = utilService.cleanText(textFromPdf);
        cleanedText = utilService.removeAllWhiteSpaces(cleanedText);
        List<String> allShingles = new ArrayList<>();
        allShingles.addAll(utilService.createShingles(Constants.SHINGLE_LENGTH, cleanedText));
        int[] minHashFromShingles = utilService.generateMinHashSignature(allShingles);

        //Transform to Hex string format and submit to plagchain
        String hexData = utilService.formatDataToHex(file.getOriginalFilename(), Arrays.asList(ArrayUtils.toObject(minHashFromShingles)), contactInfo, walletAddress);
        String responseFromBlockchain = utilService.submitToPlagchain(Constants.STREAMNAME, sha256DocHash.get(0), hexData);
        if(responseFromBlockchain != null && responseFromBlockchain.length() > 0)
            response.setSuccess(responseFromBlockchain);
        else
            response.setError("Error submitting document to blockchain in Min_Hash stream");
        return response;
    }
}
