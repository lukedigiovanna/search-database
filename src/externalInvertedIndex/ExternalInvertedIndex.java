package externalInvertedIndex;

import utils.Pair;
import utils.StopWords;
import utils.StringUtils;
import utils.Tokenizer;

import java.util.*;

import articleRetrieval.WikipediaArticle;
import articleRetrieval.WikipediaImage;
import invertedindex.DocumentWeightPair;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ExternalInvertedIndex {
    private static float calculatePositionalWeight(float x) {
        return 6.0f / (1.0f + (float) Math.exp(4 * x));
    }

    // the offsets in that file are stored where each index of this list is the ID of the doc
    private List<DocumentData> documentOffsets;
    // associates term with a set of document ID's that contain that term
    private Map<String, Map<Integer, Float>> index; // string is the term, integer is the document ID

    private String dataFile;

    public ExternalInvertedIndex(String dataFile) {
        this.documentOffsets = new ArrayList<>();
        this.index = new HashMap<>();
        this.dataFile = dataFile;
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
        this.documentOffsets.add(new DocumentData(d.offset, d.links, (short)d.body.length()));
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
            if (results.size() == 0) {
                results.addAll(found.keySet());
            } else {
                results.retainAll(found.keySet());
            }
            // implements tf idf
            searchEmbedding.put(token, 1.0f / (float) Math.log(found.size()));
        }

        List<Pair<Integer, Float>> pairResults = new ArrayList<>();
        for (Integer id : results) {
            float score = 0.0f;
            for (String term : searchEmbedding.keySet()) {
                score += searchEmbedding.get(term) * this.index.get(term).get(id);
            }
            score *= Math.log10(this.documentOffsets.get(id).links);
            score *= Math.log10(this.documentOffsets.get(id).length);
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

        int count = 0;
        for (String term : this.index.keySet()) {
            if (this.index.get(term).size() > 1) {
                count++;
            }
        }

        data.writeInt(count);
        data.writeInt(this.documentOffsets.size());
        // write index data
        for (String term : this.index.keySet()) {
            if (this.index.get(term).size() < 2) {
                continue;
            }
            // write the term
            if (term.length() > 255) {
                System.out.println("How did that happen");
            }
            data.writeByte(term.length());
            data.writeBytes(term);
            // write the number of documents that contain this term
            Map<Integer, Float> docs = this.index.get(term);
            data.writeInt(docs.size());
            // System.out.println(term + " " + docs.size());
            // write the document ID's and embeddings
            for (int docID : docs.keySet()) {
                data.writeInt(docID);
                data.writeFloat(docs.get(docID));
            }
        }
        // now write document data
        for (DocumentData doc : this.documentOffsets) {
            data.writeLong(doc.offset);
            data.writeInt(doc.links);
            data.writeShort(doc.length);
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
                // System.out.println(term + " " + numDocsForTerm);
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
                short length = buffer.getShort();
                this.documentOffsets.add(new DocumentData(offset, links, length));
            }

            channel.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<DocumentWeightPair> getArticleData(List<Pair<Integer, Float>> results, int offset, int count) {
        List<DocumentWeightPair> data = new ArrayList<>();
        try {
            RandomAccessFile file = new RandomAccessFile(this.dataFile, "r");
            int end = Math.min(offset + count, results.size());
            for (int i = offset; i < end; i++) {
                Pair<Integer, Float> result = results.get(i);
                DocumentData doc = this.documentOffsets.get(result.first);
                long byteOffset = doc.offset;
                int links = doc.links;
                float weight = result.second;
                
                file.seek(byteOffset);
                String title = file.readLine();
                file.readLine();
                String text = file.readLine();
                WikipediaArticle article = new WikipediaArticle(title, text, links);
                data.add(new DocumentWeightPair(article, weight));
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public List<DocumentWeightPair> getImageData(List<Pair<Integer, Float>> results, int offset, int count) {
        List<DocumentWeightPair> data = new ArrayList<>();
        try {
            RandomAccessFile file = new RandomAccessFile(this.dataFile, "r");
            int end = Math.min(offset + count, results.size());
            for (int i = offset; i < end; i++) {
                Pair<Integer, Float> result = results.get(i);
                DocumentData doc = this.documentOffsets.get(result.first);
                long byteOffset = doc.offset;
                int links = doc.links;
                float weight = result.second;

                file.seek(byteOffset);
                String url = file.readLine();
                String text = file.readLine();
                WikipediaImage image = new WikipediaImage(url, text, links, "");
                data.add(new DocumentWeightPair(image, weight));
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Validates that the document offsets stored in this index correctly
     * correspond to the data in the data file.
     * @return  True if it is valid data, false otherwise
     */
    public boolean validate() {
        System.out.println("[-] Validating index matches " + this.dataFile);
        try {
            RandomAccessFile file = new RandomAccessFile(this.dataFile, "r");
            BufferedReader reader = new BufferedReader(new FileReader(this.dataFile));
            for (int i = 0; i < this.documentOffsets.size(); i++) {
                // fetch article data
                DocumentData doc = this.documentOffsets.get(i);
                long byteOffset = doc.offset;
                int links = doc.links;
                
                file.seek(byteOffset);
                String readTitle = file.readLine();
                String readLinks = file.readLine();
                String readText = file.readLine();

                String realTitle = reader.readLine();
                String realLinks = reader.readLine();
                String realText = reader.readLine();

                if (!readLinks.equals(realLinks)) {
                    // then we have a problem here!
                    System.out.println("[-] VALIDATION ERROR DISCOVERED");
                    System.out.println("\tOccurs on article #" + (i + 1));
                    System.out.println("\tOccurs at byte offset " + byteOffset + " (" + String.format("0x%x", byteOffset) + ")");
                    System.out.println("\tReal title: " + realTitle);
                    System.out.println("\tProblem title: " + readTitle);
                    System.out.println("\tReal links: " + realLinks);
                    System.out.println("\tProblem links: " + readLinks);
                    file.close();
                    reader.close();
                    return false; // invalid :(
                }
            }
            reader.close();
            file.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
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
