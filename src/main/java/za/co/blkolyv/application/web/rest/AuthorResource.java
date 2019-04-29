package za.co.blkolyv.application.web.rest;
import za.co.blkolyv.application.domain.Author;
import za.co.blkolyv.application.repository.AuthorRepository;
import za.co.blkolyv.application.repository.search.AuthorSearchRepository;
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
 * REST controller for managing Author.
 */
@RestController
@RequestMapping("/api")
public class AuthorResource {

    private final Logger log = LoggerFactory.getLogger(AuthorResource.class);

    private static final String ENTITY_NAME = "author";

    private final AuthorRepository authorRepository;

    private final AuthorSearchRepository authorSearchRepository;

    public AuthorResource(AuthorRepository authorRepository, AuthorSearchRepository authorSearchRepository) {
        this.authorRepository = authorRepository;
        this.authorSearchRepository = authorSearchRepository;
    }

    /**
     * POST  /authors : Create a new author.
     *
     * @param author the author to create
     * @return the ResponseEntity with status 201 (Created) and with body the new author, or with status 400 (Bad Request) if the author has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/authors")
    public ResponseEntity<Author> createAuthor(@RequestBody Author author) throws URISyntaxException {
        log.debug("REST request to save Author : {}", author);
        if (author.getId() != null) {
            throw new BadRequestAlertException("A new author cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Author result = authorRepository.save(author);
        authorSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/authors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /authors : Updates an existing author.
     *
     * @param author the author to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated author,
     * or with status 400 (Bad Request) if the author is not valid,
     * or with status 500 (Internal Server Error) if the author couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/authors")
    public ResponseEntity<Author> updateAuthor(@RequestBody Author author) throws URISyntaxException {
        log.debug("REST request to update Author : {}", author);
        if (author.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Author result = authorRepository.save(author);
        authorSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, author.getId().toString()))
            .body(result);
    }

    /**
     * GET  /authors : get all the authors.
     *
     * @param pageable the pagination information
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many)
     * @return the ResponseEntity with status 200 (OK) and the list of authors in body
     */
    @GetMapping("/authors")
    public ResponseEntity<List<Author>> getAllAuthors(Pageable pageable, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get a page of Authors");
        Page<Author> page;
        if (eagerload) {
            page = authorRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = authorRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, String.format("/api/authors?eagerload=%b", eagerload));
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /authors/:id : get the "id" author.
     *
     * @param id the id of the author to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the author, or with status 404 (Not Found)
     */
    @GetMapping("/authors/{id}")
    public ResponseEntity<Author> getAuthor(@PathVariable String id) {
        log.debug("REST request to get Author : {}", id);
        Optional<Author> author = authorRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(author);
    }

    /**
     * DELETE  /authors/:id : delete the "id" author.
     *
     * @param id the id of the author to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/authors/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable String id) {
        log.debug("REST request to delete Author : {}", id);
        authorRepository.deleteById(id);
        authorSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }

    /**
     * SEARCH  /_search/authors?query=:query : search for the author corresponding
     * to the query.
     *
     * @param query the query of the author search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/authors")
    public ResponseEntity<List<Author>> searchAuthors(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Authors for query {}", query);
        Page<Author> page = authorSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/authors");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
