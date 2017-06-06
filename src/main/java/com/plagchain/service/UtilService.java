package com.plagchain.service;

import com.plagchain.database.dbobjects.SeedSubmission;
import com.plagchain.database.service.SeedSubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Created by Jagrut on 06-06-2017.
 * A utility service that handles all generic requests for this module
 */

@Service
public class UtilService {

    private final Logger log = LoggerFactory.getLogger(UtilService.class);

    @Inject
    private SeedSubmissionService seedSubmissionService;

    /**
     * Find Seed Submission hash in DB whose plagchain_seed filed contains the given hash
     * @param hash the hash for which to find the corresponding seed
     * @return {SeedSubmission} object
     */
    public SeedSubmission getSeedSubmissionObjectForHash(String hash) {
        log.info("UtilService to get seed submission object which contains the given hash");
        SeedSubmission dbObject = seedSubmissionService.findByOriginstampSeedRegex(hash);
        if(dbObject != null)
            return dbObject;
        else
            return null;
    }
}
