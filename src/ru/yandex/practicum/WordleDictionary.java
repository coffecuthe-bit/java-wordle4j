package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private static final String CORRECT_PLACE_LETTER = "+";

    private static final String ABSENT_LETTER = "-";

    private static final String WRONG_PLACE_LETTER = "^";

    private List<String> words;

    public WordleDictionary(List<String> words) {
        this.words = words;
    }

    public WordleDictionary() {
    }

    public List<String> getWords() {
        if (words == null) {
            return new ArrayList<>();
        }
        return words;
    }

    public String comparisonWords(String example, String actual) {
        StringBuilder result = new StringBuilder();

        StringBuilder examplePool = new StringBuilder(example);
        char[] marks = new char[actual.length()];

        for (int i = 0; i < actual.length(); i++) {
            if (i < example.length() && example.charAt(i) == actual.charAt(i)) {
                marks[i] = CORRECT_PLACE_LETTER.charAt(0);
                examplePool.setCharAt(i, '\0'); // помечаем как использованную
            }
        }

        for (int i = 0; i < actual.length(); i++) {
            if (marks[i] != 0) continue;

            char actualChar = actual.charAt(i);
            int idx = examplePool.indexOf(String.valueOf(actualChar));

            if (idx >= 0) {
                marks[i] = WRONG_PLACE_LETTER.charAt(0);
                examplePool.setCharAt(idx, '\0');
            } else {
                marks[i] = ABSENT_LETTER.charAt(0);
            }
        }

        result.append(marks);
        return result.toString();
    }

    public boolean quickComparison(String actual, String example) {
        return actual.equals(example);
    }

    public boolean checkLength(String actual, String example) {
        return example.length() == actual.length() && !actual.isBlank();
    }
}
