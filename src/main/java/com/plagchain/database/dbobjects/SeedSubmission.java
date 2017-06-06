package com.plagchain.database.dbobjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Jagrut on 22-05-2017.
 * Database object to hold seed data submitted to Originstamp server.
 */
@Document(collection = "seed_submission")
public class SeedSubmission implements Serializable{

    @Id
    private String id;

    @Field("plagchain_seed_hash")
    private String plagchainSeedHash;

    @Field("plagchain_seed")
    @TextIndexed
    private String plagchainSeed;

    @Field("publishedwork")
    private boolean publishedWork;

    @Field("originstamp_seed_address")
    private String originstampBtcAddress;

    @Field("originstamp_seed")
    private String originstampSeed;

    @Field("originstamp_confirmed")
    private int originstampConfirmed = 0;

    @Field("originstamp_bitcoin_confirm_time")
    private String originstampBitcoinConfirmTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlagchainSeed() {
        return plagchainSeed;
    }

    public void setPlagchainSeed(String plagchainSeed) {
        this.plagchainSeed = plagchainSeed;
    }

    public String getPlagchainSeedHash() {
        return plagchainSeedHash;
    }

    public void setPlagchainSeedHash(String plagchainSeedHash) {
        this.plagchainSeedHash = plagchainSeedHash;
    }

    public boolean isPublishedWork() {
        return publishedWork;
    }

    public void setPublishedWork(boolean publishedWork) {
        this.publishedWork = publishedWork;
    }

    public String getOriginstampBtcAddress() {
        return originstampBtcAddress;
    }

    public void setOriginstampBtcAddress(String originstampBtcAddress) {
        this.originstampBtcAddress = originstampBtcAddress;
    }

    public String getOriginstampSeed() {
        return originstampSeed;
    }

    public void setOriginstampSeed(String originstampSeed) {
        this.originstampSeed = originstampSeed;
    }

    public int getOriginstampConfirmed() {
        return originstampConfirmed;
    }

    public void setOriginstampConfirmed(int originstampConfirmed) {
        this.originstampConfirmed = originstampConfirmed;
    }

    public String getOriginstampBitcoinConfirmTime() {
        return originstampBitcoinConfirmTime;
    }

    public void setOriginstampBitcoinConfirmTime(String originstampBitcoinConfirmTime) {
        this.originstampBitcoinConfirmTime = originstampBitcoinConfirmTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SeedSubmission seedSubmission = (SeedSubmission) o;
        return Objects.equals(this.id, seedSubmission.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this, this.getClass());
    }
}
