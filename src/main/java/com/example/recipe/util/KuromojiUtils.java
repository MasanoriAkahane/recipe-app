package com.example.recipe.util;

import java.util.List;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

public class KuromojiUtils {

    // Tokenizerは生成コストがあるためシングルトン（1つだけ保持）で再利用
    private static final Tokenizer TOKENIZER = new Tokenizer();

    /**
     * 文字列（漢字・ひらがな・混在）をカタカナの読みに変換する
     * 例: "玉ねぎ" -> "タマネギ", "玉葱" -> "タマネギ", "たまねぎ" -> "タマネギ"
     */
    public static String toKatakanaReading(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        List<Token> tokens = TOKENIZER.tokenize(text);
        StringBuilder reading = new StringBuilder();
        for (Token token : tokens) {
            String tokenReading = token.getReading();
            // 辞書に読みがない文字（記号・英数等）は表層形（元の文字）をそのまま使用
            if (tokenReading == null || "*".equals(tokenReading)) {
                reading.append(token.getSurface());
            } else {
                reading.append(tokenReading);
            }
        }
        return reading.toString();
    }
}