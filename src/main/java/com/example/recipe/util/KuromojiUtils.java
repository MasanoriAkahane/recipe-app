package com.example.recipe.util;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

public class KuromojiUtils {

    private static final Tokenizer TOKENIZER = new Tokenizer();

    private KuromojiUtils() {
    }

    public static String toKatakanaReading(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String result = "";

        for (Token token : TOKENIZER.tokenize(text)) {
            String reading = token.getReading();

            if (reading == null || reading.equals("*")) {
                result += hiraganaToKatakana(token.getSurface());
            } else {
                result += reading;
            }
        }

        return hiraganaToKatakana(result);
    }

    private static String hiraganaToKatakana(String text) {
        String result = "";

        for (char c : text.toCharArray()) {
            if (c >= 'ぁ' && c <= 'ゖ') {
                // ひらがなとカタカナの文字コードの差
                result += (char) (c + 96);
            } else {
                result += c;
            }
        }

        return result;
    }
}
