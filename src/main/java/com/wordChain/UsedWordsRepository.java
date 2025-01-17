package com.wordChain;

import java.util.HashSet;
import java.util.Set;

public class UsedWordsRepository {
    private final Set<String> usedWords = new HashSet<>();

    public void addWord(String word) {
        usedWords.add(word);
    }

    public boolean isUsed(String word) {
        return usedWords.contains(word);
    }

    public void clear() {
        usedWords.clear();
    }
}
