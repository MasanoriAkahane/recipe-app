package com.example.recipe.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.worksap.nlp.sudachi.Config;
import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.Tokenizer;

import jakarta.annotation.PreDestroy;

// 登録・編集時の「読み」と、検索語の「読み」を生成するクラス。
@Component
public class SudachiReadingUtils {

    private final Dictionary dictionary;

    // Spring Boot起動時に1回だけ辞書を読み込む。
    public SudachiReadingUtils(@Value("${sudachi.dictionary.path}") String dictionaryPath)
            throws IOException {
        Path path = Path.of(dictionaryPath).toAbsolutePath();
        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException("Sudachi辞書がありません: " + path);
        }
        Config config = Config.defaultConfig().systemDictionary(path);
        this.dictionary = Dictionary.load(config);
    }

    public String toKatakanaReading(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        StringBuilder reading = new StringBuilder();
        // tokenizerは各呼び出しで取得する。辞書本体は共有する。
        Tokenizer tokenizer = dictionary.tokenizer();
        for (Morpheme word : tokenizer.tokenize(Tokenizer.SplitMode.C, text)) {
            String kana = word.readingForm();
            if (kana == null || kana.isEmpty()) {
                kana = word.surface();
            }
            reading.append(kana);
        }
        return SearchNormalizer.toKatakana(reading.toString());
    }

    @PreDestroy
    public void close() {
        try {
            dictionary.close();
        } catch (IOException e) {
            System.err.println("Sudachi辞書の終了時エラー: " + e.getMessage());
        }
    }
}
