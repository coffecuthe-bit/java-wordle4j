package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.GameException;
import ru.yandex.practicum.exception.InvalidWordException;
import ru.yandex.practicum.exception.WordleException;
import ru.yandex.practicum.exception.WordNotFoundException;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private static PrintWriter log;

    private WordleDictionary dictionary;

    @BeforeAll
    static void initLog() {
        log = new PrintWriter(System.out);
    }

    @BeforeEach
    void initDictionary() {
        dictionary = new WordleDictionary(List.of("арбуз", "буква", "текст", "ягоды"));
    }

    @Test
    public void quickComparison() {
        assertTrue(dictionary.quickComparison(WordleDictionary.normalize("БуквА"), "буква"));
        assertFalse(dictionary.quickComparison("букв", "буква"));
        assertFalse(dictionary.quickComparison("сенокоПнитель1", "сенокопнитель"));
    }

    @Test
    public void checkLength() {
        assertTrue(dictionary.checkLength("сеНокопнитель", "сенокопнитель"));
        assertFalse(dictionary.checkLength("сенокопнитель", "сенокопнитель1"));
    }

    @Test
    public void normalize() {
        assertEquals("елка", WordleDictionary.normalize("Ёлка"));
        assertEquals("буква", WordleDictionary.normalize("  Буква "));
    }

    @Test
    public void isValidFormat() {
        assertTrue(WordleDictionary.isValidFormat("буква"));
        assertFalse(WordleDictionary.isValidFormat("буквы1"));
        assertFalse(WordleDictionary.isValidFormat("бук"));
        assertFalse(WordleDictionary.isValidFormat(""));
        assertFalse(WordleDictionary.isValidFormat(null));
        assertFalse(WordleDictionary.isValidFormat("table"));
    }

    @Test
    public void comparisonWords() {
        assertEquals("+++", dictionary.comparisonWords("сон", "сон"));
        assertEquals("+-+", dictionary.comparisonWords("сон", "сан"));
        assertEquals("+--", dictionary.comparisonWords("сон", "сав"));
        assertEquals("^+^", dictionary.comparisonWords("сон", "нос"));
        assertEquals("^^^", dictionary.comparisonWords("сон", "нсо"));
        assertEquals("---", dictionary.comparisonWords("сон", "ввв"));
        assertEquals("+^-^-", dictionary.comparisonWords("герой", "гонец"));
        assertEquals("-+---", dictionary.comparisonWords("гонец", "ооооо"));
        assertEquals("--+++", dictionary.comparisonWords("гонец", "синец"));
        assertThrows(IllegalArgumentException.class, () -> dictionary.comparisonWords("сон", "сони"));
    }

    @Test
    public void dictionaryLoaderFile() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        assertThrows(WordleException.class, () -> loader.loadFile(Path.of("error")));

        List<String> expectedWords = List.of("арбуз", "буква", "текст", "яркий");
        List<String> actualWords = loader.loadFile(Path.of("test/resources/words_ru.txt")).getWords();
        assertEquals(expectedWords, actualWords);
    }

    @Test
    public void dictionaryLoaderEmptyFile() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        assertThrows(WordleException.class, () -> loader.loadFile(Path.of("test/resources/empty.txt")));
    }

    @Test
    public void gameWrongInputDoesNotCountAsStep() throws GameException {
        WordleGame game = new WordleGame(dictionary, log, "буква");
        assertThrows(InvalidWordException.class, () -> game.userWordCheck("аб"));
        assertThrows(InvalidWordException.class, () -> game.userWordCheck("table"));
        assertThrows(WordNotFoundException.class, () -> game.userWordCheck("ёжика"));
        assertEquals(WordleGame.MAX_STEPS, game.getSteps());
        assertTrue(game.isRunning());
    }

    @Test
    public void gameCorrectGuessWins() throws GameException {
        WordleGame game = new WordleGame(dictionary, log, "буква");
        assertEquals("+++++", game.userWordCheck("буква"));
        assertTrue(game.isWin());
        assertFalse(game.isRunning());
    }

    @Test
    public void gameOutOfStepsLoses() throws GameException {
        WordleGame game = new WordleGame(dictionary, log, "буква");
        String[] guesses = {"арбуз", "текст", "ягоды", "арбуз", "текст", "ягоды"};
        for (String guess : guesses) {
            game.userWordCheck(guess);
        }
        assertEquals(0, game.getSteps());
        assertFalse(game.isRunning());
        assertFalse(game.isWin());
        assertThrows(GameException.class, () -> game.userWordCheck("буква"));
    }

    @Test
    public void hintFitsAllConstraints() throws GameException {
        WordleGame game = new WordleGame(dictionary, log, "буква");
        assertEquals("--+--", game.userWordCheck("текст"));

        String hint = game.computerHint();
        assertTrue(dictionary.contains(hint));
        assertNotEquals("текст", hint);
        assertFalse(hint.contains("т"));
        assertFalse(hint.contains("е"));
        assertFalse(hint.contains("с"));
        assertEquals('к', hint.charAt(2));
    }

    @Test
    public void computerPlaysWholeGameByHints() throws GameException {
        WordleGame game = new WordleGame(dictionary, log, "ягоды");
        while (game.isRunning()) {
            String hint = game.computerHint();
            game.userWordCheck(hint);
        }
        assertTrue(game.isWin());
    }

    @Test
    public void hintsDoNotRepeat() throws GameException {
        WordleGame game = new WordleGame(dictionary, log, "буква");
        String first = game.computerHint();
        game.userWordCheck(first);
        String second = game.computerHint();
        assertNotEquals(first, second);
    }
}
