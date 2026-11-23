package ru.yandex.practicum;

import ru.yandex.practicum.exception.HintNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class HintProvider {

    private final List<String> fullDictionary;
    private final List<Attempts> attempts = new ArrayList<>();
    private final Random random = new Random();

    public HintProvider(WordleDictionary dictionary) {
        this.fullDictionary = new ArrayList<>(dictionary.getWords());
    }

    public void addAttempt(String actual, String result) {
        attempts.add(new Attempts(actual, result));
    }

    public List<Attempts> getAttempts() {
        return attempts;
    }


    public String help() throws HintNotFoundException {
        List<String> candidates = filterCandidates();
        candidates.removeAll(triedWords());
        if (candidates.isEmpty()) {
            throw new HintNotFoundException("Не удалось подобрать подсказку");
        }
        return candidates.get(random.nextInt(candidates.size()));
    }


    public List<String> filterCandidates() {
        Map<Integer, Character> fixedPositions = new HashMap<>();
        Map<Character, Integer> presentCounts = new HashMap<>();
        Map<Character, Integer> absentCounts = new HashMap<>();
        Map<Integer, Set<Character>> blockedPositions = new HashMap<>();
        Set<Character> absentLetters = new HashSet<>();

        for (Attempts attempt : attempts) {
            Map<Character, Integer> localPresent = new HashMap<>();
            for (int i = 0; i < attempt.actual().length(); i++) {
                char c = attempt.actual().charAt(i);
                char mark = attempt.result().charAt(i);
                switch (mark) {
                    case WordleDictionary.CORRECT_PLACE_LETTER -> {
                        fixedPositions.put(i, c);
                        localPresent.merge(c, 1, Integer::sum);
                    }
                    case WordleDictionary.WRONG_PLACE_LETTER -> {
                        blockedPositions.computeIfAbsent(i, k -> new HashSet<>()).add(c);
                        localPresent.merge(c, 1, Integer::sum);
                    }
                    default -> absentLetters.add(c);
                }
            }
            for (Map.Entry<Character, Integer> e : localPresent.entrySet()) {
                presentCounts.merge(e.getKey(), e.getValue(), Math::max);
            }
        }
        for (Character c : absentLetters) {
            absentCounts.put(c, presentCounts.getOrDefault(c, 0));
        }

        List<String> result = new ArrayList<>();
        for (String word : fullDictionary) {
            if (matches(word, fixedPositions, presentCounts, absentCounts, blockedPositions)) {
                result.add(word);
            }
        }
        return result;
    }

    private boolean matches(String word,
                            Map<Integer, Character> fixedPositions,
                            Map<Character, Integer> presentCounts,
                            Map<Character, Integer> absentCounts,
                            Map<Integer, Set<Character>> blockedPositions) {
        for (Map.Entry<Integer, Character> e : fixedPositions.entrySet()) {
            if (word.charAt(e.getKey()) != e.getValue()) {
                return false;
            }
        }
        for (Map.Entry<Character, Integer> e : presentCounts.entrySet()) {
            if (countChar(word, e.getKey()) < e.getValue()) {
                return false;
            }
        }
        for (Map.Entry<Character, Integer> e : absentCounts.entrySet()) {
            if (countChar(word, e.getKey()) > e.getValue()) {
                return false;
            }
        }
        for (Map.Entry<Integer, Set<Character>> e : blockedPositions.entrySet()) {
            if (e.getValue().contains(word.charAt(e.getKey()))) {
                return false;
            }
        }
        return true;
    }

    private int countChar(String word, char c) {
        int count = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    private Set<String> triedWords() {
        Set<String> tried = new HashSet<>();
        for (Attempts attempt : attempts) {
            tried.add(attempt.actual());
        }
        return tried;
    }
}
