package com.plagchain.domain;

import java.util.List;

/**
 * Created by Jagrut on 22-05-2017.
 * For parsing the data field from a stream
 */
public class ChainData {
    private String fileType;
    private List<String> hashData;
    private String contactInfo;
    private int orderIndex;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public List<String> getHashData() {
        return hashData;
    }

    public void setHashData(List<String> hashData) {
        this.hashData = hashData;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public int getOrder() {
        return orderIndex;
    }

    public void setOrder(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}
