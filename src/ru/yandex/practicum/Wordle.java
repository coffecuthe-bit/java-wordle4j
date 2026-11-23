package ru.yandex.practicum;

import ru.yandex.practicum.exception.GameException;
import ru.yandex.practicum.exception.WordleException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    public static void main(String[] args) {
        try {
            startGame();
        } catch (WordleException e) {
            System.err.println("Ошибка программы: " + e.getMessage());
        } catch (Exception e) {
            try (PrintWriter log = new PrintWriter(Files.newBufferedWriter(Path.of("wordle.log")))) {
                e.printStackTrace(log);
            } catch (IOException ignored) {
            }
            System.err.println("Программа завершена с ошибкой. Подробности в wordle.log");
        }
    }

    public static void startGame() throws IOException {
        try (PrintWriter log = new PrintWriter(Files.newBufferedWriter(Path.of("wordle.log")))) {
            log.printf("=== Старт игры %s ===%n", LocalDateTime.now());

            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            WordleDictionary dictionary = loader.loadFile(Path.of("words_ru.txt"));
            log.flush();

            WordleGame game = new WordleGame(dictionary, log);
            System.out.println("Компьютер загадал слово из 5 букв. У вас 6 попыток.");
            System.out.println("Введите слово или пустую строку для подсказки.");

            try (Scanner input = new Scanner(System.in)) {
                while (game.isRunning()) {
                    String request = input.nextLine();
                    try {
                        String word = request.isBlank() ? game.computerHint() : request.trim();
                        String result = game.userWordCheck(word);
                        System.out.println(word);
                        System.out.println(result);
                    } catch (GameException e) {
                        System.out.println(e.getMessage());
                        log.printf("Игровая ошибка: %s (ввод '%s')%n", e.getMessage(), request);
                    }
                    log.printf("Состояние: steps=%d, running=%b%n", game.getSteps(), game.isRunning());
                    log.flush();
                }
            }

            printResult(game);
            log.printf("Игра завершена. Загаданное слово: '%s', победа: %b%n", game.getAnswer(), game.isWin());
            log.flush();
        }
    }

    private static void printResult(WordleGame game) {
        StringBuilder message = new StringBuilder();
        if (game.isWin()) {
            message.append("Поздравляем, вы угадали слово!");
        } else {
            message.append("Попытки закончились, вы не угадали.");
        }
        message.append(System.lineSeparator())
                .append("# загаданное слово: ")
                .append(game.getAnswer());
        System.out.println(message);
    }
}
