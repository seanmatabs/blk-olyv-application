package za.co.blkolyv.application.service.impl;

import za.co.blkolyv.application.service.StepService;
import za.co.blkolyv.application.domain.Step;
import za.co.blkolyv.application.repository.StepRepository;
import za.co.blkolyv.application.repository.search.StepSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Step.
 */
@Service
public class StepServiceImpl implements StepService {

    private final Logger log = LoggerFactory.getLogger(StepServiceImpl.class);

    private final StepRepository stepRepository;

    private final StepSearchRepository stepSearchRepository;

    public StepServiceImpl(StepRepository stepRepository, StepSearchRepository stepSearchRepository) {
        this.stepRepository = stepRepository;
        this.stepSearchRepository = stepSearchRepository;
    }

    /**
     * Save a step.
     *
     * @param step the entity to save
     * @return the persisted entity
     */
    @Override
    public Step save(Step step) {
        log.debug("Request to save Step : {}", step);
        Step result = stepRepository.save(step);
        stepSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the steps.
     *
     * @return the list of entities
     */
    @Override
    public List<Step> findAll() {
        log.debug("Request to get all Steps");
        return stepRepository.findAll();
    }


    /**
     * Get one step by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    public Optional<Step> findOne(String id) {
        log.debug("Request to get Step : {}", id);
        return stepRepository.findById(id);
    }

    /**
     * Delete the step by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(String id) {
        log.debug("Request to delete Step : {}", id);
        stepRepository.deleteById(id);
        stepSearchRepository.deleteById(id);
    }

    /**
     * Search for the step corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Override
    public List<Step> search(String query) {
        log.debug("Request to search Steps for query {}", query);
        return StreamSupport
            .stream(stepSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
