package com.plagchain.domain;

import java.util.List;

/**
 * Created by Jagrut on 22-05-2017.
 * For parsing the data field from a stream
 */
public class ChainData {
    private List<String> textMinHash;
    private List<String> imageHash;
    private String contactInfo;

    public List<String> getTextMinHash() {
        return textMinHash;
    }

    public void setTextMinHash(List<String> textMinHash) {
        this.textMinHash = textMinHash;
    }

    public List<String> getImageHash() {
        return imageHash;
    }

    public void setImageHash(List<String> imageHash) {
        this.imageHash = imageHash;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
