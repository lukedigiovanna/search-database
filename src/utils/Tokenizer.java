package utils;

/**
 * Static class for easy functions to convert entire bodies of text
 * into their proper tokenized form.
 */
public class Tokenizer {
    private static Stemmer stemmer = new Stemmer();

    /**
     * Performs the following steps on the given string:
     *  1. Removes all punctuation
     *  2. Converts to lowercase
     *  3. Splits each token based on whitespace
     *  4. Stems each term
     * 
     * @param string String to parse
     * @return
     *      String array of tokenized form. Maintains order of tokens.
     */
    public static String[] tokenize(String string) {
        String[] tokens = string.replaceAll("[^a-zA-Z0-9 ]", "")
                                .toLowerCase()
                                .split("\\s+");
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = stemmer.stem(tokens[i]);
        }
        return tokens;
    }

    public static String tokenizeSingle(String term) {
        return stemmer.stem(term.toLowerCase().replaceAll("[^a-zA-Z ]", ""));
    }
}
