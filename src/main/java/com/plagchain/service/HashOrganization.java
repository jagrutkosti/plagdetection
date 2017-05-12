package com.plagchain.service;

import com.plagchain.domain.PublishedWork;
import multichain.command.*;
import multichain.object.Stream;
import multichain.object.StreamItem;
import multichain.object.StreamKeyPublisherInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Inject
    private PublishedWorkService publishedWorkService;

    /**
     * Run this method after every $time milliseconds mentioned in application.properties file
     */
    @Scheduled(fixedRateString = "${plagdetection.hash.organize.timeinterval}")
    private void startOrganization() {
        log.info("Organization of hashes started.");
        ChainCommand.initializeChain(chainName);
        organizePublishedWork();
    }

    /**
     * Responsible for organizing the hashes for "publishedwork" stream
     */
    private void organizePublishedWork() {
        //if not subscribed already, subscribe to the stream
        subscibeToStream(publishedWorkStreamName);
        //get all items from database
        List<PublishedWork> allItemsInDb = publishedWorkService.findAll();
        //get all keys from block chain
        List<StreamKeyPublisherInfo> allKeysFromPublishedworkStream = allKeysInStream(publishedWorkStreamName);
        System.out.println(allKeysFromPublishedworkStream.size());
        if(allKeysFromPublishedworkStream != null && allKeysFromPublishedworkStream.size() > 0) {
            //filter the keys which are confirmed
            allKeysFromPublishedworkStream = allKeysFromPublishedworkStream.stream()
                .filter(chainItem -> chainItem.getConfirmed() > 0)
                .collect(Collectors.toList());
            System.out.println(allKeysFromPublishedworkStream.size());
            //choose the keys that are not already in the database
            List<StreamKeyPublisherInfo> newItems = allKeysFromPublishedworkStream.stream()
                    .filter(chainItem -> !allItemsInDb.stream().anyMatch(dbItem -> dbItem.getDocHashKey().equalsIgnoreCase(chainItem.getKey())))
                    .collect(Collectors.toList());
            //iterate over all keys not in DB
            for(StreamKeyPublisherInfo singleItem : newItems) {
                System.out.println(singleItem.getKey());
                //get all items associated with this key from this particular stream
                List<StreamItem> addToDatabase = allItemsForKey(publishedWorkStreamName, singleItem.getKey());
                //add relevant info to POJO and save the POJO in DB
                PublishedWork dbPutItem = new PublishedWork();
                dbPutItem.setDocHashKey(singleItem.getKey());
                dbPutItem.setPublisherAddress(addToDatabase.get(0).getPublishers().get(0));
                dbPutItem.setTimestamp(addToDatabase.get(0).getTime().toString());
                List<String> minHashList = new ArrayList<>();
                for(StreamItem minHash : addToDatabase) {
                    minHashList.add(minHash.getData());
                }
                dbPutItem.setListMinHash(minHashList);
                publishedWorkService.save(dbPutItem);
            }
        }
    }

    /**
     * Generic method to subsribe to a stream
     * @param streamName the name of the stream to subscribe to
     */
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

    /**
     * Generic method to get all keys in the particular stream
     * @param streamName the name of the stream from which to get list of all keys
     * @return {List} of all keys in Java Object with other metadata as well
     */
    private List<StreamKeyPublisherInfo> allKeysInStream(String streamName) {
        try {
            return StreamCommand.listStreamKeys(streamName);
        } catch (MultichainException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generic method to get all items related to a key from given stream
     * @param streamName the name of the stream to look into
     * @param key the key for which all item needs to be fetched
     * @return {List} of all items associated with the given key
     */
    private List<StreamItem> allItemsForKey(String streamName, String key) {
        try {
            return StreamCommand.listStreamKeyItems(streamName, key, "true");
        } catch (MultichainException e) {
            e.printStackTrace();
            return null;
        }
    }


}
