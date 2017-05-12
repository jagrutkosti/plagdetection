package com.plagchain.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Jagrut on 27-04-2017.
 * Database object for Published Work content
 */
@Document(collection = "published_work")
public class PublishedWork implements Serializable {

    @Id
    private String id;

    @NotNull
    @Field("doc_hash")
    private String docHashKey;

    @NotNull
    @Field("list_minHash")
    private List<String> listMinHash;

    @Field("publisher_address")
    private String publisherAddress;

    @Field("timestamp")
    private String timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocHashKey() {
        return docHashKey;
    }

    public void setDocHashKey(String docHash) {
        this.docHashKey = docHash;
    }

    public List<String> getListMinHash() {
        return listMinHash;
    }

    public void setListMinHash(List<String> listMinHash) {
        this.listMinHash = listMinHash;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PublishedWork publishedWork = (PublishedWork) o;
        return Objects.equals(this.id, publishedWork.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    @Override
    public String toString() {
        return "PublishedWork{" +
                "id='" + id + '\'' +
                ", docHashKey='" + docHashKey + '\'' +
                ", listMinHash=" + listMinHash +
                ", publisherAddress='" + publisherAddress + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
