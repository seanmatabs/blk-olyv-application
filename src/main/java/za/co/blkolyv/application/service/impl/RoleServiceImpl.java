package za.co.blkolyv.application.service.impl;

import za.co.blkolyv.application.service.RoleService;
import za.co.blkolyv.application.domain.Role;
import za.co.blkolyv.application.repository.RoleRepository;
import za.co.blkolyv.application.repository.search.RoleSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Role.
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;

    private final RoleSearchRepository roleSearchRepository;

    public RoleServiceImpl(RoleRepository roleRepository, RoleSearchRepository roleSearchRepository) {
        this.roleRepository = roleRepository;
        this.roleSearchRepository = roleSearchRepository;
    }

    /**
     * Save a role.
     *
     * @param role the entity to save
     * @return the persisted entity
     */
    @Override
    public Role save(Role role) {
        log.debug("Request to save Role : {}", role);
        Role result = roleRepository.save(role);
        roleSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the roles.
     *
     * @return the list of entities
     */
    @Override
    public List<Role> findAll() {
        log.debug("Request to get all Roles");
        return roleRepository.findAll();
    }


    /**
     * Get one role by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    public Optional<Role> findOne(String id) {
        log.debug("Request to get Role : {}", id);
        return roleRepository.findById(id);
    }

    /**
     * Delete the role by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(String id) {
        log.debug("Request to delete Role : {}", id);
        roleRepository.deleteById(id);
        roleSearchRepository.deleteById(id);
    }

    /**
     * Search for the role corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Override
    public List<Role> search(String query) {
        log.debug("Request to search Roles for query {}", query);
        return StreamSupport
            .stream(roleSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
