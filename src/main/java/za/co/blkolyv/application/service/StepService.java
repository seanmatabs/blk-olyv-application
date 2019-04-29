package za.co.blkolyv.application.service;

import za.co.blkolyv.application.domain.Step;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Step.
 */
public interface StepService {

    /**
     * Save a step.
     *
     * @param step the entity to save
     * @return the persisted entity
     */
    Step save(Step step);

    /**
     * Get all the steps.
     *
     * @return the list of entities
     */
    List<Step> findAll();


    /**
     * Get the "id" step.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Step> findOne(String id);

    /**
     * Delete the "id" step.
     *
     * @param id the id of the entity
     */
    void delete(String id);

    /**
     * Search for the step corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @return the list of entities
     */
    List<Step> search(String query);
}
