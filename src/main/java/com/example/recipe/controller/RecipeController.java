package com.example.recipe.controller;

import com.example.recipe.entity.Recipe;
import com.example.recipe.repository.RecipeRepository;
import com.example.recipe.util.KuromojiUtils;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class RecipeController {


    public RecipeController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @GetMapping("/")
    public ModelAndView root(
        @RequestParam(required = false) String searchText, // 値が渡されない場合でも動作する。nullを受け取る。
        @RequestParam(required = false) String category, // 値が渡されない場合でも動作する。nullを受け取る。
        @RequestParam(defaultValue = "0") int sort, // 値が渡されない場合、初期値0を受け取る。 int はNullを受け取れない型の為。
        ModelAndView mav) {
            //==========================================
            // String = 検索窓の文字列。
            // int = 表示する種類（すべて,主菜,副菜,汁物,その他）
            // int = 画面側のソート情報（更新日時新古、評価高低）
            //==========================================
            List<Recipe> list;
            switch (category){
                // 全て
                case "すべて" -> {// レシピ名or カテゴリでor検索　カテゴリ名はStringで来ることを想定。
                    list = recipeRepository.findAllByOrderByUpdatedAtDesc();}
                // 主菜
                case "主菜" -> {list = recipeRepository.findByCategoryOrderByUpdatedAtDesc(category);}
                // 副菜
                case "副菜" -> {list = recipeRepository.findAll();}
                // 汁物
                case "汁物" -> {list = recipeRepository.findAll();}
                // その他
                case "その他" -> {list = recipeRepository.findAll();}
                default -> {list = recipeRepository.findAllByOrderByUpdatedAtDesc();}
            }
            mav.addObject("data", list);
            mav.setViewName("list");
            return mav;
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

    // 編集画面表示処理
    @GetMapping("/recipes/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        return recipeRepository.findById(id)
                .map(recipe -> {
                    model.addAttribute("recipe", recipe);
                    return "edit";
                })
                .orElse("redirect:/recipes");
    }

    // レシピ更新処理
    @PostMapping("/recipes/{id}/update")
    public String updateRecipe(@PathVariable Long id,
                               @ModelAttribute Recipe recipe,
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile)
            throws IOException {

        Recipe existingRecipe = recipeRepository.findById(id).orElse(null);
        if (existingRecipe == null) {
            return "redirect:/recipes";
        }

        existingRecipe.setTitle(recipe.getTitle());
        existingRecipe.setCategory(recipe.getCategory());
        existingRecipe.setCookingTime(recipe.getCookingTime());
        existingRecipe.setFavoriteLevel(recipe.getFavoriteLevel());
        existingRecipe.setIngredients(recipe.getIngredients());
        existingRecipe.setInstructions(recipe.getInstructions());

        existingRecipe.setReading(KuromojiUtils.toKatakanaReading(recipe.getTitle()));
        existingRecipe.setIngredientsReading(KuromojiUtils.toKatakanaReading(recipe.getIngredients()));

        if (imageFile != null && !imageFile.isEmpty()) {
            String base64Image = "data:" + imageFile.getContentType() + ";base64," +
                    Base64.getEncoder().encodeToString(imageFile.getBytes());
            existingRecipe.setImage(base64Image);
        }

        recipeRepository.save(existingRecipe);
        return "redirect:/recipes";
    }

}
