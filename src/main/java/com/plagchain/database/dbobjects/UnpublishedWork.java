package com.plagchain.database.dbobjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Jagrut on 18-05-2017.
 * Database object fo unpublished work content.
 */

@Document(collection = "unpublished_work")
public class UnpublishedWork implements Serializable {
    @NotNull
    @Field("doc_hash")
    private String docHashKey;

    @NotNull
    @Field("list_minHash")
    private List<String> listMinHash;

    @Field("image_list_minHash")
    private List<String> imageListMinHash;

    @Field("publisher_address")
    private String publisherAddress;

    @Field("timestamp")
    private String timestamp;

    @Field("contact_info")
    private String contactInfo;

    @Field("submitted_originstamp")
    private boolean submittedToOriginstamp;

    public String getDocHashKey() {
        return docHashKey;
    }

    public void setDocHashKey(String docHashKey) {
        this.docHashKey = docHashKey;
    }

    public List<String> getListMinHash() {
        return listMinHash;
    }

    public void setListMinHash(List<String> listMinHash) {
        this.listMinHash = listMinHash;
    }

    public List<String> getImageListMinHash() {
        return imageListMinHash;
    }

    public void setImageListMinHash(List<String> imageListMinHash) {
        this.imageListMinHash = imageListMinHash;
    }

    public String getPublisherAddress() {
        return publisherAddress;
    }

    public void setPublisherAddress(String publisherAddress) {
        this.publisherAddress = publisherAddress;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSubmittedToOriginstamp() {
        return submittedToOriginstamp;
    }

    public void setSubmittedToOriginstamp(boolean submittedToOriginstamp) {
        this.submittedToOriginstamp = submittedToOriginstamp;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UnpublishedWork unpublishedWork = (UnpublishedWork) o;
        return Objects.equals(this.docHashKey, unpublishedWork.docHashKey);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.docHashKey);
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this, this.getClass());
    }
}