package com.plagchain.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.plagchain.database.dbobjects.PublishedWork;
import com.plagchain.database.dbobjects.SeedSubmission;
import com.plagchain.database.dbobjects.UnpublishedWork;

import java.util.List;
import java.util.Map;

/**
 * Created by Jagrut on 29-05-2017.
 * The Java object that needs to be returned for every REST request.
 * Populate appropriate classes.
 */
public class ResponseItem {
    private String error;
    private String success;
    private SeedSubmission seedDetails;
    private List<SimilarDocument> listOfSimilarPublishedWork;
    private List<PublishedWork> listOfSimilarImagePublishedWork;
    private List<SimilarDocument> listOfSimilarUnpublishedWork;
    private List<UnpublishedWork> listOfSimilarImageUnpublishedWork;

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

    public List<SimilarDocument> getListOfSimilarPublishedWork() {
        return listOfSimilarPublishedWork;
    }

    public void setListOfSimilarPublishedWork(List<SimilarDocument> listOfSimilarPublishedWork) {
        this.listOfSimilarPublishedWork = listOfSimilarPublishedWork;
    }

    public List<SimilarDocument> getListOfSimilarUnpublishedWork() {
        return listOfSimilarUnpublishedWork;
    }

    public void setListOfSimilarUnpublishedWork(List<SimilarDocument> listOfSimilarUnpublishedWork) {
        this.listOfSimilarUnpublishedWork = listOfSimilarUnpublishedWork;
    }

    public List<PublishedWork> getListOfSimilarImagePublishedWork() {
        return listOfSimilarImagePublishedWork;
    }

    public void setListOfSimilarImagePublishedWork(List<PublishedWork> listOfSimilarImagePublishedWork) {
        this.listOfSimilarImagePublishedWork = listOfSimilarImagePublishedWork;
    }

    public List<UnpublishedWork> getListOfSimilarImageUnpublishedWork() {
        return listOfSimilarImageUnpublishedWork;
    }

    public void setListOfSimilarImageUnpublishedWork(List<UnpublishedWork> listOfSimilarImageUnpublishedWork) {
        this.listOfSimilarImageUnpublishedWork = listOfSimilarImageUnpublishedWork;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this, this.getClass());
    }
}
