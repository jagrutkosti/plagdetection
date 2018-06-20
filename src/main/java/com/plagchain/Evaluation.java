package com.plagchain;

import com.plagchain.database.dbobjects.MinHashFeatures;
import com.plagchain.database.service.MinHashFeaturesService;
import com.plagchain.service.UtilService;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class Evaluation {
    private UtilService utilService;
    private MinHashFeaturesService minHashFeaturesService;

    public Evaluation(UtilService utilService, MinHashFeaturesService minHashFeaturesService) {
        this.utilService = utilService;
        this.minHashFeaturesService = minHashFeaturesService;
    }

    //@Scheduled(fixedRate = 86400000)
    public void start() {
        String walletAddress = "1YoAuhGkvRqNdPip2qmuBnxj1D5vaYeQvQSCz2";

        File directory = new File("/home/jagrut/Downloads/pan/source-documents");
        FileFilter fileFilter = new WildcardFileFilter("source-document1????.txt");
        File[] txtFiles = directory.listFiles(fileFilter);

        System.out.println("Number of files::" + txtFiles.length);

        for(int i = 0; i < txtFiles.length; i++) {
            try {
                String fileContent = new String(Files.readAllBytes(txtFiles[i].toPath()));
                extractDocFeaturesAndStoreInDb(fileContent, txtFiles[i].getName(), walletAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void extractDocFeaturesAndStoreInDb(String fileContent, String fileName, String walletAddress) {

        String cleanedText = utilService.cleanText(fileContent);
        cleanedText = utilService.removeAllWhiteSpaces(cleanedText);
        List<String> allShingles = new ArrayList<>();
        allShingles.addAll(utilService.createShingles(Constants.SHINGLE_LENGTH, cleanedText));
        int[] minHashFromShingles = utilService.generateMinHashSignature(allShingles);
        List<String> sha256 = utilService.generateSHA256HashFromObjects(Arrays.asList(fileContent.getBytes()));

        MinHashFeatures minHashFeatures = new MinHashFeatures();
        minHashFeatures.setPublisherAddress(walletAddress);
        minHashFeatures.setFileName(fileName);
        minHashFeatures.setListMinHash(Arrays.asList(ArrayUtils.toObject(minHashFromShingles)));
        minHashFeatures.setDocHashKey(sha256.get(0));
        minHashFeaturesService.save(minHashFeatures);
    }
}
