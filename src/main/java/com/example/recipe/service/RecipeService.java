package com.example.recipe.service;

import java.util.Comparator;
import java.util.List;

import com.example.recipe.entity.Recipe;
import com.example.recipe.repository.RecipeRepository;
import com.example.recipe.util.SearchNormalizer;
import com.example.recipe.util.SudachiReadingUtils;

// 依存関係　コンストラクタインジェクションから動作する想定
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final SudachiReadingUtils sudachiReadingUtils; //別の人が追加予定

        public RecipeService(RecipeRepository recipeRepository, SudachiReadingUtils sudachiReadingUtils) {
        this.recipeRepository = recipeRepository;
        this.sudachiReadingUtils = sudachiReadingUtils;
    }


    //====================================================================================
    // 用途：
    // 検索語句、カテゴリを基に、DBからリストを取り出す
    // Controllerから呼び出される
    //====================================================================================
    @SuppressWarnings("ConvertToStringSwitch") // ifelseのswich変換提案を無視する
    public List<Recipe> searchDb(String keyword, String category){

        //======================================
        // 機能：検索文字を整理
        // nullなら""それ以外はトリムする　検索文字列
        // queryが""ならqueryReadingに""を取得　それ以外はすだちクラスで読み仮名を取得
        //======================================
    String query = keyword == null ? "" : keyword.trim();
    String queryReading = query.isEmpty() ? "" : sudachiReadingUtils.toKatakanaReading(query);

        //======================================
        // 機能：検索文字を使用したリスト作成
        // 1:全件取得
        // 2:カテゴリ一致
        // 3:入力文字で検索
        // 4:カナ文字で検索
        //======================================
    @SuppressWarnings("null") // エンティティ側で更新日は自動入力されるため、UpdatedAtにNullは来ません。
    List<Recipe> matches = recipeRepository.findAll().stream()
        .filter( // カテゴリに一致するレシピを取得
            recipe -> matchesCategory(recipe, category)//Trueのレコードを抽出。
        )
        .filter(
            recipe -> query.isEmpty() // 検索欄が空欄なら何もしない　全件取得のまま
                || SearchNormalizer.matches(
                    recipe.getTitle(),
                    recipe.getReading(),
                    query, queryReading)
                || SearchNormalizer.matches(
                    recipe.getIngredients(),
                    recipe.getIngredientsReading(),
                    query, queryReading)
                )
        .sorted(// HTMLに渡されるリストは更新日時が新しい順（降順）
            Comparator.comparing(Recipe::getUpdatedAt)
            .reversed())
        .toList();
    return matches;
    }

        //======================================
        // 機能：カテゴリで絞り込みを行う
        // 1行分の処理
        // true falseを返していく
        //======================================
    private boolean matchesCategory(Recipe recipe, String category) {
        // カテゴリが未選択=Nullの場合は"すべて"に変換して手続きを進める。
        if (category == null || "すべて".equals(category))
            return true;
        if ("その他".equals(category)) {
            // "主菜", "副菜", "汁物"とカテゴリが一致しないならばtrueを返す
            return !List.of("主菜", "副菜", "汁物").contains(recipe.getCategory());
        }
        return category.equals(recipe.getCategory());
    }

}
