package za.co.blkolyv.application.repository.search;

import za.co.blkolyv.application.domain.Recipe;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Recipe entity.
 */
public interface RecipeSearchRepository extends ElasticsearchRepository<Recipe, String> {
}
