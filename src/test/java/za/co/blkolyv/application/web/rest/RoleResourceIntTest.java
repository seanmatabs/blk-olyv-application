package za.co.blkolyv.application.web.rest;

import za.co.blkolyv.application.BlkOlyvApp;

import za.co.blkolyv.application.domain.Role;
import za.co.blkolyv.application.repository.RoleRepository;
import za.co.blkolyv.application.repository.search.RoleSearchRepository;
import za.co.blkolyv.application.service.RoleService;
import za.co.blkolyv.application.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.Collections;
import java.util.List;


import static za.co.blkolyv.application.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RoleResource REST controller.
 *
 * @see RoleResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BlkOlyvApp.class)
public class RoleResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    /**
     * This repository is mocked in the za.co.blkolyv.application.repository.search test package.
     *
     * @see za.co.blkolyv.application.repository.search.RoleSearchRepositoryMockConfiguration
     */
    @Autowired
    private RoleSearchRepository mockRoleSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restRoleMockMvc;

    private Role role;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RoleResource roleResource = new RoleResource(roleService);
        this.restRoleMockMvc = MockMvcBuilders.standaloneSetup(roleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Role createEntity() {
        Role role = new Role()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION);
        return role;
    }

    @Before
    public void initTest() {
        roleRepository.deleteAll();
        role = createEntity();
    }

    @Test
    public void createRole() throws Exception {
        int databaseSizeBeforeCreate = roleRepository.findAll().size();

        // Create the Role
        restRoleMockMvc.perform(post("/api/roles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(role)))
            .andExpect(status().isCreated());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeCreate + 1);
        Role testRole = roleList.get(roleList.size() - 1);
        assertThat(testRole.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testRole.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Role in Elasticsearch
        verify(mockRoleSearchRepository, times(1)).save(testRole);
    }

    @Test
    public void createRoleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = roleRepository.findAll().size();

        // Create the Role with an existing ID
        role.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoleMockMvc.perform(post("/api/roles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(role)))
            .andExpect(status().isBadRequest());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeCreate);

        // Validate the Role in Elasticsearch
        verify(mockRoleSearchRepository, times(0)).save(role);
    }

    @Test
    public void getAllRoles() throws Exception {
        // Initialize the database
        roleRepository.save(role);

        // Get all the roleList
        restRoleMockMvc.perform(get("/api/roles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(role.getId())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
    
    @Test
    public void getRole() throws Exception {
        // Initialize the database
        roleRepository.save(role);

        // Get the role
        restRoleMockMvc.perform(get("/api/roles/{id}", role.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(role.getId()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    public void getNonExistingRole() throws Exception {
        // Get the role
        restRoleMockMvc.perform(get("/api/roles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateRole() throws Exception {
        // Initialize the database
        roleService.save(role);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockRoleSearchRepository);

        int databaseSizeBeforeUpdate = roleRepository.findAll().size();

        // Update the role
        Role updatedRole = roleRepository.findById(role.getId()).get();
        updatedRole
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION);

        restRoleMockMvc.perform(put("/api/roles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRole)))
            .andExpect(status().isOk());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
        Role testRole = roleList.get(roleList.size() - 1);
        assertThat(testRole.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testRole.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Role in Elasticsearch
        verify(mockRoleSearchRepository, times(1)).save(testRole);
    }

    @Test
    public void updateNonExistingRole() throws Exception {
        int databaseSizeBeforeUpdate = roleRepository.findAll().size();

        // Create the Role

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoleMockMvc.perform(put("/api/roles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(role)))
            .andExpect(status().isBadRequest());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Role in Elasticsearch
        verify(mockRoleSearchRepository, times(0)).save(role);
    }

    @Test
    public void deleteRole() throws Exception {
        // Initialize the database
        roleService.save(role);

        int databaseSizeBeforeDelete = roleRepository.findAll().size();

        // Delete the role
        restRoleMockMvc.perform(delete("/api/roles/{id}", role.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Role in Elasticsearch
        verify(mockRoleSearchRepository, times(1)).deleteById(role.getId());
    }

    @Test
    public void searchRole() throws Exception {
        // Initialize the database
        roleService.save(role);
        when(mockRoleSearchRepository.search(queryStringQuery("id:" + role.getId())))
            .thenReturn(Collections.singletonList(role));
        // Search the role
        restRoleMockMvc.perform(get("/api/_search/roles?query=id:" + role.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(role.getId())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Role.class);
        Role role1 = new Role();
        role1.setId("id1");
        Role role2 = new Role();
        role2.setId(role1.getId());
        assertThat(role1).isEqualTo(role2);
        role2.setId("id2");
        assertThat(role1).isNotEqualTo(role2);
        role1.setId(null);
        assertThat(role1).isNotEqualTo(role2);
    }
}
