package za.co.blkolyv.application.repository.search;

import za.co.blkolyv.application.domain.Role;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Role entity.
 */
public interface RoleSearchRepository extends ElasticsearchRepository<Role, String> {
}
