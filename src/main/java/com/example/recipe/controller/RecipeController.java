package com.example.recipe.controller;

import java.io.IOException;
import java.util.Base64;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.recipe.entity.Recipe;
import com.example.recipe.repository.RecipeRepository;

@Controller
public class RecipeController {

    private final RecipeRepository recipeRepository;

    public RecipeController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/recipes";
    }

    @GetMapping("/recipes")
    public String list(Model model) {
        model.addAttribute("recipes", recipeRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt")));
        return "list";
    }

    // 【新規登録画面の表示】フォームバインド用の空Recipeオブジェクトを渡して画面遷移
    @GetMapping("/recipes/new")
    public String registerForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "register";
    }

// 【新規登録処理】画像Base64変換、Kuromojiによるカタカナ読み自動生成、DB保存を実行
    @PostMapping("/recipes")
    public String createRecipe(@ModelAttribute Recipe recipe,
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        
        // 1. 画像のBase64エンコード（Data URI形式）
        if (imageFile != null && !imageFile.isEmpty()) {
            String base64Image = "data:" + imageFile.getContentType() + ";base64," +
                    Base64.getEncoder().encodeToString(imageFile.getBytes());
            recipe.setImage(base64Image);
        } else {
            recipe.setImage("");
        }

        /* 
         * 森口さんの KuromojiUtils.java 完成・developマージ待ちのため一時コメントアウト
         *
        // 2. 表記揺れ検索用：料理名のカタカナ読みを自動生成
        if (recipe.getTitle() != null && !recipe.getTitle().isBlank()) {
            String reading = KuromojiUtils.toKatakana(recipe.getTitle());
            recipe.setReading(reading);
        }

        // 3. 表記揺れ検索用：材料のカタカナ読みを自動生成
        if (recipe.getIngredients() != null && !recipe.getIngredients().isBlank()) {
            String ingredientsReading = KuromojiUtils.toKatakana(recipe.getIngredients());
            recipe.setIngredientsReading(ingredientsReading);
        }
        */

        // 4. データベースへの保存
        recipeRepository.save(recipe);

        // 登録完了後は一覧画面へリダイレクト
        return "redirect:/recipes";
    }
}