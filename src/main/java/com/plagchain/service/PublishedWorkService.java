package com.plagchain.service;

import com.plagchain.domain.PublishedWork;
import com.plagchain.repository.PublishedWorkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Jagrut on 27-04-2017.
 * Service to manage PublishedWork Database tasks
 */
@Service
public class PublishedWorkService {

    private final Logger log = LoggerFactory.getLogger(PublishedWorkService.class);

    @Inject
    private PublishedWorkRepository publishedWorkRepository;

    /**
     * Save a publishedWork.
     * @return the persisted entity
     */
    public PublishedWork save(PublishedWork publishedWork) {
        log.info("Request to save PublishedWork : {}", publishedWork);
        PublishedWork result = publishedWorkRepository.save(publishedWork);
        return result;
    }

    /**
     *  get all the publishedWorks.
     *  @return the list of entities
     */
    public List<PublishedWork> findAll() {
        log.info("Request to get all PublishedWorks");
        List<PublishedWork> result = publishedWorkRepository.findAll();
        return result;
    }

    /**
     *  get one publishedWork by id.
     *  @return the entity
     */
    public PublishedWork findOne(String id) {
        log.info("Request to get PublishedWork : {}", id);
        PublishedWork publishedWork = publishedWorkRepository.findOne(id);
        System.out.println("PublishedWorkService#findOne()");
        //System.out.println(publishedWork.toString());
        return publishedWork;
    }

    /**
     *  delete the  publishedWork by id.
     */
    public void delete(String id) {
        log.info("Request to delete PublishedWork : {}", id);
        publishedWorkRepository.delete(id);
    }

    /**
     * delete ALL publishedWork in Database.
     */
    public void deleteAll(){
        log.info("Request to delete all PublishedWorks");
        publishedWorkRepository.deleteAll();
    }

}
