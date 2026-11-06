package com.example.recipe.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.ArrayList;
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
    public String root(Model model) {
            model.addAttribute("recipes", recipeRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt")));
            return "list";
        }

    @GetMapping("/recipes")
    public String list(
        @RequestParam(name = "searchText", required = false) String searchText, // 値が渡されない場合でも動作する。nullを受け取る。
        @RequestParam(name = "category", required = false) String category, // 値が渡されない場合でも動作する。nullを受け取る。
        @RequestParam(defaultValue = "0") int sort, // 値が渡されない場合、初期値0を受け取る。 int はNullを受け取れない型の為。
        Model model) {

        List<Recipe> list;
        //==========================================
        // kuromojiで日本語→かなに変換。
        //==========================================
        String keyword = KuromojiUtils.toKatakanaReading(searchText);
        if (category == null){category = "すべて";} // Nullをすべてに変換

        RecipeService service = new RecipeService(recipeRepository);
        list = service.searchDb(keyword, category, sort);
        model.addAttribute("recipes", list);
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

        recipe.setReading(
            KuromojiUtils.toKatakanaReading(recipe.getTitle())
        );
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
        recipe.setIngredientsReading(
            KuromojiUtils.toKatakanaReading(recipe.getIngredients())
        );

        recipeRepository.save(recipe);
        return "redirect:/recipes";
    }

    // 詳細表示
    @GetMapping("/recipes/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe == null) {
            return "redirect:/recipes";
        }

        // 材料を1行ずつリストに入れる（人数計算はJavaScript側で行う）
        List<String> ingredientLines = new ArrayList<>();
        if (recipe.getIngredients() != null) {
            String[] lines = recipe.getIngredients().split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    ingredientLines.add(line.trim());
                }
            }
        }

        model.addAttribute("recipe", recipe);
        model.addAttribute("ingredientLines", ingredientLines);
        return "detail";
    }

    // 削除
    @PostMapping("/recipes/{id}/delete")
    public String deleteRecipe(@PathVariable Long id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
        }
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
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe == null) {
            return "redirect:/recipes";
        }
        model.addAttribute("recipe", recipe);
        return "edit";
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
        existingRecipe.setServings(recipe.getServings());
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
