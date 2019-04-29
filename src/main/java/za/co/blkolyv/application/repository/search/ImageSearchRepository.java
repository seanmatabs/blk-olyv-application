package za.co.blkolyv.application.repository.search;

import za.co.blkolyv.application.domain.Image;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Image entity.
 */
public interface ImageSearchRepository extends ElasticsearchRepository<Image, String> {
}
