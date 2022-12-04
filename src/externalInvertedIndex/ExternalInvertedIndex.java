package externalInvertedIndex;

import utils.Pair;
import utils.StopWords;
import utils.Tokenizer;

import java.util.*;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ExternalInvertedIndex {
    private static float calculatePositionalWeight(float x) {
        return 6.0f / (1.0f + (float) Math.exp(4 * x));
    }

    // the offsets in that file are stored where each index of this list is the ID of the doc
    private List<Pair<Long, Integer>> documentOffsets;
    // associates term with a set of document ID's that contain that term
    private Map<String, Map<Integer, Float>> index; // string is the term, integer is the document ID

    public ExternalInvertedIndex() {
        this.documentOffsets = new ArrayList<>();
        this.index = new HashMap<>();
    }

    public void add(ExternalDocument d) {
        int id = documentOffsets.size();
        // tokenize the document body. 
        // take out from the document all of the words and disregard any punctuation
        String[] tokens = d.tokens();
        Map<String, Float> embedding = new HashMap<String, Float>();
        // add this document to each word's hashset.
        for (int i = 0; i < tokens.length; i++) {
            // we only care about non-stop words
            if (StopWords.isStopWord(tokens[i])) {
                continue; // continue if it is a stop word.
            }

            // otherwise, put it in the map
            if (!this.index.containsKey(tokens[i])) {
                this.index.put(tokens[i], new HashMap<Integer, Float>());
            }
            // update the embedding (will update all existing index data utilizing this embedding)
            if (!embedding.containsKey(tokens[i])) {
                embedding.put(tokens[i], 0.0f);
            }
            // apply positional weighting
            embedding.put(tokens[i], embedding.get(tokens[i]) + calculatePositionalWeight((float) i / tokens.length));
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

        // now update the embeddings stored in the map
        for (String term : embedding.keySet()) {
            this.index.get(term).put(id, embedding.get(term));
        }

        // now add the document offset to the list
        this.documentOffsets.add(new Pair<>(d.offset, d.links));
    }

    /**
     * Will search for the given query, collect external data results, sort, and deliver the data.
     * @param queryString
     * @return
     */
    public List<Pair<Integer, Float>> search(String queryString) {
        // tokenize the query
        String[] tokens = Tokenizer.tokenize(queryString);
        Set<Integer> results = new HashSet<>();
        Map<String, Float> searchEmbedding = new HashMap<>();

        for (String token : tokens) {
            Map<Integer, Float> found = this.index.get(token);
            if (found != null) {
                if (results.size() == 0) {
                    results.addAll(found.keySet());
                } else {
                    results.retainAll(found.keySet());
                }
                searchEmbedding.put(token, 1.0f / (float) Math.log(found.size()));
            }
        }

        List<Pair<Integer, Float>> pairResults = new ArrayList<>();
        for (Integer id : results) {
            float score = 0.0f;
            for (String term : searchEmbedding.keySet()) {
                score += searchEmbedding.get(term) * this.index.get(term).get(id);
            }
            pairResults.add(new Pair<>(id, score));
        }

        // sort the results
        Collections.sort(pairResults);
        Collections.reverse(pairResults);

        return pairResults;
    }

    /**
     * Writes a serialized version of this index to a given file path.
     * @param filePath
     */
    public void write(String filePath) throws IOException {
        // binary file constructed as follows:
        // 1. 4 bytes for the number of terms in the index
        // each term is stored as follows
        // 1 byte for the length of the term
        // n bytes for the term
        // 4 bytes for the number of documents that contain this term
        // each document is stored as follows
        // 4 bytes for the document ID
        // 4 byte float for the embedding of the term in the document

        // write the index to a file using buffered output streams
        FileOutputStream file = new FileOutputStream(new File(filePath));
        BufferedOutputStream buffer = new BufferedOutputStream(file);
        DataOutputStream data = new DataOutputStream(buffer);
        data.writeInt(this.index.size());
        data.writeInt(this.documentOffsets.size());
        // write index data
        for (String term : this.index.keySet()) {
            // write the term
            data.writeByte(term.length());
            data.writeBytes(term);
            // write the number of documents that contain this term
            Map<Integer, Float> docs = this.index.get(term);
            data.writeInt(docs.size());
            // write the document ID's and embeddings
            for (int docID : docs.keySet()) {
                data.writeInt(docID);
                data.writeFloat(docs.get(docID));
            }
        }
        // now write document data
        for (Pair<Long, Integer> doc : this.documentOffsets) {
            data.writeLong(doc.first);
            data.writeInt(doc.second);
        }
        data.close();
    }

    /**
     * Reads 
     * @param filePath
     */
    public void readFromIndexFile(String filePath) {
        try {
            FileInputStream inputStream = new FileInputStream(filePath);
            FileChannel channel = inputStream.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

            // read the number of terms in the index
            int numTerms = buffer.getInt();
            // read the number of documents in the index
            int numDocs = buffer.getInt();
            // read the index data
            for (int i = 0; i < numTerms; i++) {
                // read the term
                int termLength = buffer.get() & 0xFF;
                byte[] termBytes = new byte[termLength];
                buffer.get(termBytes);
                String term = new String(termBytes);
                // read the number of documents that contain this term
                int numDocsForTerm = buffer.getInt();
                // read the document ID's and embeddings
                Map<Integer, Float> docs = new HashMap<>();
                for (int j = 0; j < numDocsForTerm; j++) {
                    int docID = buffer.getInt();
                    float embedding = buffer.getFloat();
                    docs.put(docID, embedding);
                }
                this.index.put(term, docs);
            }
            // now read document data
            for (int i = 0; i < numDocs; i++) {
                long offset = buffer.getLong();
                int links = buffer.getInt();
                this.documentOffsets.add(new Pair<>(offset, links));
            }

            channel.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printFrequencyReport() {
        // print out the frequency of each term in the index
        Map<Integer, Integer> frequencyReport = new HashMap<>();
        for (String term : this.index.keySet()) {
            int count = this.index.get(term).size();
            if (!frequencyReport.containsKey(count)) {
                frequencyReport.put(count, 0);
            }
            frequencyReport.put(count, frequencyReport.get(count) + 1);
        }
        // now print out the frequency report
        List<Integer> keys = new ArrayList<>(frequencyReport.keySet());
        Collections.sort(keys);
        System.out.println("Count: Frequency");
        for (int key : keys) {
            System.out.println(key + ": " + frequencyReport.get(key));
        }
    }
}
