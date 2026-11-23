package ru.yandex.practicum;

import ru.yandex.practicum.exception.WordleException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary loadFile(Path path) {
        if (!Files.exists(path)) {
            throw new WordleException(String.format("Не удалось найти словарь по пути '%s'", path));
        }
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = WordleDictionary.normalize(line.trim());
                if (WordleDictionary.isValidFormat(word)) {
                    result.add(word);
                }
            }
        } catch (IOException e) {
            throw new WordleException(String.format("Ошибка чтения словаря '%s': %s", path, e.getMessage()), e);
        }
        if (result.isEmpty()) {
            throw new WordleException(String.format("Словарь '%s' не содержит подходящих слов", path));
        }
        result.sort(null);
        log.printf("Словарь '%s' загружен, подходящих слов: %d%n", path, result.size());
        return new WordleDictionary(result);
    }
}
