package com.plagchain.rest;

import com.plagchain.domain.ResponseItem;
import com.plagchain.service.PlagDetectionService;
import com.plagchain.service.UtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Jagrut on 27-04-2017.
 *
 * REST controller to handle requests related to submitting a document to check for plagiarism
 */

@RestController
@RequestMapping(path = "/plagcheck")
public class PlagCheckREST {

    private final Logger log = LoggerFactory.getLogger(PlagCheckREST.class);
    private PlagDetectionService plagDetectionService;
    private UtilService utilService;

    public PlagCheckREST(PlagDetectionService plagDetectionService, UtilService utilService) {
        this.plagDetectionService = plagDetectionService;
        this.utilService = utilService;
    }

    /**
     * REST method to fetch the seed for a given hash and all corresponding details
     * @param hashString the hash for which to fetch the seed details
     * @return {ResponseItem} object containing the SeedSubmission object
     */
    @RequestMapping(path = "/getHashSeed", method = RequestMethod.POST)
    public ResponseItem getHashSeedDetails(@RequestParam("hashString") String hashString) {
        log.info("REST request to get seed details for hash: {}", hashString);
        ResponseItem responseItem = new ResponseItem();
        responseItem.setSeedDetails(utilService.getSeedSubmissionObjectForHash(hashString));
        responseItem.setSuccess("success");
        return responseItem;
    }

    /**
     * REST method to run the Min Hash algorithm
     * @param textHashList list of min hash values for text in the document
     * @param imageHashList list of min hash values for images in the document
     * @param checkUnpublishedWorkStream set to true if user wish to check in unpublishedwork stream as well
     * @return {ResponseItem} object containing all documents that were similar to the hashes submitted
     */
    @RequestMapping(path = "/runMinHashAlgo", method = RequestMethod.POST)
    public ResponseItem runMinHashAlgo(@RequestBody @RequestParam("textHashList") List<Integer> textHashList,
                                       @RequestParam("imageHashList") List<String> imageHashList,
                                       @RequestParam("checkUnpublishedWorkStream") boolean checkUnpublishedWorkStream) {
        log.info("REST request to run Min Hash algorithm");
        ResponseItem responseItem = new ResponseItem();
        responseItem = plagDetectionService.runLSHAlgorithmPublishedWork(textHashList, imageHashList, responseItem);
        if (checkUnpublishedWorkStream)
            responseItem = plagDetectionService.runLSHAlgorithmUnpublishedWork(textHashList, imageHashList, responseItem);
        return responseItem;
    }
}
