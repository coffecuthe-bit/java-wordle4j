package ru.yandex.practicum;

import ru.yandex.practicum.exception.WordleException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ListIterator;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {


    public WordleDictionaryLoader() {
    }


    public WordleDictionary loadFile(Path path) {
        File f = new File(path.toUri());
        if (!f.exists()) {
            throw new WordleException(String.format("Не удалось найти словарь по пути '%s'", path));
        }
        try {
            List<String> words = Files.readAllLines(path, StandardCharsets.UTF_8);
            normalizeWords(words);
            words = words.stream().sorted().toList();
            return new WordleDictionary(words);

        } catch (IOException e) {
            throw new WordleException("Словарь пустой или не найден");
        }
    }

    private void normalizeWords(List<String> words) {
        ListIterator<String> iterator = words.listIterator();
        while (iterator.hasNext()) {
            String word = iterator.next().toLowerCase();
            if (word.length() != 5) {
                iterator.remove();
                continue;
            }
            boolean ruWord = word.matches("[а-яё]+");
            if (!ruWord) {
                iterator.remove();
                continue;
            }
            if (word.contains("ё")) {
                word = word.replace("ё", "е");
            }
            iterator.set(word);
        }
    }
}
