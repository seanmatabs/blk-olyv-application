package za.co.blkolyv.application.repository;

import za.co.blkolyv.application.domain.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for the Recipe entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {
    @Query("{}")
    Page<Recipe> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<Recipe> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<Recipe> findOneWithEagerRelationships(String id);

}
