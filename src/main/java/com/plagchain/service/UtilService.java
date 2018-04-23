package com.plagchain.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.plagchain.Constants;
import com.plagchain.domain.ChainData;
import multichain.command.MultichainException;
import multichain.command.StreamCommand;
import multichain.object.StreamItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by Jagrut on 06-06-2017.
 * A utility service that handles all generic requests for this module
 */

@Service
public class UtilService {

    private final Logger log = LoggerFactory.getLogger(UtilService.class);
    private final int[] randomNumbers = new int[Constants.NUMBER_OF_RANDOM_NUMBERS];

    /**
     * !!!DO NOT MODIFY random-number.txt FILE EVER, after deployment!!!
     *
     * Initialize random numbers from file. The random numbers used should always be the same for all documents for
     * MinHash algorithm to work. So, if you delete the random-number.txt file, you need to re-index all documents.
     *
     * Read the random number file and populate the randomNumbers int array.
     * @throws IOException if error while fetching or reading file
     */
    @PostConstruct
    public void populateRandomNumbers() {
        log.info("Populating random numbers from file");
        List<String> randomNumAsString;
        try {
            randomNumAsString = IOUtils.readLines(this.getClass().getResourceAsStream(Constants.RANDOM_NUMBERS_FILE), "UTF-8");
            int i = 0;
            for(String number : randomNumAsString) {
                randomNumbers[i] = Integer.parseInt(number);
                i++;
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extracts text from Multipart file using iText library
     * Works only for pdf files. Check for file extension where calling this method.
     * @param pdfFile the multipart file from the user
     * @return {String} Extracted text from the PDF file
     */
    public String parsePdf(MultipartFile pdfFile) {
        log.info("Extracting text from pdf: {}", pdfFile.getOriginalFilename());
        StringJoiner extractedText = new StringJoiner(" ");
        try {
            PdfReader reader = new PdfReader(pdfFile.getInputStream());
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            TextExtractionStrategy strategy;
            for(int i = 1; i <= reader.getNumberOfPages(); i++) {

                strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
                extractedText.add(strategy.getResultantText());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return extractedText.toString();
    }

    /**
     * To extract text from file based on file extension
     * @param multipartFile the multipartfile received from user
     * @return
     */
    public String getTextFromDoc(MultipartFile multipartFile) {
        if(multipartFile.getOriginalFilename().endsWith(".pdf"))
            return parsePdf(multipartFile);
        else
            try {
                return new String(multipartFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
    }

    /**
     * Removes the hyphens added by the iText library. Removes any new line character.
     * Convert all characters to lowercase. Removes all punctuations.
     * @param uncleanText the text that needs to be cleaned
     * @return {List<String>} containing single sentence as whole text.
     */
    public String cleanText(String uncleanText) {
        log.info("Cleaning text.");
        //For text extracted from pdf has unnecessary line breaks and hyphen(when line break occurs in words)
        uncleanText = uncleanText.replaceAll("-\n","");
        //For removing all line breaks
        uncleanText = uncleanText.replaceAll("[\n]"," ");
        //For removing all punctuations
        uncleanText = uncleanText.replaceAll("\\p{P}", "");
        //Convert everything to lowercase
        uncleanText = uncleanText.toLowerCase();
        return uncleanText;
    }

    /**
     * Remove all white spaces as identified by Java
     * @param stringWithWhiteSpaces string containing white spaces
     * @return String without any white space
     */
    public String removeAllWhiteSpaces(String stringWithWhiteSpaces) {
        return StringUtils.deleteWhitespace(stringWithWhiteSpaces);
    }

    /**
     * Create character shingles of specified length from a sentence.
     * @param shingleLength the fixed length of all shingles
     * @param cleanedText the sentence from which shingles need to be extracted
     * @return {List<String>} list containing word shingles from the sentence
     */
    public Set<String> createShingles(int shingleLength, String cleanedText) {
        log.info("Creating shingles of length: {}", shingleLength);
        Set<String> shinglesFromSentences = new HashSet<>();
        for(int i = 0; i < cleanedText.length() - shingleLength + 1; i++) {
            shinglesFromSentences.add(cleanedText.substring(i, i + shingleLength));
        }
        return shinglesFromSentences;
    }

    /**
     * Generates MinHash signature from the given document's shingles. Initial hash value is of Java's hashcode.
     * After that, that hashValue is XORed with random numbers fetched from the files.
     * @param docShingles all shingles created from the document/text
     * @return {int[]} array of MinHash values for the document/text
     */
    public int[] generateMinHashSignature(List<String> docShingles) {
        log.info("Generating MinHash from shingles");
        int[] minHash = new int[Constants.NUMBER_OF_RANDOM_NUMBERS + 1];

        int[] hashcodes = new int[docShingles.size()];
        for(int i = 0; i < docShingles.size(); i++) {
            hashcodes[i] = docShingles.get(i).hashCode();
        }
        minHash[0] = Collections.min(Arrays.asList(ArrayUtils.toObject(hashcodes)));

        for(int  i = 1; i <= Constants.NUMBER_OF_RANDOM_NUMBERS; i++) {
            int[] tempAllHashValues = new int[docShingles.size()];
            for(int j = 0; j < docShingles.size(); j++) {
                tempAllHashValues[j] = hashcodes[j] ^ randomNumbers[i - 1];
            }
            minHash[i] = Collections.min(Arrays.asList(ArrayUtils.toObject(tempAllHashValues)));
        }
        return minHash;
    }

    /**
     * Creates list of SHA256 hash from list of objects. The objects are first serialized into byte array and then the
     * hash is generated. Used as a generic method for generating SHA256 hash
     * @param listOfObjects list of java objects to generate hash for.
     * @return List of SHA256 hash as string
     */
    public List<String> generateSHA256HashFromObjects(List<byte[]> listOfObjects) {
        log.info("Generating SHA-256 hash.");
        List<String> sha256Hashes = new ArrayList<>();

        for(byte[] object : listOfObjects) {
            StringBuilder seedHash = new StringBuilder();
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                messageDigest.update(object);
                byte[] messageDigestBytes = messageDigest.digest();
                for(byte singleByte : messageDigestBytes) {
                    String hex = Integer.toHexString(0xFF & singleByte);
                    if(hex.length() == 1)
                        seedHash.append("0");
                    seedHash.append(hex);
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            sha256Hashes.add(seedHash.toString());
        }
        return sha256Hashes;
    }

    /**
     * Initializes the chain and submits the transaction into plagchain with document sha256 hash as key and  hexdata as
     * value (derived from ChainData)
     * @param walletAddress the wallet address of the logged in user
     * @param streamName the name of the stream to publish to
     * @param keyAsDocHash the sha256 hash of the whole document
     * @param hexData the data to be submitted in hex string format
     * @return {String} Transaction id
     */
    public String submitToPlagchainFrom(String walletAddress, String streamName, String keyAsDocHash, String hexData) {
        log.info("Submitting data to plagchain");
        String response = null;
        try {
            List<StreamItem> alreadyExistingKeys = StreamCommand.listStreamKeyItems(streamName, keyAsDocHash);
            if(alreadyExistingKeys != null && alreadyExistingKeys.size() > 0)
                return "Document already exists in " + streamName;
            response = StreamCommand.publishFromStream(walletAddress, streamName, keyAsDocHash, hexData);
            log.info("Transaction response: {}", response);
        } catch (MultichainException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Transforms the hashes and contact information to JSON format as required by the plagchain and then to
     * hex string.
     * @param minHashList the list of integers representing the minhash of a document
     * @param contactInfo the contact info as string
     * @return String data transformed into Hex String representation
     */
    public String formatDataToHex(String fileName, List<Integer> minHashList, String contactInfo, String walletAddress) {
        log.info("Formatting data to Hex string");
        ChainData chainData = new ChainData();
        if(fileName != null && fileName.length() > 0)
            chainData.setFileName(fileName);
        if(minHashList != null && minHashList.size() > 0)
            chainData.setTextMinHash(minHashList);
        if(contactInfo != null && contactInfo.length() > 0)
            chainData.setContactInfo(contactInfo);
        if(walletAddress != null && walletAddress.length() > 0)
            chainData.setPublisherWalletAddress(walletAddress);

        Gson gson = new GsonBuilder().create();
        return DatatypeConverter.printHexBinary(gson.toJson(chainData, ChainData.class).getBytes());
    }

    /**
     * Submits the transaction into plagchain as key pair in the mentioned stream.
     * @param streamName the name of the stream to publish to
     * @param key the key
     * @param hexData the data to be submitted in hex string format
     * @return {String} Transaction id
     */
    public String submitToPlagchain(String streamName, String key, String hexData) {
        log.info("Submitting data to plagchain");
        String response = null;
        try {
            response = StreamCommand.publishStream(streamName, key, hexData);
            log.info("Transaction response: {}", response);
        } catch (MultichainException e) {
            e.printStackTrace();
        }
        return response;
    }
}
