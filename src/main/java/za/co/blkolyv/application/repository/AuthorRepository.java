package za.co.blkolyv.application.repository;

import za.co.blkolyv.application.domain.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for the Author entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuthorRepository extends MongoRepository<Author, String> {
    @Query("{}")
    Page<Author> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<Author> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<Author> findOneWithEagerRelationships(String id);

}
