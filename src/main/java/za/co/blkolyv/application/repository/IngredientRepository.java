package za.co.blkolyv.application.repository;

import za.co.blkolyv.application.domain.Ingredient;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data MongoDB repository for the Ingredient entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IngredientRepository extends MongoRepository<Ingredient, String> {

}
