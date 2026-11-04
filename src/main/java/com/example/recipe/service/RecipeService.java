package com.example.recipe.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;


import com.example.recipe.entity.Recipe;
import com.example.recipe.repository.RecipeRepository;

public class RecipeService {

    private final RecipeRepository recipeRepository;

        public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }


    //==========================================
    // 用途：
    // 検索結果を2回実施する。
    //==========================================
    public List<Recipe> concatRequest(String keyword, String category,int sort){
        List<Recipe> list;

        // "すべて"が選択された場合の手続き
        if ("すべて".equals(category)){
                    switch (sort){
                        // 元コードはRepository側のAnd条件に対して引数が1つだったため、同じkeywordを2つ渡す
                        // case 0 -> {list = recipeRepository.findByReadingAndIngredientsReadingOrderByUpdatedAtDesc(keyword);}
                        case 0 -> {list = recipeRepository.findByReadingAndIngredientsReadingOrderByUpdatedAtDesc(keyword,keyword);}
                        // case 1 -> {list = recipeRepository.findByReadingAndIngredientsReadingOrderByFavoriteLevelDesc(keyword);}
                        case 1 -> {list = recipeRepository.findByReadingAndIngredientsReadingOrderByFavoriteLevelDesc(keyword,keyword);}
                        // case 2 -> {list = recipeRepository.findByReadingAndIngredientsReadingOrderByCookingTime(keyword);}
                        case 2 -> {list = recipeRepository.findByReadingAndIngredientsReadingOrderByCookingTime(keyword,keyword);}
                        // case 3 -> {list = recipeRepository.findByReadingAndIngredientsReadingOrderByTitle(keyword);}
                        case 3 -> {list = recipeRepository.findByReadingAndIngredientsReadingOrderByTitle(keyword,keyword);}
                            // ソートが見初期化の場合の処理　※0-3以外が入ったとき。
                        // default -> {list = recipeRepository.findByReadingAndIngredientsReadingOrderByUpdatedAtDesc(keyword);
                        default -> {list = recipeRepository.findByReadingAndIngredientsReadingOrderByUpdatedAtDesc(keyword,keyword);
                        System.out.println("sort方法不明 ここはNullは想定されていない。");}
            }
        }
        // カテゴリに"すべて"以外が入力された場合の手続き
        else{
            List<Recipe> list1 = recipeRepository.findByReadingAndCategory(keyword,category);
            List<Recipe> list2 = recipeRepository.findByIngredientsReadingAndCategory(keyword,category);
            switch (sort){
                case 0 -> {list = concatRequestUpdatedAt(list1,list2);}
                case 1 -> {list = RecipeService.concatRequestFavoriteLevel(list1,list2);}
                case 2 -> {list = RecipeService.concatRequestCookingTime(list1,list2);}
                case 3 -> {list = RecipeService.concatRequestTitle(list1,list2);}
                    // ソートが見初期化の場合の処理　※0-3以外が入ったとき。
                default -> {list = concatRequestUpdatedAt(list1,list2);
                        System.out.println("sort方法不明 ここはNullは想定されていない。");}
                }
        }
        return list;
    }

    //==========================================
    // 用途：
    // 検索結果リストを接続する
    // 並び替える。
    //==========================================
    // 更新日時が新しい順（デフォルト: updatedAt,desc）
    public static List<Recipe> concatRequestUpdatedAt(List<Recipe> list1,List<Recipe> list2){
        List<Recipe> list = Stream
        .concat(list1.stream(),list2.stream())
        .distinct()
        .sorted(Comparator.comparing(Recipe::getUpdatedAt).reversed())
        // AIは無理にNull対策を入れる必要はないと示している。更新日時はNullにならない認識。
        .toList();
        return list;
    }
    // お気に入り度が高い順（favoriteLevel,desc）
    public static List<Recipe> concatRequestFavoriteLevel(List<Recipe> list1,List<Recipe> list2){
        List<Recipe> list = Stream
        .concat(list1.stream(),list2.stream())
        .distinct()
        .sorted(Comparator.comparing(Recipe::getFavoriteLevel).reversed())
        .toList();
        return list;
    }
    // 調理時間が短い順（cookingTime,asc）
    public static List<Recipe> concatRequestCookingTime(List<Recipe> list1,List<Recipe> list2){
        List<Recipe> list = Stream
        .concat(list1.stream(),list2.stream())
        .distinct()
        .sorted(Comparator.comparing(Recipe::getCookingTime))
        .toList();
        return list;
    }
    // 料理名順（title,asc）
    public static List<Recipe> concatRequestTitle(List<Recipe> list1,List<Recipe> list2){
        List<Recipe> list = Stream
        .concat(list1.stream(),list2.stream())
        .distinct()
        .sorted(Comparator.comparing(Recipe::getTitle))
        .toList();
        return list;
    }

}
