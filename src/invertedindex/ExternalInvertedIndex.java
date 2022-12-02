package invertedindex;

import articleRetrieval.Document;
import utils.StopWords;

import java.util.*;
import java.util.LinkedHashMap;
import java.io.RandomAccessFile;

public class ExternalInvertedIndex<T extends Document> {
    private static float calculatePositionalWeight(float x) {
        return 6.0f / (1.0f + (float) Math.exp(4 * x));
    }

    // the actual content of what is stored in this index is stored in an external file.
    private RandomAccessFile dataFile;
    // the offsets in that file are stored where each index of this list is the ID of the doc
    private List<Long> documentOffsets;
    // associates term with a set of document ID's that contain that term
    private Map<String, Set<Integer>> index; // string is the term, integer is the document ID

    public ExternalInvertedIndex() {
        this.documentOffsets = new ArrayList<>();
        this.index = new HashMap<>();
    }

    public void add(T d) {
        int id = documentOffsets.size();
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
                this.index.put(tokens[i], new HashSet<Integer>());
            }
            // update the embedding (will update all existing index data utilizing this embedding)
            if (!embedding.containsKey(tokens[i])) {
                embedding.put(tokens[i], 0.0f);
            }
            // apply positional weighting
            embedding.put(tokens[i], embedding.get(tokens[i]) + calculatePositionalWeight((float) i / tokens.length));
            // add this index data to the inverted index
            this.index.get(tokens[i]).add(id);
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

    /**
     * Will search for the given query, collect external data results, sort, and deliver the data.
     * @param queryString
     * @return
     */
    public List<DocumentWeightPair> search(String queryString) {
        return null;
    }

    /**
     * Writes a serialized version of this index to a given file path.
     * @param filePath
     */
    public void writeIndexFile(String filePath) {
        // binary file constructed as follows:

    }

    /**
     * Reads 
     * @param filePath
     */
    public void readFromIndexFile(String filePath) {

    }
}
