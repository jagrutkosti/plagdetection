package com.plagchain.database.repository;

import com.plagchain.database.dbobjects.UnpublishedWork;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Jagrut on 22-05-2017.
 */
public interface UnpublishedWorkRepository extends MongoRepository<UnpublishedWork, String> {
}
