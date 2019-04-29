package za.co.blkolyv.application.service;

import za.co.blkolyv.application.domain.Image;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Image.
 */
public interface ImageService {

    /**
     * Save a image.
     *
     * @param image the entity to save
     * @return the persisted entity
     */
    Image save(Image image);

    /**
     * Get all the images.
     *
     * @return the list of entities
     */
    List<Image> findAll();


    /**
     * Get the "id" image.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Image> findOne(String id);

    /**
     * Delete the "id" image.
     *
     * @param id the id of the entity
     */
    void delete(String id);

    /**
     * Search for the image corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @return the list of entities
     */
    List<Image> search(String query);
}
