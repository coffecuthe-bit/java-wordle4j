package ru.yandex.practicum.exception;


public class WordNotFoundException extends GameException {

    public WordNotFoundException(String word) {
        super(String.format("Слова '%s' нет в словаре игры", word));
    }
}
