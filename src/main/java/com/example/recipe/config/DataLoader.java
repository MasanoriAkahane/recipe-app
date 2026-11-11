package com.example.recipe.config;

import com.example.recipe.entity.Recipe;
import com.example.recipe.repository.RecipeRepository;
import com.example.recipe.util.SudachiReadingUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final RecipeRepository recipeRepository;
    private final SudachiReadingUtils sudachiReadingUtils;

    public DataLoader(RecipeRepository recipeRepository, SudachiReadingUtils sudachiReadingUtils) {
        this.recipeRepository = recipeRepository;
        this.sudachiReadingUtils = sudachiReadingUtils;
    }

    @Override
    public void run(String... args) throws Exception {
        if (recipeRepository.count() == 0) {
            saveRecipe(new Recipe(
                "鮭の塩焼き定食", "主菜", 20, 5,
                "生鮭 2切れ\n塩 少々\n大根おろし 適量",
                "1. 鮭に塩を振って10分置く\n2. 水気を拭き取りグリルで両面焼く\n3. 大根おろしを添えて盛り付ける",
                ""
            ));
            saveRecipe(new Recipe(
                "豚の生姜焼き", "主菜", 15, 4,
                "豚ロース肉 200g\n玉ねぎ 1/2個\n生姜 1片\n醤油 大さじ2\nみりん 大さじ2",
                "1. 玉ねぎと生姜をスライスする\n2. フライパンで肉と玉ねぎを炒める\n3. 調味料を加えて照りが出るまで絡める",
                ""
            ));
            saveRecipe(new Recipe(
                "彩り野菜の和風カレー", "主菜", 35, 4,
                "鶏もも肉 150g\nかぼちゃ 100g\nなす 1本\n和風だし 400ml\nカレールー 2片",
                "1. 具材を一口大に切る\n2. 鍋で肉と野菜を炒め、出汁で煮込む\n3. ルーを溶いてとろみがつくまで煮込む",
                ""
            ));
            saveRecipe(new Recipe(
                "ほうれん草のごま和え", "副菜", 10, 3,
                "ほうれん草 1束\n白すりごま 大さじ2\n醤油 小さじ1\n砂糖 小さじ1",
                "1. ほうれん草を茹でて冷水にとり、水気を絞る\n2. 3cm幅に切る\n3. ボウルで調味料と和える",
                ""
            ));
            System.out.println(">>> 初期レシピデータ4件を自動登録しました。");
        }

        for (Recipe recipe : recipeRepository.findAll()) {
            boolean needsUpdate = false;

            if (recipe.getReading() == null) {
                recipe.setReading(sudachiReadingUtils.toKatakanaReading(recipe.getTitle()));
                needsUpdate = true;
            }

            if (recipe.getIngredientsReading() == null) {
                recipe.setIngredientsReading(sudachiReadingUtils.toKatakanaReading(recipe.getIngredients()));
                needsUpdate = true;
            }

            if (needsUpdate) {
                recipeRepository.save(recipe);
            }
        }
    }

    private void saveRecipe(Recipe recipe) {
        recipe.setReading(sudachiReadingUtils.toKatakanaReading(recipe.getTitle()));
        recipe.setIngredientsReading(sudachiReadingUtils.toKatakanaReading(recipe.getIngredients()));
        recipeRepository.save(recipe);
    }
}
