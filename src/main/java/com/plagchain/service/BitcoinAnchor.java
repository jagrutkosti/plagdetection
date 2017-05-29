package com.plagchain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Jagrut on 29-05-2017.
 * Handles anchoring the confirmed hashes into Bitcoin using Originstamp service.
 */

@Component
public class BitcoinAnchor {
    private final Logger log = LoggerFactory.getLogger(BitcoinAnchor.class);

    @Scheduled(fixedRateString = "${plagdetection.hash.anchor.timeinterval}")
    public void startAnchoring() {
        log.info("Start Anchoring");

    }
}
