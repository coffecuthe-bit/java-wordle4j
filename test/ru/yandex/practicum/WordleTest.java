package ru.yandex.practicum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.WordleException;

import java.nio.file.Path;
import java.util.List;


class WordleTest {

    private final WordleDictionary dictionary = new WordleDictionary();

    @Test
    public void quickComparison() {

        boolean actualResult = dictionary.quickComparison("сеНокопНитель", "сенокопнитель");
        Assertions.assertTrue(actualResult);
        actualResult = dictionary.quickComparison("сенокоПнитель1", "сенокопнитель");
        Assertions.assertFalse(actualResult);
    }

    @Test
    public void checkLength() {
        boolean actualResult = dictionary.checkLength("сеНокопнитель", "сенокопнитель");
        Assertions.assertTrue(actualResult);
        actualResult = dictionary.checkLength("сенокопнитель", "сенокопнитель1");
        Assertions.assertFalse(actualResult);
    }


    @Test
    public void comparisonWords() {
        String actual = dictionary.comparisonWords("сон", "сон");
        Assertions.assertEquals("+++", actual);

        actual = dictionary.comparisonWords("сон", "сан");
        Assertions.assertEquals("+-+", actual);

        actual = dictionary.comparisonWords("сон", "сав");
        Assertions.assertEquals("+--", actual);

        actual = dictionary.comparisonWords("сон", "нос");
        Assertions.assertEquals("^+^", actual);

        actual = dictionary.comparisonWords("сон", "нсо");
        Assertions.assertEquals("^^^", actual);

        actual = dictionary.comparisonWords("сон", "ввв");
        Assertions.assertEquals("---", actual);

        actual = dictionary.comparisonWords("герой", "гонец");
        Assertions.assertEquals("+^-^-", actual);
    }

    @Test
    public void WordleDictionaryLoaderFile() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader();
        Assertions.assertThrows(WordleException.class, () -> loader.loadFile(Path.of("error")));

        List<String> expectedWords = List.of("арбуз", "буква", "текст", "яркий");
        List<String> actualWord = loader.loadFile(Path.of("test/resources/words_ru.txt")).getWords();
        Assertions.assertEquals(expectedWords, actualWord);
    }


}
