package com.plagchain.database.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.plagchain.Constants;
import com.plagchain.database.dbobjects.MinHashFeatures;
import com.plagchain.database.repository.MinHashFeaturesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Jagrut on 27-04-2017.
 * Service to manage MinHashFeatures Database tasks
 */
@Service
public class MinHashFeaturesService {

    private final Logger log = LoggerFactory.getLogger(MinHashFeaturesService.class);

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Inject
    private MinHashFeaturesRepository minHashFeaturesRepository;

    /**
     * Save a minHashFeatures.
     * @return the persisted entity
     */
    public MinHashFeatures save(MinHashFeatures minHashFeatures) {
        log.info("Request to save MinHashFeatures : {}", minHashFeatures);
        MinHashFeatures result = minHashFeaturesRepository.save(minHashFeatures);
        return result;
    }

    /**
     *  get all the publishedWorks.
     *  @return the list of entities
     */
    public List<MinHashFeatures> findAll() {
        log.info("Request to get all PublishedWorks");
        List<MinHashFeatures> result = minHashFeaturesRepository.findAll();
        return result;
    }

    /**
     *  get one publishedWork by id.
     *  @return the entity
     */
    public MinHashFeatures findOne(String id) {
        log.info("Request to get MinHashFeatures : {}", id);
        MinHashFeatures minHashFeatures = minHashFeaturesRepository.findOne(id);
        return minHashFeatures;
    }

    /**
     *  delete the  publishedWork by id.
     */
    public void delete(String id) {
        log.info("Request to delete MinHashFeatures : {}", id);
        minHashFeaturesRepository.delete(id);
    }

    /**
     * delete ALL publishedWork in Database.
     */
    public void deleteAll(){
        log.info("Request to delete all PublishedWorks");
        minHashFeaturesRepository.deleteAll();
    }

    /**
     * Use only for fetching very large number of documents.
     * Get all documents from MinHashFeatures collection.
     * Using MongoTemplate and DBCursor to iterate over millions of records.
     * @return {DBCursor} to iterate over all documents in the collection
     */
    public DBCursor find() {
        log.info("Request to get all PublishedWorks with DBCursor");
        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), databaseName));
        DBCollection dbCollection = mongoTemplate.getCollection(Constants.MIN_HASH_DB_NAME);
        return dbCollection.find();
    }

    /**
     * Use only for fetching very large number of documents.
     * Get all documents from MinHashFeatures collection which satisfy the given criteria.
     * Using MongoTemplate and DBCursor to iterate over millions of records.
     * @return {DBCursor} to iterate over all documents in the collection
     */
    public DBCursor find(BasicDBObject query) {
        log.info("Request to get DBCurosr for all PublishedWorks for given query");
        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), databaseName));
        DBCollection dbCollection = mongoTemplate.getCollection(Constants.MIN_HASH_DB_NAME);
        return dbCollection.find(query);
    }
}
