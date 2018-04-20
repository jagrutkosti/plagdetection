package com.plagchain.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.plagchain.database.dbobjects.MinHashFeatures;
import com.plagchain.database.dbobjects.SeedSubmission;

import java.util.List;

/**
 * Created by Jagrut on 29-05-2017.
 * The Java object that needs to be returned for every REST request.
 * Populate appropriate classes.
 */
public class ResponseItem {
    private String error;
    private String success;
    private SeedSubmission seedDetails;
    private List<SimilarDocument> listOfSimilarDocuments;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public SeedSubmission getSeedDetails() {
        return seedDetails;
    }

    public void setSeedDetails(SeedSubmission seedDetails) {
        this.seedDetails = seedDetails;
    }

    public List<SimilarDocument> getListOfSimilarDocuments() {
        return listOfSimilarDocuments;
    }

    public void setListOfSimilarDocuments(List<SimilarDocument> listOfSimilarDocuments) {
        this.listOfSimilarDocuments = listOfSimilarDocuments;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this, this.getClass());
    }
}
