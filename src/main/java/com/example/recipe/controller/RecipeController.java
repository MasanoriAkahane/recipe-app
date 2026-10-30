package com.example.recipe.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.recipe.entity.Recipe;
import com.example.recipe.repository.RecipeRepository;

@Controller
public class RecipeController {

    private final RecipeRepository recipeRepository;

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
