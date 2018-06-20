package com.plagchain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to performing the LSH binning of the min hash values from the database.
 * Call performLSH() method when the server starts as well.
 * The performLSH() method needs to run every 24 hours if there are new documents added in the database.
 */

public class LSH {
    private int[][] minHashes;
    private int numBands;
    private int numHashFunctions;
    private int rowsPerBand;
    private int numDocuments;
    private Map<Integer, List<Integer>> lshBuckets = new HashMap<>();

    /**
     * Initialize the parameter values
     * @param minHashes min hash matrix with rows as the documents and columns as minhash values
     * @param rowsPerBand number of rows per band, usually 20
     */
    public LSH(int[][] minHashes, int rowsPerBand) {
        this.minHashes = minHashes;
        this.rowsPerBand = rowsPerBand;
        this.numHashFunctions = minHashes[0].length;
        this.numDocuments = minHashes.length;
        this.numBands = this.numHashFunctions / rowsPerBand;
    }

    public void performLSH() {
        int thisHash = 0;
        for (int b = 0; b < numBands; b++) {
            HashMap<Integer, List<Integer>> thisMap = new HashMap<>();
            for (int d = 0; d < numDocuments; d++) {
                int hashValue = 0;
                for (int th = thisHash; th < thisHash + rowsPerBand; th++) {
                    hashValue = hashValue * 1174247 + minHashes[d][th];
                }
                if (!thisMap.containsKey(hashValue))
                    thisMap.put(hashValue, new ArrayList<>());
                thisMap.get(hashValue).add(d);
            }
            thisHash += rowsPerBand;
            HashMap<Integer, List<Integer>> copy = new HashMap<>();
            for(int hashVal : thisMap.keySet()) {
                if(thisMap.get(hashVal).size() > 1)
                    copy.put(hashVal, thisMap.get(hashVal));
            }
            System.out.println(copy.size());
            lshBuckets.putAll(copy);
        }
        System.out.println("Final LSH Buckets size::::" + lshBuckets.size());
    }

    public List<Integer> getSimilarDocuments(int rowOfDocInMatrix) {
        List<Integer> similarItems = new ArrayList<>();
        for(List<Integer> lshItem : lshBuckets.values()) {
            if(lshItem.contains(rowOfDocInMatrix))
                similarItems.addAll(lshItem);
        }
        //similarItems.remove(rowOfDocInMatrix);
        return similarItems;
    }
}
