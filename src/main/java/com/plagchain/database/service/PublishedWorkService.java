package com.plagchain.database.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.plagchain.database.dbobjects.PublishedWork;
import com.plagchain.database.repository.PublishedWorkRepository;
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
 * Service to manage PublishedWork Database tasks
 */
@Service
public class PublishedWorkService {

    private final Logger log = LoggerFactory.getLogger(PublishedWorkService.class);

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Inject
    private PublishedWorkRepository publishedWorkRepository;

    /**
     * Save a publishedWork.
     * @return the persisted entity
     */
    public PublishedWork save(PublishedWork publishedWork) {
        log.info("Request to save PublishedWork : {}", publishedWork);
        PublishedWork result = publishedWorkRepository.save(publishedWork);
        return result;
    }

    /**
     *  get all the publishedWorks.
     *  @return the list of entities
     */
    public List<PublishedWork> findAll() {
        log.info("Request to get all PublishedWorks");
        List<PublishedWork> result = publishedWorkRepository.findAll();
        return result;
    }

    /**
     *  get one publishedWork by id.
     *  @return the entity
     */
    public PublishedWork findOne(String id) {
        log.info("Request to get PublishedWork : {}", id);
        PublishedWork publishedWork = publishedWorkRepository.findOne(id);
        return publishedWork;
    }

    /**
     *  delete the  publishedWork by id.
     */
    public void delete(String id) {
        log.info("Request to delete PublishedWork : {}", id);
        publishedWorkRepository.delete(id);
    }

    /**
     * delete ALL publishedWork in Database.
     */
    public void deleteAll(){
        log.info("Request to delete all PublishedWorks");
        publishedWorkRepository.deleteAll();
    }

    /**
     * Use only for fetching very large number of documents.
     * Get all documents from PublishedWork collection.
     * Using MongoTemplate and DBCursor to iterate over millions of records.
     * @return {DBCursor} to iterate over all documents in the collection
     */
    public DBCursor find() {
        log.info("Request to get all PublishedWorks with DBCursor");
        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), databaseName));
        DBCollection dbCollection = mongoTemplate.getCollection("published_work");
        return dbCollection.find();
    }

    /**
     * Use only for fetching very large number of documents.
     * Get all documents from PublishedWork collection which satisfy the given criteria.
     * Using MongoTemplate and DBCursor to iterate over millions of records.
     * @return {DBCursor} to iterate over all documents in the collection
     */
    public DBCursor find(BasicDBObject query) {
        log.info("Request to get DBCurosr for all PublishedWorks for given query");
        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), databaseName));
        DBCollection dbCollection = mongoTemplate.getCollection("published_work");
        return dbCollection.find(query);
    }
}
