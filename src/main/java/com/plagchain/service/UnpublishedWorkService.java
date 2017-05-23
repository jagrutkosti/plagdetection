package com.plagchain.service;

import com.plagchain.domain.UnpublishedWork;
import com.plagchain.repository.UnpublishedWorkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Jagrut on 22-05-2017.
 * Service to manage UnpublishedWork Database tasks
 */
@Service
public class UnpublishedWorkService {

    private final Logger log = LoggerFactory.getLogger(UnpublishedWorkService.class);

    @Inject
    private UnpublishedWorkRepository unpublishedWorkRepository;

    /**
     * Save a publishedWork.
     * @return the persisted entity
     */
    public UnpublishedWork save(UnpublishedWork publishedWork) {
        log.info("Request to save UnpublishedWork : {}", publishedWork);
        UnpublishedWork result = unpublishedWorkRepository.save(publishedWork);
        return result;
    }

    /**
     *  get all the publishedWorks.
     *  @return the list of entities
     */
    public List<UnpublishedWork> findAll() {
        log.info("Request to get all UnpublishedWorks");
        List<UnpublishedWork> result = unpublishedWorkRepository.findAll();
        return result;
    }

    /**
     *  get one publishedWork by id.
     *  @return the entity
     */
    public UnpublishedWork findOne(String id) {
        log.info("Request to get UnpublishedWork : {}", id);
        UnpublishedWork publishedWork = unpublishedWorkRepository.findOne(id);
        System.out.println("UnpublishedWorkService#findOne()");
        //System.out.println(publishedWork.toString());
        return publishedWork;
    }

    /**
     *  delete the  publishedWork by id.
     */
    public void delete(String id) {
        log.info("Request to delete UnpublishedWork : {}", id);
        unpublishedWorkRepository.delete(id);
    }

    /**
     * delete ALL publishedWork in Database.
     */
    public void deleteAll(){
        log.info("Request to delete all UnpublishedWorks");
        unpublishedWorkRepository.deleteAll();
    }
}
