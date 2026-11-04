package com.example.recipe.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.recipe.entity.Recipe;
import com.example.recipe.repository.RecipeRepository;
import com.example.recipe.service.RecipeService;
import com.example.recipe.util.KuromojiUtils;

@Controller
public class RecipeController {

    private final RecipeRepository recipeRepository;

    public RecipeController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @GetMapping("/")
    public String root(
        @RequestParam(required = false) String searchText, // 値が渡されない場合でも動作する。nullを受け取る。
        @RequestParam(required = false) String category, // 値が渡されない場合でも動作する。nullを受け取る。
        @RequestParam(defaultValue = "0") int sort, // 値が渡されない場合、初期値0を受け取る。 int はNullを受け取れない型の為。
        // name="item"：HTML側の要素の名前
        // value = "item"：取得対象となるリクエストパラメータ名
        Model model) {

            List<Recipe> list;
            //==========================================
            // kuromojiで日本語→かなに変換。
            //==========================================
            String keyword = searchText;


            if (searchText == null){keyword = "未設定";} // Nullを未設定に変換
            if (category == null){category = "すべて";} // Nullをすべてに変換

            RecipeService service = new RecipeService(recipeRepository);
            list = service.concatRequest(keyword, category, sort);

            // model.addAttribute("data", list);
            // list.html側がrecipesを参照しているため名前を合わせる
            model.addAttribute("recipes", list);
            return "list";
        }

    @GetMapping("/recipes")
    public String list(Model model) {
        model.addAttribute("recipes", recipeRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt")));
        return "list";
    }

    @GetMapping("/recipes/new")
    public String registerForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "register";
    }

    @PostMapping("/recipes")
    public String createRecipe(@ModelAttribute Recipe recipe,
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile)
            throws IOException {

        if (imageFile != null && !imageFile.isEmpty()) {
            String base64Image = "data:" + imageFile.getContentType() + ";base64," +
                    Base64.getEncoder().encodeToString(imageFile.getBytes());
            recipe.setImage(base64Image);
        } else {
            recipe.setImage("");
        }

        recipeRepository.save(recipe);
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
