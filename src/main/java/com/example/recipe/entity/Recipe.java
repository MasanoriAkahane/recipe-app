package com.example.recipe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String category;

    @NotNull
    @Min(1)
    @Column(name = "cooking_time", nullable = false)
    private Integer cookingTime;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "favorite_level", nullable = false)
    private Integer favoriteLevel;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String ingredients;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String image;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Recipe() {}

    public Recipe(String title, String category, Integer cookingTime, Integer favoriteLevel, 
                  String ingredients, String instructions, String image) {
        this.title = title;
        this.category = category;
        this.cookingTime = cookingTime;
        this.favoriteLevel = favoriteLevel;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.image = image;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getCookingTime() { return cookingTime; }
    public void setCookingTime(Integer cookingTime) { this.cookingTime = cookingTime; }

    public Integer getFavoriteLevel() { return favoriteLevel; }
    public void setFavoriteLevel(Integer favoriteLevel) { this.favoriteLevel = favoriteLevel; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
