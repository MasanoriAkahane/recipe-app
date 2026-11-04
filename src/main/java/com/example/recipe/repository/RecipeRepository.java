package com.example.recipe.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.recipe.entity.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    //==========================================
    // 主菜、副菜、汁物、のどれかを検索
    //==========================================
    List<Recipe> findByReadingContainingAndCategory(String keyWord, String category);
    List<Recipe> findByIngredientsReadingContainingAndCategory(String keyWord, String category);

    //==========================================
    // 主菜、副菜、汁物、以外を検索
    //==========================================
    List<Recipe> findByCategoryNotInAndReadingContaining(Collection<String> categories, String keyWord);
    List<Recipe> findByCategoryNotInAndIngredientsReadingContaining(Collection<String> categories, String keyWord);

    //==========================================
    // 全て検索
    //==========================================
    List<Recipe> findByReadingContaining(String keyWord);
    List<Recipe> findByIngredientsReadingContaining(String keyWord);




}
