package com.plagchain.service;

import multichain.command.*;
import multichain.object.Stream;
import multichain.object.StreamKeyPublisherInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Jagrut on 11-05-2017.
 * Organize and store the hash from plagchain to the Database.
 * Runs every $time hour, mentioned in application.properties file.
 */

@Component
public class HashOrganization {
    private final Logger log = LoggerFactory.getLogger(HashOrganization.class);

    @Value("${plagdetection.chainname}")
    private String chainName;

    @Value("${plagdetection.streamname.publishedwork}")
    private String publishedWorkStreamName;

    @Value("${plagdetection.streamname.unpublishedwork}")
    private String unpublishedWorkStreamName;

    @Scheduled(fixedRateString = "${plagdetection.hash.organize.timeinterval}")
    private void startOrganization() {
        log.info("Organization of hashes started.");
        ChainCommand.initializeChain(chainName);
        organizeUnPublishedWork();
    }

    private void organizeUnPublishedWork() {
        subscibeToStream(unpublishedWorkStreamName);

        List<StreamKeyPublisherInfo> allKeysFromUnpublishedworkStream = allKeysInStream(unpublishedWorkStreamName);
    }

    private void subscibeToStream(String streamName) {
        try {
            List<Stream> streams = StreamCommand.listStreams(streamName);
            for(Stream stream : streams) {
                if(!stream.isSubscribed())
                    StreamCommand.subscribeAssetsOrStreams(streamName);
            }
        } catch (MultichainException e) {
            e.printStackTrace();
        }
    }

    private List<StreamKeyPublisherInfo> allKeysInStream(String streamName) {
        try {
            return StreamCommand.listStreamKeys(streamName);
        } catch (MultichainException e) {
            e.printStackTrace();
            return null;
        }
    }
}
