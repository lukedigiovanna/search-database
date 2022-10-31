package invertedindex;

import java.util.*;

import articleRetrieval.WikipediaArticle;
import utils.StopWords;
import utils.Tokenizer;
import utils.Stemmer;

public class InvertedIndex {
    private Map<String, Set<IndexData>> index;
    private Map<String, Integer> wordIndices;
    private int uniqueWordCount;
    private Stemmer stemmer;

    public InvertedIndex() {
        this.index = new HashMap<String, Set<IndexData>>();
        this.stemmer = new Stemmer();
    }

    /**
     * Adds a document to this index.
     * @param d
     *          Article to add
     */
    public void add(WikipediaArticle d) {
        // tokenize the document body. 
        // take out from the document all of the words and disregard any punctuation
        String[] tokens = d.tokens();
        // add this document to each word's hashset.
        for (int i = 0; i < tokens.length; i++) {
            // we only care about non-stop words
            if (StopWords.isStopWord(tokens[i])) {
                continue; // continue if it is a stop word.
            }

            // otherwise, put it in the map
            if (!this.index.containsKey(tokens[i])) {
                this.index.put(tokens[i], new HashSet<IndexData>());
                // this.wordIndices.put(words[i].)
            }

            this.index.get(tokens[i]).add(new IndexData(d));
        }
    }

    public List<WikipediaArticle> search(String term) {
        term = stemmer.stem(term).toLowerCase().replaceAll("[^a-zA-Z ]", "");

        Set<IndexData> results = this.index.get(term);
        // from the set constructed a sorted list of most relevant results.
        // each document can calculate some relevancy score to the term.
        // sort via this value.
        List<WikipediaArticle> listResults = new ArrayList<>();
        for (IndexData article : results) {
            listResults.add(article.getArticle());
        }
        return listResults;
    }

    public List<WikipediaArticle> search(String terms) {
        // parse the terms
        String[] tokens = Tokenizer.tokenize(terms);
        // collect all things.

    }
}