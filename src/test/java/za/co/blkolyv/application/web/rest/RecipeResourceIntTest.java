package za.co.blkolyv.application.web.rest;

import za.co.blkolyv.application.BlkOlyvApp;

import za.co.blkolyv.application.domain.Recipe;
import za.co.blkolyv.application.repository.RecipeRepository;
import za.co.blkolyv.application.repository.search.RecipeSearchRepository;
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
 * Test class for the RecipeResource REST controller.
 *
 * @see RecipeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BlkOlyvApp.class)
public class RecipeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_SERVES = 1;
    private static final Integer UPDATED_SERVES = 2;

    private static final String DEFAULT_PREPTIME = "AAAAAAAAAA";
    private static final String UPDATED_PREPTIME = "BBBBBBBBBB";

    @Autowired
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeRepository recipeRepositoryMock;

    /**
     * This repository is mocked in the za.co.blkolyv.application.repository.search test package.
     *
     * @see za.co.blkolyv.application.repository.search.RecipeSearchRepositoryMockConfiguration
     */
    @Autowired
    private RecipeSearchRepository mockRecipeSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restRecipeMockMvc;

    private Recipe recipe;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RecipeResource recipeResource = new RecipeResource(recipeRepository, mockRecipeSearchRepository);
        this.restRecipeMockMvc = MockMvcBuilders.standaloneSetup(recipeResource)
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
    public static Recipe createEntity() {
        Recipe recipe = new Recipe()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .serves(DEFAULT_SERVES)
            .preptime(DEFAULT_PREPTIME);
        return recipe;
    }

    @Before
    public void initTest() {
        recipeRepository.deleteAll();
        recipe = createEntity();
    }

    @Test
    public void createRecipe() throws Exception {
        int databaseSizeBeforeCreate = recipeRepository.findAll().size();

        // Create the Recipe
        restRecipeMockMvc.perform(post("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isCreated());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeCreate + 1);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRecipe.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRecipe.getServes()).isEqualTo(DEFAULT_SERVES);
        assertThat(testRecipe.getPreptime()).isEqualTo(DEFAULT_PREPTIME);

        // Validate the Recipe in Elasticsearch
        verify(mockRecipeSearchRepository, times(1)).save(testRecipe);
    }

    @Test
    public void createRecipeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = recipeRepository.findAll().size();

        // Create the Recipe with an existing ID
        recipe.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecipeMockMvc.perform(post("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Recipe in Elasticsearch
        verify(mockRecipeSearchRepository, times(0)).save(recipe);
    }

    @Test
    public void getAllRecipes() throws Exception {
        // Initialize the database
        recipeRepository.save(recipe);

        // Get all the recipeList
        restRecipeMockMvc.perform(get("/api/recipes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipe.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].serves").value(hasItem(DEFAULT_SERVES)))
            .andExpect(jsonPath("$.[*].preptime").value(hasItem(DEFAULT_PREPTIME.toString())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllRecipesWithEagerRelationshipsIsEnabled() throws Exception {
        RecipeResource recipeResource = new RecipeResource(recipeRepositoryMock, mockRecipeSearchRepository);
        when(recipeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restRecipeMockMvc = MockMvcBuilders.standaloneSetup(recipeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restRecipeMockMvc.perform(get("/api/recipes?eagerload=true"))
        .andExpect(status().isOk());

        verify(recipeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllRecipesWithEagerRelationshipsIsNotEnabled() throws Exception {
        RecipeResource recipeResource = new RecipeResource(recipeRepositoryMock, mockRecipeSearchRepository);
            when(recipeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restRecipeMockMvc = MockMvcBuilders.standaloneSetup(recipeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restRecipeMockMvc.perform(get("/api/recipes?eagerload=true"))
        .andExpect(status().isOk());

            verify(recipeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    public void getRecipe() throws Exception {
        // Initialize the database
        recipeRepository.save(recipe);

        // Get the recipe
        restRecipeMockMvc.perform(get("/api/recipes/{id}", recipe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(recipe.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.serves").value(DEFAULT_SERVES))
            .andExpect(jsonPath("$.preptime").value(DEFAULT_PREPTIME.toString()));
    }

    @Test
    public void getNonExistingRecipe() throws Exception {
        // Get the recipe
        restRecipeMockMvc.perform(get("/api/recipes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateRecipe() throws Exception {
        // Initialize the database
        recipeRepository.save(recipe);

        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();

        // Update the recipe
        Recipe updatedRecipe = recipeRepository.findById(recipe.getId()).get();
        updatedRecipe
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .serves(UPDATED_SERVES)
            .preptime(UPDATED_PREPTIME);

        restRecipeMockMvc.perform(put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRecipe)))
            .andExpect(status().isOk());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecipe.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRecipe.getServes()).isEqualTo(UPDATED_SERVES);
        assertThat(testRecipe.getPreptime()).isEqualTo(UPDATED_PREPTIME);

        // Validate the Recipe in Elasticsearch
        verify(mockRecipeSearchRepository, times(1)).save(testRecipe);
    }

    @Test
    public void updateNonExistingRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();

        // Create the Recipe

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeMockMvc.perform(put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Recipe in Elasticsearch
        verify(mockRecipeSearchRepository, times(0)).save(recipe);
    }

    @Test
    public void deleteRecipe() throws Exception {
        // Initialize the database
        recipeRepository.save(recipe);

        int databaseSizeBeforeDelete = recipeRepository.findAll().size();

        // Delete the recipe
        restRecipeMockMvc.perform(delete("/api/recipes/{id}", recipe.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Recipe in Elasticsearch
        verify(mockRecipeSearchRepository, times(1)).deleteById(recipe.getId());
    }

    @Test
    public void searchRecipe() throws Exception {
        // Initialize the database
        recipeRepository.save(recipe);
        when(mockRecipeSearchRepository.search(queryStringQuery("id:" + recipe.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(recipe), PageRequest.of(0, 1), 1));
        // Search the recipe
        restRecipeMockMvc.perform(get("/api/_search/recipes?query=id:" + recipe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipe.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].serves").value(hasItem(DEFAULT_SERVES)))
            .andExpect(jsonPath("$.[*].preptime").value(hasItem(DEFAULT_PREPTIME)));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Recipe.class);
        Recipe recipe1 = new Recipe();
        recipe1.setId("id1");
        Recipe recipe2 = new Recipe();
        recipe2.setId(recipe1.getId());
        assertThat(recipe1).isEqualTo(recipe2);
        recipe2.setId("id2");
        assertThat(recipe1).isNotEqualTo(recipe2);
        recipe1.setId(null);
        assertThat(recipe1).isNotEqualTo(recipe2);
    }
}
