package za.co.blkolyv.application.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * not an ignored comment
 */
@ApiModel(description = "not an ignored comment")
@Document(collection = "recipe")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "recipe")
public class Recipe implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("serves")
    private Integer serves;

    @Field("preptime")
    private String preptime;

    @DBRef
    @Field("image")
    private Set<Image> images = new HashSet<>();
    @DBRef
    @Field("step")
    private Set<Step> steps = new HashSet<>();
    @DBRef
    @Field("author")
    @JsonIgnoreProperties("recipes")
    private Author author;

    /**
     * A relationship
     */
    @ApiModelProperty(value = "A relationship")
    @DBRef
    @Field("ingredient")
    @JsonIgnoreProperties("recipes")
    private Ingredient ingredient;

    @DBRef
    @Field("categories")
    private Set<Category> categories = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Recipe name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Recipe description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getServes() {
        return serves;
    }

    public Recipe serves(Integer serves) {
        this.serves = serves;
        return this;
    }

    public void setServes(Integer serves) {
        this.serves = serves;
    }

    public String getPreptime() {
        return preptime;
    }

    public Recipe preptime(String preptime) {
        this.preptime = preptime;
        return this;
    }

    public void setPreptime(String preptime) {
        this.preptime = preptime;
    }

    public Set<Image> getImages() {
        return images;
    }

    public Recipe images(Set<Image> images) {
        this.images = images;
        return this;
    }

    public Recipe addImage(Image image) {
        this.images.add(image);
        image.setRecipe(this);
        return this;
    }

    public Recipe removeImage(Image image) {
        this.images.remove(image);
        image.setRecipe(null);
        return this;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    public Set<Step> getSteps() {
        return steps;
    }

    public Recipe steps(Set<Step> steps) {
        this.steps = steps;
        return this;
    }

    public Recipe addStep(Step step) {
        this.steps.add(step);
        step.setRecipe(this);
        return this;
    }

    public Recipe removeStep(Step step) {
        this.steps.remove(step);
        step.setRecipe(null);
        return this;
    }

    public void setSteps(Set<Step> steps) {
        this.steps = steps;
    }

    public Author getAuthor() {
        return author;
    }

    public Recipe author(Author author) {
        this.author = author;
        return this;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public Recipe ingredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public Recipe categories(Set<Category> categories) {
        this.categories = categories;
        return this;
    }

    public Recipe addCategory(Category category) {
        this.categories.add(category);
        category.getRecipes().add(this);
        return this;
    }

    public Recipe removeCategory(Category category) {
        this.categories.remove(category);
        category.getRecipes().remove(this);
        return this;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Recipe recipe = (Recipe) o;
        if (recipe.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), recipe.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Recipe{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", serves=" + getServes() +
            ", preptime='" + getPreptime() + "'" +
            "}";
    }
}
