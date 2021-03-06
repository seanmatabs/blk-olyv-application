package za.co.blkolyv.application.repository.search;

import za.co.blkolyv.application.domain.Ingredient;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Ingredient entity.
 */
public interface IngredientSearchRepository extends ElasticsearchRepository<Ingredient, String> {
}
