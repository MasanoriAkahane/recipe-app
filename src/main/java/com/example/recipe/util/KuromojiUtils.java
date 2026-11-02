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

        StringBuilder result = new StringBuilder();

        for (Token token : TOKENIZER.tokenize(text)) {
            String reading = token.getReading();

            if (reading == null || reading.equals("*")) {
                result.append(hiraganaToKatakana(token.getSurface()));
            } else {
                result.append(reading);
            }
        }

        return hiraganaToKatakana(result.toString());
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
