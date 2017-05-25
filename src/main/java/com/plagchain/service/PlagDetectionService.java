package com.plagchain.service;

import com.mongodb.DBCursor;
import com.plagchain.database.dbobjects.PublishedWork;
import com.plagchain.database.dbobjects.UnpublishedWork;
import com.plagchain.database.service.PublishedWorkService;
import com.plagchain.database.service.UnpublishedWorkService;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jagrut on 22-05-2017.
 * Service class which will be used by the REST to transfer the task of plagiarism detection.
 */
public class PlagDetectionService {

    @Value("${plagdetection.streamname.publishedwork}")
    private String publishedWorkStreamName;

    @Value("${plagdetection.streamname.unpublishedwork}")
    private String unpublishedWorkStreamName;

    @Inject
    private PublishedWorkService publishedWorkService;

    @Inject
    private UnpublishedWorkService unpublishedWorkService;

    public List<PublishedWork> runLSHAlgorithmPublishedWork(String plagCheckDocHash, List<String> plagCheckMinHashList,
                                             List<String> plagCheckImageMinHashList) {
        List<PublishedWork> similarDocuments = new ArrayList<>();
        DBCursor dbCursor = publishedWorkService.find();
        while(dbCursor.hasNext()) {
            PublishedWork singleDocument = (PublishedWork) dbCursor.next();
            if(singleDocument.getDocHashKey().equals(plagCheckDocHash)) {
                similarDocuments.add(singleDocument);
                continue;
            }

        }
        return similarDocuments;
    }

    public List<UnpublishedWork> runLSHAlgorithmUnpublishedWork(String plagCheckDocHash,
                                                                List<String> plagCheckMinHashList,
                                                                List<String> plagCheckImageMinHashList) {
        List<UnpublishedWork> similarDocuments = new ArrayList<>();
        DBCursor dbCursor = unpublishedWorkService.find();
        while(dbCursor.hasNext()) {
            UnpublishedWork singleDocument = (UnpublishedWork) dbCursor.next();
            if(singleDocument.getDocHashKey().equals(plagCheckDocHash)) {
                similarDocuments.add(singleDocument);
                continue;
            }
        }
        return similarDocuments;
    }
}
