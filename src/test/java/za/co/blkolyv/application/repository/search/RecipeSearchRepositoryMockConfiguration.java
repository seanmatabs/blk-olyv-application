package za.co.blkolyv.application.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of RecipeSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class RecipeSearchRepositoryMockConfiguration {

    @MockBean
    private RecipeSearchRepository mockRecipeSearchRepository;

}
