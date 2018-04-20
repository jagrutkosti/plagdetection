package com.plagchain.database.repository;

import com.plagchain.database.dbobjects.MinHashFeatures;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Jagrut on 27-04-2017.
 */
public interface MinHashFeaturesRepository extends MongoRepository<MinHashFeatures, String> {
}
