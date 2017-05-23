package com.plagchain.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    @Field("plagchain_seed_hash")
    private String plagchainSeedHash;

    @Field("plagchain_seed")
    private String plagchainSeed;

    @Field("originstamp_seed")
    private String originstampSeed;

    @Field("originstamp_confirmed")
    private boolean originstampConfirmed;

    @Field("originstamp_bitcoin_submit_time")
    private String originstampBitcoinSubmitTime;

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

    public String getOriginstampSeed() {
        return originstampSeed;
    }

    public void setOriginstampSeed(String originstampSeed) {
        this.originstampSeed = originstampSeed;
    }

    public boolean isOriginstampConfirmed() {
        return originstampConfirmed;
    }

    public void setOriginstampConfirmed(boolean originstampConfirmed) {
        this.originstampConfirmed = originstampConfirmed;
    }

    public String getOriginstampBitcoinSubmitTime() {
        return originstampBitcoinSubmitTime;
    }

    public void setOriginstampBitcoinSubmitTime(String originstampBitcoinSubmitTime) {
        this.originstampBitcoinSubmitTime = originstampBitcoinSubmitTime;
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
        return Objects.equals(this.plagchainSeedHash, seedSubmission.getPlagchainSeedHash());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.plagchainSeedHash);
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this, this.getClass());
    }
}
