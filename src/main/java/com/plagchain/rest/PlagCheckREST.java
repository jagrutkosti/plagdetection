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

    public PlagCheckREST(PlagService plagService) {
        this.plagService = plagService;
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
