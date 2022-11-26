package invertedindex;

import java.util.*;

import articleRetrieval.Document;
import utils.StopWords;
import utils.Tokenizer;

public class InvertedIndex< T extends Document > {
    private Map<String, Set<T>> index;

    public InvertedIndex() {
        this.index = new HashMap<String, Set<T>>();
    }

    private static float calculatePositionalWeight(float x) {
        return 6.0f / (1.0f + (float)Math.exp(4 * x));
        // return x;
    }

    /**
     * Adds a document to this index.
     * @param d
     *          Article to add
     */
    public void add(T d) {
        // tokenize the document body. 
        // take out from the document all of the words and disregard any punctuation
        String[] tokens = d.tokens();
        Map<String, Float> embedding = d.getEmbedding();
        // add this document to each word's hashset.
        for (int i = 0; i < tokens.length; i++) {
            // we only care about non-stop words
            if (StopWords.isStopWord(tokens[i])) {
                continue; // continue if it is a stop word.
            }

            // otherwise, put it in the map
            if (!this.index.containsKey(tokens[i])) {
                this.index.put(tokens[i], new HashSet<T>());
            }
            // update the embedding (will update all existing index data utilizing this embedding)
            if (!embedding.containsKey(tokens[i])) {
                embedding.put(tokens[i], 0.0f);
            }
            // apply positional weighting
            embedding.put(tokens[i], embedding.get(tokens[i]) + calculatePositionalWeight((float) i / tokens.length));
            // add this index data to the inverted index
            this.index.get(tokens[i]).add(d);
        }

        // Apply L2 norm to the embedding
        float mag = 0.0f;
        for (String term : embedding.keySet()) {
            mag += embedding.get(term) * embedding.get(term);
        }
        mag = (float) Math.sqrt(mag);
        for (String term : embedding.keySet()) {
            // uses a scalar factor of 100
            embedding.put(term, embedding.get(term) / mag * 100.0f);
        }
    }

    public List<DocumentWeightPair> search(String term) {
        String[] tokens = Tokenizer.tokenize(term);

        Set<T> results = new HashSet<>();
        Map<String, Float> searchEmbedding = new HashMap<>();
        // calculate the intersection among all of the sets for
        // each corresponding term
        for (String token : tokens) {
            Set<T> found = this.index.get(token);
            if (found != null) {
                if (results.size() == 0) {
                    results.addAll(this.index.get(token));
                }
                else {
                    results.retainAll(this.index.get(token));
                }
            }

            // incorporate tf-idf modification
            searchEmbedding.put(token, 1.0f / (float)Math.log(found.size()));
            // searchEmbedding.put(token, 50f);
        }
        List<DocumentWeightPair> pairResults = new ArrayList<>();
        for (T d : results) {
            pairResults.add(
                new DocumentWeightPair(
                    d, 
                    d.calculateRanking(searchEmbedding)
                )
            );
        }

        Collections.sort(pairResults);
        Collections.reverse(pairResults);

        // for (int i = 0; i < Math.min(20, pairResults.size()); i++) {
        //     System.out.println(pairResults.get(i).article.getTitle() + ", " + pairResults.get(i).weight);
        // }

        return pairResults;
    }
}