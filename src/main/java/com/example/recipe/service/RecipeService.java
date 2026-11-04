package com.example.recipe.service;

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


    //====================================================================================
    // 用途：
    // 検索語句、カテゴリ、ソート方法を基に、DBへのメソッドを作成する。
    // Conrollerから呼び出される。
    //====================================================================================
    public List<Recipe> searchDb(String keyword, String category,int sort){
        List<Recipe> list;

        if ("すべて".equals(category)){// "すべて"が選択された場合の手続き ※nullは呼び出し前に"すべて"に変換されてからくる。
            List<Recipe> list1 = recipeRepository.findByReadingContaining(keyword);
            List<Recipe> list2 = recipeRepository.findByIngredientsReadingContaining(keyword);
            list = concatRequestAndSort(sort, list1, list2);
        }
        else if ("その他".equals(category)){// カテゴリに"すべて""その他"以外が入力された場合の手続き
            List<Recipe> list1 = recipeRepository.findByCategoryNotInAndReadingContaining(List.of("主菜", "副菜", "汁物"),keyword);
            List<Recipe> list2 = recipeRepository.findByCategoryNotInAndIngredientsReadingContaining(List.of("主菜", "副菜", "汁物"),keyword);
            list = concatRequestAndSort(sort, list1, list2);
        }
        else{// 主菜副菜汁物が来た時に動作する。
            List<Recipe> list1 = recipeRepository.findByReadingContainingAndCategory(keyword,category);
            List<Recipe> list2 = recipeRepository.findByIngredientsReadingContainingAndCategory(keyword,category);
            list = concatRequestAndSort(sort, list1, list2);
        }
        return list;
    }

    //====================================================================================
    // 用途：
    // 2種類のlistを接続する
    // 接続後のリストをsort値を参照して並び替える
    // 更新日時が新しい順（デフォルト: updatedAt desc）
    //====================================================================================
    private static List<Recipe> concatRequestAndSort(int sort, List<Recipe> list1, List<Recipe> list2) {

    Comparator<Recipe> comparator = switch (sort) {
        case 0 -> Comparator.comparing(Recipe::getUpdatedAt).reversed();// 更新日時が新しい順（降順）
        case 1 -> Comparator.comparing(Recipe::getUpdatedAt);// 更新日時が新しい順（昇順）
        case 2 -> Comparator.comparing(Recipe::getFavoriteLevel).reversed();// お気に入り度が高い順（降順）
        case 3 -> Comparator.comparing(Recipe::getFavoriteLevel);// お気に入り度が高い順（昇順）
        case 4 -> Comparator.comparing(Recipe::getCookingTime).reversed();// 調理時間が短い順（降順）
        case 5 -> Comparator.comparing(Recipe::getCookingTime);// 調理時間が短い順（昇順）
        case 6 -> Comparator.comparing(Recipe::getTitle).reversed();// 料理名順（降順）
        case 7 -> Comparator.comparing(Recipe::getTitle);// 料理名順（昇順）
        // ソートが未初期化の場合の処理
        default -> Comparator.comparing(Recipe::getUpdatedAt).reversed();// ここは所定の数字以外が来ることは想定していない。
    };
    return Stream.concat(list1.stream(), list2.stream())
            .distinct()
            .sorted(comparator)
            .toList();
}

}
