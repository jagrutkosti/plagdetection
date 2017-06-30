package com.plagchain.domain;

/**
 * Created by Jagrut on 28-06-2017.
 * Structure to store details of similar document
 */
public class SimilarDocument {
    private String fileName;
    private String documentHash;
    private String publisherWalletAddress;
    private String contactInfo;
    private String timestamp;
    private double similarityScore;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDocumentHash() {
        return documentHash;
    }

    public void setDocumentHash(String documentHash) {
        this.documentHash = documentHash;
    }

    public String getPublisherWalletAddress() {
        return publisherWalletAddress;
    }

    public void setPublisherWalletAddress(String publisherWalletAddress) {
        this.publisherWalletAddress = publisherWalletAddress;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(double similarityScore) {
        this.similarityScore = similarityScore;
    }
}
