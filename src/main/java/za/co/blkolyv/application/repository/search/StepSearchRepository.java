package za.co.blkolyv.application.repository.search;

import za.co.blkolyv.application.domain.Step;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Step entity.
 */
public interface StepSearchRepository extends ElasticsearchRepository<Step, String> {
}
