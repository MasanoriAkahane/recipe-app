package com.example.recipe.util;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import java.util.List; // 1. importを追加

public class KuromojiUtils {

    private static final Tokenizer TOKENIZER = new Tokenizer();

    private KuromojiUtils() {
    }

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

        // 2. 変数名を reading.toString() に修正
        return hiraganaToKatakana(reading.toString());
    }

    private static String hiraganaToKatakana(String text) {
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (c >= 'ぁ' && c <= 'ゖ') {
                result.append((char) (c + 0x60));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}