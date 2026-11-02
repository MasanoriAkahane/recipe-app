package com.example.recipe.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

    @Lob
    @Column(name = "reading", columnDefinition = "TEXT")
    private String reading;

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
    @Column(name = "ingredients_reading", columnDefinition = "TEXT")
    private String ingredientsReading;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String image;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 既存のフィールド群の下に追加
    @Column(name = "reading")
    private String reading;

    // Getter / Setter を追加（Lombok の @Data を使っている場合は自動生成されるため不要）
    public String getReading() {
        return reading;
    }
    public void setReading(String reading) {
        this.reading = reading;
    }



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

    public String getReading() { return reading; }
    public void setReading(String reading) { this.reading = reading; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getCookingTime() { return cookingTime; }
    public void setCookingTime(Integer cookingTime) { this.cookingTime = cookingTime; }

    public Integer getFavoriteLevel() { return favoriteLevel; }
    public void setFavoriteLevel(Integer favoriteLevel) { this.favoriteLevel = favoriteLevel; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getIngredientsReading() { return ingredientsReading; }
    public void setIngredientsReading(String ingredientsReading) { this.ingredientsReading = ingredientsReading; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}


