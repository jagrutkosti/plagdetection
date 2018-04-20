package com.plagchain.rest;

import com.plagchain.GenericResponse;
import com.plagchain.domain.ResponseItem;
import com.plagchain.service.PlagService;
import com.plagchain.service.UtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by Jagrut on 27-04-2017.
 *
 * REST controller to handle requests related to submitting a document to check for plagiarism
 */

@RestController
public class PlagCheckREST {

    private final Logger log = LoggerFactory.getLogger(PlagCheckREST.class);
    private PlagService plagService;
    private UtilService utilService;

    public PlagCheckREST(PlagService plagService, UtilService utilService) {
        this.plagService = plagService;
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

    @PostMapping("/submitDoc")
    @ResponseBody
    public GenericResponse putDocFeaturesInBlockchain(@RequestBody @RequestParam("file")MultipartFile file,
                                                      @RequestParam(required = false, value = "contactInfo")String contactInfo,
                                                      @RequestParam("publisherWalletAddress")String publisherWalletAddress) {
        log.info("REST request to extract document features and store them");
        return plagService.extractDocFeaturesAndStore(file, contactInfo, publisherWalletAddress);
    }

    @PostMapping("/checkSim")
    @ResponseBody
    public ResponseItem checkDocSim(@RequestBody @RequestParam("file")MultipartFile file) {
        log.info("REST request to find similar documents");
        ResponseItem responseItem = new ResponseItem();
        responseItem = plagService.extractDocFeaturesAndCheckSim(file, responseItem);
        return  responseItem;
    }
}
