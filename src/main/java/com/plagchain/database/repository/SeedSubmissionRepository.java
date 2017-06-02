package com.plagchain.database.repository;

import com.plagchain.database.dbobjects.SeedSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Jagrut on 01-06-2017.
 */
public interface SeedSubmissionRepository extends MongoRepository<SeedSubmission, String> {
}
