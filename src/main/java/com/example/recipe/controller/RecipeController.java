package com.example.recipe.controller;

import com.example.recipe.entity.Recipe;
import com.example.recipe.repository.RecipeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

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

    @GetMapping("/recipes/new")
    public String registerForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "register";
    }

    @PostMapping("/recipes")
    public String createRecipe(@ModelAttribute Recipe recipe,
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
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
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                               @RequestParam(value = "currentImage", required = false) String currentImage)
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

        if (imageFile != null && !imageFile.isEmpty()) {
            String base64Image = "data:" + imageFile.getContentType() + ";base64," +
                    Base64.getEncoder().encodeToString(imageFile.getBytes());
            existingRecipe.setImage(base64Image);
        } else {
            existingRecipe.setImage(currentImage);
        }

        existingRecipe.setCreatedAt(existingRecipe.getCreatedAt());
        existingRecipe.setUpdatedAt(LocalDateTime.now());

        recipeRepository.save(existingRecipe);
        return "redirect:/recipes/" + id;
    }
}
