package za.co.blkolyv.application.web.rest;
import za.co.blkolyv.application.domain.Recipe;
import za.co.blkolyv.application.repository.RecipeRepository;
import za.co.blkolyv.application.repository.search.RecipeSearchRepository;
import za.co.blkolyv.application.web.rest.errors.BadRequestAlertException;
import za.co.blkolyv.application.web.rest.util.HeaderUtil;
import za.co.blkolyv.application.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Recipe.
 */
@RestController
@RequestMapping("/api")
public class RecipeResource {

    private final Logger log = LoggerFactory.getLogger(RecipeResource.class);

    private static final String ENTITY_NAME = "recipe";

    private final RecipeRepository recipeRepository;

    private final RecipeSearchRepository recipeSearchRepository;

    public RecipeResource(RecipeRepository recipeRepository, RecipeSearchRepository recipeSearchRepository) {
        this.recipeRepository = recipeRepository;
        this.recipeSearchRepository = recipeSearchRepository;
    }

    /**
     * POST  /recipes : Create a new recipe.
     *
     * @param recipe the recipe to create
     * @return the ResponseEntity with status 201 (Created) and with body the new recipe, or with status 400 (Bad Request) if the recipe has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/recipes")
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) throws URISyntaxException {
        log.debug("REST request to save Recipe : {}", recipe);
        if (recipe.getId() != null) {
            throw new BadRequestAlertException("A new recipe cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Recipe result = recipeRepository.save(recipe);
        recipeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/recipes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /recipes : Updates an existing recipe.
     *
     * @param recipe the recipe to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated recipe,
     * or with status 400 (Bad Request) if the recipe is not valid,
     * or with status 500 (Internal Server Error) if the recipe couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/recipes")
    public ResponseEntity<Recipe> updateRecipe(@RequestBody Recipe recipe) throws URISyntaxException {
        log.debug("REST request to update Recipe : {}", recipe);
        if (recipe.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Recipe result = recipeRepository.save(recipe);
        recipeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, recipe.getId().toString()))
            .body(result);
    }

    /**
     * GET  /recipes : get all the recipes.
     *
     * @param pageable the pagination information
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many)
     * @return the ResponseEntity with status 200 (OK) and the list of recipes in body
     */
    @GetMapping("/recipes")
    public ResponseEntity<List<Recipe>> getAllRecipes(Pageable pageable, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get a page of Recipes");
        Page<Recipe> page;
        if (eagerload) {
            page = recipeRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = recipeRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, String.format("/api/recipes?eagerload=%b", eagerload));
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /recipes/:id : get the "id" recipe.
     *
     * @param id the id of the recipe to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the recipe, or with status 404 (Not Found)
     */
    @GetMapping("/recipes/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable String id) {
        log.debug("REST request to get Recipe : {}", id);
        Optional<Recipe> recipe = recipeRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(recipe);
    }

    /**
     * DELETE  /recipes/:id : delete the "id" recipe.
     *
     * @param id the id of the recipe to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/recipes/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable String id) {
        log.debug("REST request to delete Recipe : {}", id);
        recipeRepository.deleteById(id);
        recipeSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }

    /**
     * SEARCH  /_search/recipes?query=:query : search for the recipe corresponding
     * to the query.
     *
     * @param query the query of the recipe search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/recipes")
    public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Recipes for query {}", query);
        Page<Recipe> page = recipeSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/recipes");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
