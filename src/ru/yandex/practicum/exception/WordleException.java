package ru.yandex.practicum.exception;

import java.io.Serial;

public class WordleException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -3372764928851913610L;


    public WordleException(String message) {
        super(message);
    }
}
