package database;

import java.util.*;

/**
 * Stores the common and rare word counts for a given instance.
 * 
 * This may be used for a specific document or for an internal search node.
 */
public class InternalCounts {
    /**
     * 
     */
    private Map<String, Integer> words;
    // private Map<String, Integer> rareWords;

    public InternalCounts(String input) {
        // this.commonWords = new Hashtable<>();
        // this.rareWords = new Hashtable<>();
        this.words = new Hashtable<>();
    }

    private void countWords(String input) {
        // remove all punctuation and make lowercase.
        String[] words = input.replaceAll("[^a-zA-Z ]", "")
                            .toLowerCase()
                            .split("\\s+");
        for (String word : words) {
            // refer to 
            int currentCount = 0;
            if (this.words.containsKey(word)) {
                this.words.put(word, this.words.get(word) + 1);
            }
        }
    }
}
