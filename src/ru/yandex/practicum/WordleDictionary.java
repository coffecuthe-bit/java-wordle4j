package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    public static final char CORRECT_PLACE_LETTER = '+';
    public static final char ABSENT_LETTER = '-';
    public static final char WRONG_PLACE_LETTER = '^';

    public static final int WORD_LENGTH = 5;

    private static final String CYRILLIC_PATTERN = "[а-я]+";

    private final List<String> words;

    private final Random random = new Random();

    public WordleDictionary(List<String> words) {
        this.words = new ArrayList<>(words);
    }

    public List<String> getWords() {
        return words;
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public boolean isEmpty() {
        return words.isEmpty();
    }

    public String randomWord() {
        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст, невозможно выбрать слово");
        }
        return words.get(random.nextInt(words.size()));
    }

    public static String normalize(String word) {
        if (word == null) {
            return "";
        }
        return word.trim().toLowerCase().replace('ё', 'е');
    }

    public static boolean isValidFormat(String word) {
        return word != null && word.length() == WORD_LENGTH && word.matches(CYRILLIC_PATTERN);
    }

    public boolean quickComparison(String actual, String example) {
        return actual.equals(example);
    }

    public boolean checkLength(String actual, String example) {
        return example.length() == actual.length() && !actual.isBlank();
    }

    public String comparisonWords(String example, String actual) {
        if (example.length() != actual.length()) {
            throw new IllegalArgumentException("Слова должны быть одинаковой длины");
        }
        char[] marks = new char[actual.length()];
        StringBuilder pool = new StringBuilder(example);

        for (int i = 0; i < actual.length(); i++) {
            if (actual.charAt(i) == pool.charAt(i)) {
                marks[i] = CORRECT_PLACE_LETTER;
                pool.setCharAt(i, '\0');
            }
        }

        for (int i = 0; i < actual.length(); i++) {
            if (marks[i] != 0) {
                continue;
            }
            char c = actual.charAt(i);
            int idx = pool.indexOf(String.valueOf(c));
            if (idx >= 0) {
                marks[i] = WRONG_PLACE_LETTER;
                pool.setCharAt(idx, '\0');
            } else {
                marks[i] = ABSENT_LETTER;
            }
        }
        return new String(marks);
    }
}
