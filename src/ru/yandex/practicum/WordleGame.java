package ru.yandex.practicum;

import ru.yandex.practicum.exception.GameException;
import ru.yandex.practicum.exception.HintNotFoundException;
import ru.yandex.practicum.exception.InvalidWordException;
import ru.yandex.practicum.exception.WordNotFoundException;

import java.io.PrintWriter;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    public static final int MAX_STEPS = 6;

    private final PrintWriter log;
    private final WordleDictionary dictionary;
    private final HintProvider hintProvider;

    private String answer;
    private int steps = MAX_STEPS;
    private boolean running = true;
    private boolean win = false;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.log = log;
        if (dictionary == null || dictionary.isEmpty()) {
            throw new IllegalStateException("Словарь пуст или не задан, игра невозможна");
        }
        this.dictionary = dictionary;
        this.hintProvider = new HintProvider(dictionary);
        this.answer = dictionary.randomWord();
    }

    WordleGame(WordleDictionary dictionary, PrintWriter log, String answer) {
        this(dictionary, log);
        if (!dictionary.contains(answer)) {
            throw new IllegalArgumentException("Ответ должен быть словом из словаря");
        }
        this.answer = answer;
    }


    public String userWordCheck(String input) throws GameException {
        if (!running) {
            throw new GameException("Игра уже завершена");
        }
        String word = WordleDictionary.normalize(input);
        if (!WordleDictionary.isValidFormat(word)) {
            throw new InvalidWordException(
                    String.format("Слово должно состоять из %d русских букв", WordleDictionary.WORD_LENGTH));
        }
        if (!dictionary.contains(word)) {
            throw new WordNotFoundException(word);
        }

        steps--;
        String result = dictionary.comparisonWords(answer, word);
        hintProvider.addAttempt(word, result);
        log.printf("Ход: '%s', результат: '%s', осталось шагов: %d%n", word, result, steps);

        if (dictionary.quickComparison(word, answer)) {
            win = true;
            running = false;
        } else if (steps == 0) {
            running = false;
        }
        return result;
    }

    public String computerHint() throws GameException {
        String word;
        try {
            word = hintProvider.help();
        } catch (HintNotFoundException e) {
            throw new GameException(e.getMessage(), e);
        }
        log.printf("Подсказка компьютера: '%s'%n", word);
        return word;
    }

    public String getAnswer() {
        return answer;
    }

    public int getSteps() {
        return steps;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isWin() {
        return win;
    }
}
