package articleRetrieval;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.json.JSONObject;

import utils.Tokenizer;

/**
 * Stores important information about a particular wikipedia article
 * that will be useful when constructing the search engine.
 */
public class WikipediaArticle {
    private String body; // full body of the article.
    private String title; // title of the article
    private List<String> linksTo; // list of article titles that this wikipedia article has links to
    private String[] tokens;
    private Map<String, Float> embedding; // embedding is generated when inserted into a database.

    public WikipediaArticle(String title, String body, List<String> linksTo) {
        this.body = body;
        // tokenize the body
        this.tokens =  Tokenizer.tokenize(title + " " + body);
        this.embedding = new HashMap<>();
        this.title = title;
        this.linksTo = linksTo;
    }

    public WikipediaArticle(String title, String body) {
        this(title, body, null);
    }

    public String getURL() {
        return "";
    }

    public String body() {
        return this.body;
    }

    public String getTitle() {
        return this.title;
    }

    /**
     * Returns the tokenized and stemmed versions of the content of this article.
     * Returns the tokens in the order they appear in the article. This information may
     * be useful in weighting terms of an article.
     * @return
     *          String array of ordered tokenized/stemmed words.
     */
    public String[] tokens() {
        return this.tokens;
    }

    public List<String> getLinkedArticle() {
        return this.linksTo;
    }

    public Map<String, Float> getEmbedding() {
        return this.embedding;
    }

    /**
     * Calculates a simple dot product between this articles embedding
     * and the given search embedding
     * @param searchEmbedding
     * @return
     *  Dot product between the two embeddings.
     */
    public float calculateRanking(Map<String, Float> searchEmbedding) {
        float rank = 0.0f;
        for (String term : searchEmbedding.keySet()) {
            float thisVal = this.embedding.containsKey(term) ? this.embedding.get(term) : 0;
            rank += thisVal;
        }
        return rank;
    }

    public JSONObject getJSON() {
        JSONObject obj = new JSONObject();
        obj.put("title", this.title);
        obj.put("body", this.body);
        return obj;
    }

    /**
     * Saves this article to a .txt file according to the following format:
     * First line is the article's full name
     * The rest of the file is the direct content of the file.
     * @param filepath
     */
    public void saveData(String directory) {
        try {
            FileWriter file = new FileWriter(directory + "/" + this.title + ".txt");
            file.write(this.title + "\n");
            file.write(this.body);
            file.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
