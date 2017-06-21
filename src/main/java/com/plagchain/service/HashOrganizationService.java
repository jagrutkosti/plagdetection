package com.plagchain.service;

import com.google.gson.Gson;
import com.plagchain.database.service.PublishedWorkService;
import com.plagchain.database.service.UnpublishedWorkService;
import com.plagchain.domain.ChainData;
import com.plagchain.database.dbobjects.PublishedWork;
import com.plagchain.database.dbobjects.UnpublishedWork;
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
import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jagrut on 11-05-2017.
 * Organize and store the hash from plagchain to the Database.
 * Runs every $time hour, mentioned in application.properties file.
 */

@Component
public class HashOrganizationService {
    private final Logger log = LoggerFactory.getLogger(HashOrganizationService.class);

    @Value("${plagdetection.chainname}")
    private String chainName;

    @Value("${plagdetection.streamname.publishedwork}")
    private String publishedWorkStreamName;

    @Value("${plagdetection.streamname.unpublishedwork}")
    private String unpublishedWorkStreamName;

    private PublishedWorkService publishedWorkService;
    private UnpublishedWorkService unpublishedWorkService;

    public HashOrganizationService(PublishedWorkService publishedWorkService, UnpublishedWorkService unpublishedWorkService) {
        this.publishedWorkService = publishedWorkService;
        this.unpublishedWorkService = unpublishedWorkService;
    }
    /**
     * Run this method after every $time milliseconds mentioned in application.properties file
     */
    @Scheduled(fixedRateString = "${plagdetection.hash.organize.timeinterval}")
    private void startOrganization() {
        log.info("Organization of hashes started.");
        ChainCommand.initializeChain(chainName);

        log.info("Organization of PublishedWork hashes started.");
        organizePublishedWork();
        log.info("Organization of PublishedWork hashes finished.");

        log.info("Organization of UnpublishedWork hashes started.");
        organizeUnpublishedWork();
        log.info("Organization of UnpublishedWork hashes finished.");

        log.info("Organization of hashes finished.");
    }

    /**
     * Responsible for organizing the hashes for "publishedWork" stream
     */
    private void organizePublishedWork() {
        //if not subscribed already, subscribe to the stream
        subscibeToStream(publishedWorkStreamName);
        //get all items from database
        List<PublishedWork> allItemsInDb = publishedWorkService.findAll();
        //get all keys from block chain
        List<StreamKeyPublisherInfo> allKeysFromPublishedworkStream = allKeysInStream(publishedWorkStreamName);

        if(allKeysFromPublishedworkStream != null && allKeysFromPublishedworkStream.size() > 0) {
            //filter the keys which are confirmed in plagchain
            allKeysFromPublishedworkStream = filterChainKeys(allKeysFromPublishedworkStream);

            //choose the keys that are not already in the database
            List<StreamKeyPublisherInfo> newItems = allKeysFromPublishedworkStream.stream()
                    .filter(chainItem -> !allItemsInDb.stream().anyMatch(dbItem -> dbItem.getDocHashKey().equalsIgnoreCase(chainItem.getKey())))
                    .collect(Collectors.toList());
            //iterate over all keys not in DB
            for(StreamKeyPublisherInfo singleItem : newItems) {
                //get all items associated with this key from this particular stream
                List<StreamItem> addToDatabase = allItemsForKey(publishedWorkStreamName, singleItem.getKey());
                //add relevant info to POJO and save the POJO in DB
                if(addToDatabase != null && addToDatabase.size() > 0) {
                    PublishedWork dbPutItem = new PublishedWork();
                    dbPutItem.setDocHashKey(singleItem.getKey());
                    dbPutItem.setPublisherAddress(addToDatabase.get(0).getPublishers().get(0));
                    dbPutItem.setTimestamp(((Long)addToDatabase.get(0).getBlocktime()).toString());

                    dbPutItem = (PublishedWork) addMinHashToDbObject(addToDatabase, dbPutItem);
                    publishedWorkService.save(dbPutItem);
                }
            }
        }
    }

    /**
     * Responsible for organizing the hashes for "unpublishedWork" stream
     */
    private void organizeUnpublishedWork() {
        //if not subscribed already, subscribe to the stream
        subscibeToStream(unpublishedWorkStreamName);
        //get all items from database
        List<UnpublishedWork> allItemsInDb = unpublishedWorkService.findAll();
        //get all keys from block chain
        List<StreamKeyPublisherInfo> allKeysFromUnpublishedworkStream = allKeysInStream(unpublishedWorkStreamName);

        if(allKeysFromUnpublishedworkStream != null && allKeysFromUnpublishedworkStream.size() > 0) {
            //filter the keys which are confirmed in plagchain
            allKeysFromUnpublishedworkStream = filterChainKeys(allKeysFromUnpublishedworkStream);

            //choose the keys that are not already in the database
            List<StreamKeyPublisherInfo> newItems = allKeysFromUnpublishedworkStream.stream()
                    .filter(chainItem -> !allItemsInDb.stream().anyMatch(dbItem -> dbItem.getDocHashKey().equalsIgnoreCase(chainItem.getKey())))
                    .collect(Collectors.toList());
            //iterate over all keys not in DB
            for(StreamKeyPublisherInfo singleItem : newItems) {
                //get all items associated with this key from this particular stream
                List<StreamItem> addToDatabase = allItemsForKey(unpublishedWorkStreamName, singleItem.getKey());
                //add relevant info to POJO and save the POJO in DB
                if(addToDatabase != null && addToDatabase.size() > 0) {
                    UnpublishedWork dbPutItem = new UnpublishedWork();
                    dbPutItem.setDocHashKey(singleItem.getKey());
                    dbPutItem.setPublisherAddress(addToDatabase.get(0).getPublishers().get(0));
                    dbPutItem.setTimestamp(((Long)addToDatabase.get(0).getBlocktime()).toString());

                    dbPutItem = (UnpublishedWork) addMinHashToDbObject(addToDatabase, dbPutItem);
                    unpublishedWorkService.save(dbPutItem);
                }
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

    /**
     * Filter out the keys which are not confirmed and return only the confirmed keys
     * @param allKeysFromStream all keys from the stream
     * @return {List<StreamKeyPublisherInfo>} Filtered list of stream keys
     */
    private List<StreamKeyPublisherInfo> filterChainKeys(List<StreamKeyPublisherInfo> allKeysFromStream) {
        return allKeysFromStream.stream()
                .filter(chainItem -> chainItem.getConfirmed() > 0)
                .collect(Collectors.toList());
    }

    /**
     * Extracts data from list of stream items that are to be added in database and updates the DB object with
     * min hash of text items or image items.
     * @param addToDatabase list of stream item to be added to database
     * @param dbObject the DB object to be updated with min hashes
     * @return {Object} w.r.t dbObject after updating with min hashes
     */
    private Object addMinHashToDbObject(List<StreamItem> addToDatabase, Object dbObject) {
        log.info("Adding MinHash from Blockchain stream item to DB object");
        List<Integer> minHashList = new ArrayList<>();
        List<String> imageMinHashList = new ArrayList<>();
        String contactInfo = "";
        for (StreamItem minHash : addToDatabase) {
            try {
                ChainData chainData = transformDataFromHexToObject(minHash.getData());
                if(chainData.getTextMinHash() != null)
                    minHashList.addAll(chainData.getTextMinHash());
                if(chainData.getImageHash() != null)
                    imageMinHashList.addAll(chainData.getImageHash());
                if(chainData.getContactInfo() != null)
                    contactInfo = chainData.getContactInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(dbObject instanceof PublishedWork) {
            PublishedWork publishedWorkObject = (PublishedWork) dbObject;
            publishedWorkObject.setListMinHash(minHashList);
            publishedWorkObject.setImageListMinHash(imageMinHashList);
            publishedWorkObject.setContactInfo(contactInfo);
            return  publishedWorkObject;
        } else {
            UnpublishedWork unpublishedWorkObject = (UnpublishedWork) dbObject;
            unpublishedWorkObject.setListMinHash(minHashList);
            unpublishedWorkObject.setImageListMinHash(imageMinHashList);
            unpublishedWorkObject.setContactInfo(contactInfo);
            return unpublishedWorkObject;
        }
    }

    /**
     * Transforms the data from Hex-String to ChainData object
     * @param dataInHex the data in the form of hexadecimal string
     * @return {ChainData} object containing relevant information
     */
    private ChainData transformDataFromHexToObject(String dataInHex) {
        log.info("Transforming hex data: {}", dataInHex);
        String dataInString = new String(DatatypeConverter.parseHexBinary(dataInHex));
        Gson gson = new Gson();
        return gson.fromJson(dataInString,ChainData.class);
    }
}
