package com.plagchain.rest;

import com.plagchain.database.dbobjects.PublishedWork;
import com.plagchain.database.service.PublishedWorkService;
import com.plagchain.domain.ResponseItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jagrut on 27-04-2017.
 *
 * REST controller to handle requests related to submitting a document to check for plagiarism
 */

@RestController
@RequestMapping(path = "/plagcheck")
public class PlagCheckREST {

    private final Logger log = LoggerFactory.getLogger(PlagCheckREST.class);

    @Inject
    private PublishedWorkService publishedWorkService;

    @RequestMapping(path = "/{saveTest}")
    String test(@PathVariable String saveTest) {
        log.info("REST request to test : {}", saveTest);

        PublishedWork work = new PublishedWork();
        work.setDocHashKey(saveTest);
        List<String> someRandomList = new ArrayList<>();
        someRandomList.add("minhash1");
        someRandomList.add("minhash2");
        work.setListMinHash(someRandomList);
        publishedWorkService.save(work);
        return "Hello! The Object was saved";
    }

    @RequestMapping(path = "/getHashSeed", method = RequestMethod.POST)
    public ResponseItem getHashSeedDetails(@RequestParam("hashString") String hashString) {
        log.info("REST request to get seed details for hash: {}", hashString);
        return null;
    }
}
