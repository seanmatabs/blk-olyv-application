package za.co.blkolyv.application.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of StepSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class StepSearchRepositoryMockConfiguration {

    @MockBean
    private StepSearchRepository mockStepSearchRepository;

}
