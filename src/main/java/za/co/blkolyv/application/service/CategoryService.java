package za.co.blkolyv.application.service;

import za.co.blkolyv.application.domain.Category;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Category.
 */
public interface CategoryService {

    /**
     * Save a category.
     *
     * @param category the entity to save
     * @return the persisted entity
     */
    Category save(Category category);

    /**
     * Get all the categories.
     *
     * @return the list of entities
     */
    List<Category> findAll();


    /**
     * Get the "id" category.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Category> findOne(String id);

    /**
     * Delete the "id" category.
     *
     * @param id the id of the entity
     */
    void delete(String id);

    /**
     * Search for the category corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @return the list of entities
     */
    List<Category> search(String query);
}
