package com.example.recipe.controller;

import com.example.recipe.entity.Recipe;
import com.example.recipe.repository.RecipeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
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
}
