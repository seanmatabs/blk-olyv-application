package za.co.blkolyv.application.service.impl;

import za.co.blkolyv.application.service.IngredientService;
import za.co.blkolyv.application.domain.Ingredient;
import za.co.blkolyv.application.repository.IngredientRepository;
import za.co.blkolyv.application.repository.search.IngredientSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Ingredient.
 */
@Service
public class IngredientServiceImpl implements IngredientService {

    private final Logger log = LoggerFactory.getLogger(IngredientServiceImpl.class);

    private final IngredientRepository ingredientRepository;

    private final IngredientSearchRepository ingredientSearchRepository;

    public IngredientServiceImpl(IngredientRepository ingredientRepository, IngredientSearchRepository ingredientSearchRepository) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientSearchRepository = ingredientSearchRepository;
    }

    /**
     * Save a ingredient.
     *
     * @param ingredient the entity to save
     * @return the persisted entity
     */
    @Override
    public Ingredient save(Ingredient ingredient) {
        log.debug("Request to save Ingredient : {}", ingredient);
        Ingredient result = ingredientRepository.save(ingredient);
        ingredientSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the ingredients.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public Page<Ingredient> findAll(Pageable pageable) {
        log.debug("Request to get all Ingredients");
        return ingredientRepository.findAll(pageable);
    }


    /**
     * Get one ingredient by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    public Optional<Ingredient> findOne(String id) {
        log.debug("Request to get Ingredient : {}", id);
        return ingredientRepository.findById(id);
    }

    /**
     * Delete the ingredient by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(String id) {
        log.debug("Request to delete Ingredient : {}", id);
        ingredientRepository.deleteById(id);
        ingredientSearchRepository.deleteById(id);
    }

    /**
     * Search for the ingredient corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public Page<Ingredient> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Ingredients for query {}", query);
        return ingredientSearchRepository.search(queryStringQuery(query), pageable);    }
}
