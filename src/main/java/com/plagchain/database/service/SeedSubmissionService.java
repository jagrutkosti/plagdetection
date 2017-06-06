package com.plagchain.database.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.plagchain.database.dbobjects.SeedSubmission;
import com.plagchain.database.repository.SeedSubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Jagrut on 01-06-2017.
 * Service to manage SeedSubmission Database tasks
 */
@Service
public class SeedSubmissionService {
    private final Logger log = LoggerFactory.getLogger(SeedSubmissionService.class);

    @Value("${spring.data.mongodb.database}")
    private String databaseName;
    
    @Inject
    private SeedSubmissionRepository seedSubmissionRepository;

    /**
     * Save a seedSubmission.
     * @return the persisted entity
     */
    public SeedSubmission save(SeedSubmission seedSubmission) {
        log.info("Request to save SeedSubmission : {}", seedSubmission);
        SeedSubmission result = seedSubmissionRepository.save(seedSubmission);
        return result;
    }

    /**
     *  get all the seedSubmissions.
     *  @return the list of entities
     */
    public List<SeedSubmission> findAll() {
        log.info("Request to get all SeedSubmissions");
        List<SeedSubmission> result = seedSubmissionRepository.findAll();
        return result;
    }

    /**
     *  get one seedSubmission by id.
     *  @return the entity
     */
    public SeedSubmission findOne(String id) {
        log.info("Request to get SeedSubmission : {}", id);
        SeedSubmission seedSubmission = seedSubmissionRepository.findOne(id);
        return seedSubmission;
    }

    /**
     *  delete the  seedSubmission by id.
     */
    public void delete(String id) {
        log.info("Request to delete SeedSubmission : {}", id);
        seedSubmissionRepository.delete(id);
    }

    /**
     * delete ALL seedSubmission in Database.
     */
    public void deleteAll(){
        log.info("Request to delete all SeedSubmissions");
        seedSubmissionRepository.deleteAll();
    }

    /**
     * Use only for fetching very large number of documents.
     * Get all documents from SeedSubmission collection.
     * Using MongoTemplate and DBCursor to iterate over millions of records.
     * @return {DBCursor} to iterate over all documents in the collection
     */
    public DBCursor find() {
        log.info("Request to get DBCursor for all SeedSubmissions");
        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), databaseName));
        DBCollection dbCollection = mongoTemplate.getCollection("seed_submission");
        return dbCollection.find();
    }

    /**
     * Use for fetching large number of documents.
     * Get all documents from SeedSubmission collection which satisfy the given criteria.
     * @param query "where" clause in the form of BasicDbObject
     * @return {DBCursor} to iterate over all documents in the collection
     */
    public DBCursor find(BasicDBObject query) {
        log.info("Request to get DBCursor for all SeedSubmissions with mentioned criteria");
        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), databaseName));
        DBCollection dbCollection = mongoTemplate.getCollection("seed_submission");
        return dbCollection.find(query);
    }

    public SeedSubmission findByOriginstampSeedRegex(String hash) {
        log.info("Request to get single SeedSubmission object which contains specified hash");
        return seedSubmissionRepository.findByOriginstampSeedRegex(hash);
    }
}
