package com.example.recipe.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

// Sudachiが作った読みを検索で比べるためのクラス。
// レシピの読み自体は変更せず、比較する時だけ正規化する。
public class SearchNormalizer {

    private static final List<String[]> ALIASES = loadAliases();

    private SearchNormalizer() {}

    public static String toKatakana(String text) {
        if (text == null) return "";
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= 'ぁ' && c <= 'ゖ') {
                c = (char) (c + 96);
            }
            result.append(c);
        }
        return result.toString();
    }

    // ひらがな・半角カタカナを統一し、オ段+ウ／エ段+イを長音に揃える。
    public static String normalize(String text) {
        if (text == null) return "";
        String kana = toKatakana(Normalizer.normalize(text, Normalizer.Form.NFKC));
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < kana.length(); i++) {
            char current = kana.charAt(i);
            if (i > 0) {
                char previous = kana.charAt(i - 1);
                if (current == 'ウ' && isO(previous)) {
                    current = 'ー';
                } else if (current == 'イ' && isE(previous)) {
                    current = 'ー';
                }
            }
            result.append(current);
        }
        return result.toString();
    }

    private static boolean isO(char c) {
        return "オコソトノホモヨロヲゴゾドボポョォ".indexOf(c) >= 0;
    }

    private static boolean isE(char c) {
        return "エケセテネヘメレゲゼデベペェ".indexOf(c) >= 0;
    }

    private static boolean kanaOnly(String text) {
        if (text == null || text.isEmpty()) return false;
        for (char c : text.toCharArray()) {
            if (!(c >= 'ぁ' && c <= 'ゖ')
                    && !(c >= 'ァ' && c <= 'ヺ')
                    && c != 'ー' && c != '・') {
                return false;
            }
        }
        return true;
    }

    // recipe=元の料理名(または材料)、recipeReading=その保存済みの読み。
    // query=検索欄への入力、queryReading=Sudachiが解析した検索入力の読み。
    public static boolean matches(String recipe, String recipeReading,
                                  String query, String queryReading) {
        return matches(recipe, recipeReading, query, queryReading, ALIASES);
    }

    // 比較テストではList.of()を渡すことで「別名なし」の動作も検証できる。
    public static boolean matches(String recipe, String recipeReading,
                                  String query, String queryReading,
                                  List<String[]> aliases) {
        if (query == null || query.isBlank()) return false;
        if (recipe == null) recipe = "";
        if (recipe.contains(query)) return true;

        String target = normalize(recipeReading);
        String keyword = kanaOnly(query) ? normalize(query) : normalize(queryReading);
        if (keyword.isEmpty()) return false;
        if (target.contains(keyword)) return true;

        // 食材を含む元のテキストに限り、指定された別名を適用する。
        for (String[] item : aliases) {
            String surface = item[0];
            String first = normalize(item[1]);
            String second = normalize(item[2]);
            if (!recipe.contains(surface) || first.isEmpty() || second.isEmpty()) continue;

            if (target.contains(first) && target.replace(first, second).contains(keyword)) {
                return true;
            }
            if (target.contains(second) && target.replace(second, first).contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private static List<String[]> loadAliases() {
        List<String[]> aliases = new ArrayList<>();
        InputStream input = SearchNormalizer.class.getResourceAsStream("/search/reading_aliases.tsv");
        if (input == null) {
            throw new IllegalStateException("search/reading_aliases.tsv が見つかりません");
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) continue;
                String[] fields = line.split("\t", -1);
                if (fields.length != 3) {
                    throw new IllegalStateException("別名辞書の列数が3ではありません: " + line);
                }
                aliases.add(fields);
            }
        } catch (IOException e) {
            throw new IllegalStateException("別名辞書を読み込めません", e);
        }
        return List.copyOf(aliases);
    }
}
