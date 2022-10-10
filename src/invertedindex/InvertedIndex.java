package invertedindex;

import java.util.*;

public class InvertedIndex {
    private Map<String, Set<Document>> index;

    public InvertedIndex() {
        this.index = new HashMap<String, Set<Document>>();
    }

    /**
     * Adds a document to this index.
     * @param d
     *          Document to add
     */
    public void add(Document d) {
        // tokenize the document body. 
        // take out from the document all of the words and disregard any punctuation
        String[] words = d.body().replaceAll("[^a-zA-Z ]", "")
                            .toLowerCase()
                            .split("\\s+");
        // add this document to each word's hashset.
        for (int i = 0; i < words.length; i++) {
            if (!this.index.containsKey(words[i])) {
                this.index.put(words[i], new HashSet<Document>());
            }
            this.index.get(words[i]).add(d);
        }
    }

    public Set<Document> search(String term) {
        return this.index.get(term);
    }
}