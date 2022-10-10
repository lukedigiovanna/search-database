package database;

import java.util.*;

/**
 * Contains information about the corpus of documents in a Database.
 */
public class Corpus {
    
    /**
     * Performs stemming on the given string.
     * The string should have no spaces and be a single word.
     * @param str
     * @return
     */
    public static String stem(String str) {
        return str;
    }

    private static Set<String> stopWords;

    private static void initialize() {
        // initialize stop words.
        String[] stopWordsArr = {
            "the", "a", "at", "in"
        };
        stopWords = new HashSet<String>();
        for (String s : stopWordsArr) {
            stopWords.add(s);
        }

        String[] stemSuffixesArr = {
            "ing", "y", "ies", "ify", "ings"
        };
    }

    

    public Corpus() {
        if (stopWords == null) initialize();
    }

}
