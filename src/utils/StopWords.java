package utils;

import java.io.*;
import java.util.HashSet;

/**
 * Utility for checking if words 
 */
public class StopWords {
    private static HashSet<String> stopWords;

    private static void initializeStopWords() {
        stopWords = new HashSet<String>();
        BufferedReader reader;
        // open 
        try {
            reader = new BufferedReader(new FileReader("stopwords.txt"));
            String word;
            while ((word = reader.readLine()) != null) {
                System.out.println(word);
                stopWords.add(word);
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines if the given word is a stop word.
     * @param word
     * @return
     */
    public static boolean isStopWord(String word) {
        if (stopWords == null) {
            initializeStopWords();
        }
        return stopWords.contains(word);
    }
}
