package com.plagchain.service;

import com.mongodb.DBCursor;
import com.plagchain.database.dbobjects.PublishedWork;
import com.plagchain.database.dbobjects.UnpublishedWork;
import com.plagchain.database.service.PublishedWorkService;
import com.plagchain.database.service.UnpublishedWorkService;
import com.plagchain.domain.ResponseItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by Jagrut on 22-05-2017.
 * Service class which will be used by the REST to transfer the task of plagiarism detection.
 */
@Service
public class PlagDetectionService {
    private final Logger log = LoggerFactory.getLogger(PlagDetectionService.class);

    @Value("${plagdetection.streamname.publishedwork}")
    private String publishedWorkStreamName;

    @Value("${plagdetection.streamname.unpublishedwork}")
    private String unpublishedWorkStreamName;

    @Value("${plagdetection.algorithm.threshold}")
    private double algoThreshold;

    @Inject
    private PublishedWorkService publishedWorkService;

    @Inject
    private UnpublishedWorkService unpublishedWorkService;

    /**
     * Find similar items for test document in the present collection of PublishedWork min hash list in the Blockchain
     * that are confirmed and stored in DB.
     * @param plagCheckDocHash the overall document SHA-256 hash for uniquely identifying the document
     * @param plagCheckMinHashList list of min hash values from text whose signatures are not yet calculated
     * @param plagCheckImageMinHashList list of min hash values from image whose signatures are not yet calculated
     * @param response Object of ResponseItem that should be populated and returned
     * @return {ResponseItem} object after populating with appropriate content
     */
    public ResponseItem runLSHAlgorithmPublishedWork(String plagCheckDocHash, List<Integer> plagCheckMinHashList,
                                                     List<String> plagCheckImageMinHashList, ResponseItem response) {
        log.info("Started Similarity Algorithm for Published work");
        List<PublishedWork> similarPublishedWork = new ArrayList<>();
        List<PublishedWork> similarImagePublishedWork = new ArrayList<>();
        DBCursor dbCursor = publishedWorkService.find();
        while(dbCursor.hasNext()) {
            PublishedWork singleDocumentDB = (PublishedWork) dbCursor.next();
            if(singleDocumentDB.getDocHashKey().equals(plagCheckDocHash)) {
                response.setError("Database already contains the exact document.");
                return response;
            }
            if(plagCheckImageMinHashList.size() > 0)
                if(ImageSimilarityCheck(singleDocumentDB.getImageListMinHash(), plagCheckImageMinHashList))
                    similarImagePublishedWork.add(singleDocumentDB);
            if(MinHashAlgorithm(singleDocumentDB.getListMinHash(), plagCheckMinHashList) > algoThreshold)
                similarPublishedWork.add(singleDocumentDB);
        }
        response.setListOfSimilarImagePublishedWork(similarImagePublishedWork);
        response.setListOfSimilarPublishedWork(similarPublishedWork);
        response.setSuccess("success");
        return response;
    }

    /**
     * Find similar items for test document in the present collection of UnpublishedWork min hash list in the Blockchain
     * that are confirmed and stored in DB.
     * @param plagCheckDocHash the overall document SHA-256 hash for uniquely identifying the document
     * @param plagCheckMinHashList list of min hash values from text whose signatures are not yet calculated
     * @param plagCheckImageMinHashList list of min hash values from image whose signatures are not yet calculated
     * @param response Object of ResponseItem that should be populated and returned
     * @return {ResponseItem} object after populating with appropriate content
     */
    public ResponseItem runLSHAlgorithmUnpublishedWork(String plagCheckDocHash, List<Integer> plagCheckMinHashList,
                                                       List<String> plagCheckImageMinHashList, ResponseItem response) {
        log.info("Started Similarity Algorithm for Unpublished work");
        List<UnpublishedWork> similarUnpublishedWork = new ArrayList<>();
        List<UnpublishedWork> similarImageUnpublishedWork = new ArrayList<>();
        DBCursor dbCursor = unpublishedWorkService.find();
        while(dbCursor.hasNext()) {
            UnpublishedWork singleDocumentDB = (UnpublishedWork) dbCursor.next();
            if(singleDocumentDB.getDocHashKey().equals(plagCheckDocHash)) {
                response.setError("Database already contains the exact document.");
                return response;
            }
            if(plagCheckImageMinHashList.size() > 0)
                if(ImageSimilarityCheck(singleDocumentDB.getImageListMinHash(), plagCheckImageMinHashList))
                    similarImageUnpublishedWork.add(singleDocumentDB);
            if(MinHashAlgorithm(singleDocumentDB.getListMinHash(), plagCheckMinHashList) > algoThreshold)
                similarUnpublishedWork.add(singleDocumentDB);
        }
        response.setListOfSimilarImageUnpublishedWork(similarImageUnpublishedWork);
        response.setListOfSimilarUnpublishedWork(similarUnpublishedWork);
        response.setSuccess("success");
        return response;
    }

    /**
     * Simple Min Hash algorithm without performing LSH. This would work, but it is not efficient.
     * @param minHashListDB the list of min hash from DB to which the test document should be compared for similarity
     * @param plagCheckMinHashList the list of min hash from test document
     * @return {double} Similarity score between 0-1
     */
    public double MinHashAlgorithm(List<Integer> minHashListDB, List<Integer> plagCheckMinHashList) {
        log.info("Comparing MinHash for similarity");
        if(minHashListDB.size() != plagCheckMinHashList.size()) {
            log.error("Size of signatures should be the same");
            return 0.0D;
        } else {
            double sim = 0.0D;

            for(int i = 0; i < minHashListDB.size(); ++i) {
                if(minHashListDB.get(i).intValue() == plagCheckMinHashList.get(i).intValue()) {
                    ++sim;
                }
            }

            return sim / (double)minHashListDB.size();
        }
    }

    /**
     * Check for image similarity for image hashes. The image hashes are SHA-256 hash.
     * @param imageMinHashListDB the list of image hash from DB
     * @param plagCheckImageMinHashList the list of image hash from the test document
     * @return {boolean} if DB contains any SHA-256 hash for the image hash in test document
     */
    public boolean ImageSimilarityCheck(List<String> imageMinHashListDB, List<String> plagCheckImageMinHashList) {
        log.info("Checking for image similarity");
        for(String singlePlagCheckImageHash: plagCheckImageMinHashList) {
            for(String singleDBImageHash: imageMinHashListDB) {
                if (singlePlagCheckImageHash.equals(singleDBImageHash))
                    return true;
            }
        }
        return false;
    }
}
