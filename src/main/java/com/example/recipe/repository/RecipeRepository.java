package com.example.recipe.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.recipe.entity.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByTitleContaining(String keyword, Sort sort);
    List<Recipe> findAllByOrderByUpdatedAtDesc();
    List<Recipe> findByCategoryOrderByUpdatedAtDesc(String category);

    //==========================================
    // 最新案 検索を2件実施し、リストを接続する
    //==========================================
    List<Recipe> findByReadingAndCategory(
    String title,
    String category
    );
    List<Recipe> findByIngredientsReadingAndCategory(
    String readingKeyword,
    String category
    );
    //==========================================
    // 全て検索
    //==========================================
    // 元コードはAndで2項目を指定しているため引数が1つでは合わない
    // List<Recipe> findByReadingAndIngredientsReadingOrderByUpdatedAtDesc(String searchText);
    List<Recipe> findByReadingAndIngredientsReadingOrderByUpdatedAtDesc(String reading, String ingredientsReading);

    // List<Recipe> findByReadingAndIngredientsReadingOrderByFavoriteLevelDesc(String searchText);
    List<Recipe> findByReadingAndIngredientsReadingOrderByFavoriteLevelDesc(String reading, String ingredientsReading);

    // List<Recipe> findByReadingAndIngredientsReadingOrderByCookingTime(String searchText);
    List<Recipe> findByReadingAndIngredientsReadingOrderByCookingTime(String reading, String ingredientsReading);

    // List<Recipe> findByReadingAndIngredientsReadingOrderByTitle(String searchText);
    List<Recipe> findByReadingAndIngredientsReadingOrderByTitle(String reading, String ingredientsReading);
}
