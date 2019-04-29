package za.co.blkolyv.application.web.rest;

import za.co.blkolyv.application.BlkOlyvApp;

import za.co.blkolyv.application.domain.Author;
import za.co.blkolyv.application.repository.AuthorRepository;
import za.co.blkolyv.application.repository.search.AuthorSearchRepository;
import za.co.blkolyv.application.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
 * Test class for the AuthorResource REST controller.
 *
 * @see AuthorResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BlkOlyvApp.class)
public class AuthorResourceIntTest {

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_ACTIVE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_ACTIVE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final Instant DEFAULT_BIRTHDAY = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BIRTHDAY = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private AuthorRepository authorRepository;

    @Mock
    private AuthorRepository authorRepositoryMock;

    /**
     * This repository is mocked in the za.co.blkolyv.application.repository.search test package.
     *
     * @see za.co.blkolyv.application.repository.search.AuthorSearchRepositoryMockConfiguration
     */
    @Autowired
    private AuthorSearchRepository mockAuthorSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restAuthorMockMvc;

    private Author author;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AuthorResource authorResource = new AuthorResource(authorRepository, mockAuthorSearchRepository);
        this.restAuthorMockMvc = MockMvcBuilders.standaloneSetup(authorResource)
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
    public static Author createEntity() {
        Author author = new Author()
            .imageUrl(DEFAULT_IMAGE_URL)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .created(DEFAULT_CREATED)
            .lastActive(DEFAULT_LAST_ACTIVE)
            .password(DEFAULT_PASSWORD)
            .birthday(DEFAULT_BIRTHDAY);
        return author;
    }

    @Before
    public void initTest() {
        authorRepository.deleteAll();
        author = createEntity();
    }

    @Test
    public void createAuthor() throws Exception {
        int databaseSizeBeforeCreate = authorRepository.findAll().size();

        // Create the Author
        restAuthorMockMvc.perform(post("/api/authors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(author)))
            .andExpect(status().isCreated());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeCreate + 1);
        Author testAuthor = authorList.get(authorList.size() - 1);
        assertThat(testAuthor.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testAuthor.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testAuthor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testAuthor.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testAuthor.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testAuthor.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testAuthor.getLastActive()).isEqualTo(DEFAULT_LAST_ACTIVE);
        assertThat(testAuthor.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testAuthor.getBirthday()).isEqualTo(DEFAULT_BIRTHDAY);

        // Validate the Author in Elasticsearch
        verify(mockAuthorSearchRepository, times(1)).save(testAuthor);
    }

    @Test
    public void createAuthorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = authorRepository.findAll().size();

        // Create the Author with an existing ID
        author.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuthorMockMvc.perform(post("/api/authors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(author)))
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeCreate);

        // Validate the Author in Elasticsearch
        verify(mockAuthorSearchRepository, times(0)).save(author);
    }

    @Test
    public void getAllAuthors() throws Exception {
        // Initialize the database
        authorRepository.save(author);

        // Get all the authorList
        restAuthorMockMvc.perform(get("/api/authors?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(author.getId())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL.toString())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME.toString())))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].lastActive").value(hasItem(DEFAULT_LAST_ACTIVE.toString())))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD.toString())))
            .andExpect(jsonPath("$.[*].birthday").value(hasItem(DEFAULT_BIRTHDAY.toString())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllAuthorsWithEagerRelationshipsIsEnabled() throws Exception {
        AuthorResource authorResource = new AuthorResource(authorRepositoryMock, mockAuthorSearchRepository);
        when(authorRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restAuthorMockMvc = MockMvcBuilders.standaloneSetup(authorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restAuthorMockMvc.perform(get("/api/authors?eagerload=true"))
        .andExpect(status().isOk());

        verify(authorRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllAuthorsWithEagerRelationshipsIsNotEnabled() throws Exception {
        AuthorResource authorResource = new AuthorResource(authorRepositoryMock, mockAuthorSearchRepository);
            when(authorRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restAuthorMockMvc = MockMvcBuilders.standaloneSetup(authorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restAuthorMockMvc.perform(get("/api/authors?eagerload=true"))
        .andExpect(status().isOk());

            verify(authorRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    public void getAuthor() throws Exception {
        // Initialize the database
        authorRepository.save(author);

        // Get the author
        restAuthorMockMvc.perform(get("/api/authors/{id}", author.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(author.getId()))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL.toString()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER.toString()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.lastActive").value(DEFAULT_LAST_ACTIVE.toString()))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD.toString()))
            .andExpect(jsonPath("$.birthday").value(DEFAULT_BIRTHDAY.toString()));
    }

    @Test
    public void getNonExistingAuthor() throws Exception {
        // Get the author
        restAuthorMockMvc.perform(get("/api/authors/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateAuthor() throws Exception {
        // Initialize the database
        authorRepository.save(author);

        int databaseSizeBeforeUpdate = authorRepository.findAll().size();

        // Update the author
        Author updatedAuthor = authorRepository.findById(author.getId()).get();
        updatedAuthor
            .imageUrl(UPDATED_IMAGE_URL)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .created(UPDATED_CREATED)
            .lastActive(UPDATED_LAST_ACTIVE)
            .password(UPDATED_PASSWORD)
            .birthday(UPDATED_BIRTHDAY);

        restAuthorMockMvc.perform(put("/api/authors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAuthor)))
            .andExpect(status().isOk());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
        Author testAuthor = authorList.get(authorList.size() - 1);
        assertThat(testAuthor.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testAuthor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testAuthor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testAuthor.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testAuthor.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testAuthor.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testAuthor.getLastActive()).isEqualTo(UPDATED_LAST_ACTIVE);
        assertThat(testAuthor.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testAuthor.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);

        // Validate the Author in Elasticsearch
        verify(mockAuthorSearchRepository, times(1)).save(testAuthor);
    }

    @Test
    public void updateNonExistingAuthor() throws Exception {
        int databaseSizeBeforeUpdate = authorRepository.findAll().size();

        // Create the Author

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuthorMockMvc.perform(put("/api/authors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(author)))
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Author in Elasticsearch
        verify(mockAuthorSearchRepository, times(0)).save(author);
    }

    @Test
    public void deleteAuthor() throws Exception {
        // Initialize the database
        authorRepository.save(author);

        int databaseSizeBeforeDelete = authorRepository.findAll().size();

        // Delete the author
        restAuthorMockMvc.perform(delete("/api/authors/{id}", author.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Author in Elasticsearch
        verify(mockAuthorSearchRepository, times(1)).deleteById(author.getId());
    }

    @Test
    public void searchAuthor() throws Exception {
        // Initialize the database
        authorRepository.save(author);
        when(mockAuthorSearchRepository.search(queryStringQuery("id:" + author.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(author), PageRequest.of(0, 1), 1));
        // Search the author
        restAuthorMockMvc.perform(get("/api/_search/authors?query=id:" + author.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(author.getId())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].lastActive").value(hasItem(DEFAULT_LAST_ACTIVE.toString())))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].birthday").value(hasItem(DEFAULT_BIRTHDAY.toString())));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Author.class);
        Author author1 = new Author();
        author1.setId("id1");
        Author author2 = new Author();
        author2.setId(author1.getId());
        assertThat(author1).isEqualTo(author2);
        author2.setId("id2");
        assertThat(author1).isNotEqualTo(author2);
        author1.setId(null);
        assertThat(author1).isNotEqualTo(author2);
    }
}
