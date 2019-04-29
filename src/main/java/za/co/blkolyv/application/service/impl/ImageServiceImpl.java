package za.co.blkolyv.application.service.impl;

import za.co.blkolyv.application.service.ImageService;
import za.co.blkolyv.application.domain.Image;
import za.co.blkolyv.application.repository.ImageRepository;
import za.co.blkolyv.application.repository.search.ImageSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Image.
 */
@Service
public class ImageServiceImpl implements ImageService {

    private final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageRepository imageRepository;

    private final ImageSearchRepository imageSearchRepository;

    public ImageServiceImpl(ImageRepository imageRepository, ImageSearchRepository imageSearchRepository) {
        this.imageRepository = imageRepository;
        this.imageSearchRepository = imageSearchRepository;
    }

    /**
     * Save a image.
     *
     * @param image the entity to save
     * @return the persisted entity
     */
    @Override
    public Image save(Image image) {
        log.debug("Request to save Image : {}", image);
        Image result = imageRepository.save(image);
        imageSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the images.
     *
     * @return the list of entities
     */
    @Override
    public List<Image> findAll() {
        log.debug("Request to get all Images");
        return imageRepository.findAll();
    }


    /**
     * Get one image by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    public Optional<Image> findOne(String id) {
        log.debug("Request to get Image : {}", id);
        return imageRepository.findById(id);
    }

    /**
     * Delete the image by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(String id) {
        log.debug("Request to delete Image : {}", id);
        imageRepository.deleteById(id);
        imageSearchRepository.deleteById(id);
    }

    /**
     * Search for the image corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Override
    public List<Image> search(String query) {
        log.debug("Request to search Images for query {}", query);
        return StreamSupport
            .stream(imageSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
