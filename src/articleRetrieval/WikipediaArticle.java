package articleRetrieval;

import java.util.*;

import org.json.JSONObject;

import utils.Tokenizer;

/**
 * Stores important information about a particular wikipedia article
 * that will be useful when constructing the search engine.
 */
public class WikipediaArticle implements Document {
    private String body; // full body of the article.
    private String title; // title of the article
    private List<String> linksTo; // list of article titles that this wikipedia article has links to
    private String[] tokens;
    private Map<String, Float> embedding; // embedding is generated when inserted into a database.
    private int inboundLinks;

    public WikipediaArticle(String title, String body, int inboundLinks, List<String> linksTo) {
        this.body = body;
        // tokenize the body
        this.tokens =  Tokenizer.tokenize(title + " " + body);
        this.embedding = new HashMap<>();
        this.title = title;
        this.linksTo = linksTo;
        this.inboundLinks = inboundLinks;
    }

    public WikipediaArticle(String title, String body, int inboundLinks) {
        this(title, body, inboundLinks, null);
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

    public int getInboundLinks() {
        return this.inboundLinks;
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
            float thisVal = this.embedding.containsKey(term) ? 
                this.embedding.get(term) * searchEmbedding.get(term) // incorporate search term weight
                : 0;
            rank += thisVal;
        }
        if (this.inboundLinks > 0) {
            // an article more highly linked to should have higher relevance
            rank *= Math.log10(this.inboundLinks);
        }
        rank *= Math.log10(this.body.length());
        return rank;
    }

    public JSONObject getJSON() {
        JSONObject obj = new JSONObject();
        obj.put("title", this.title);
        obj.put("body", this.body.trim().replaceAll(" +", " "));
        obj.put("inboundLinks", this.inboundLinks);
        return obj;
    }

    @Override
    public int hashCode() {
        return this.getTitle().hashCode();
    }

    /**
     * Implements the equals method based on the title of the article.
     * This is under the assumption that no two articles have the same title.
     */
    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof WikipediaArticle) {
            return ((WikipediaArticle) other).getTitle().equals(this.getTitle());
        }
        else {
            return false;
        }
    }

}
