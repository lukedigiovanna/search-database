package articleRetrieval;

import utils.Tokenizer;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * For adding support to an image search service that utilizes the 
 * caption
 */
public class WikipediaImage implements Document {
    private String url;
    private String body;
    private String[] tokens;
    private Map<String, Float> embedding; // embedding is generated when inserted into a database.
    private int inboundLinks;
    private String articleTitle;

    public WikipediaImage(String url, String body, int inboundLinks, String articleTitle) {
        this.url = url;
        this.body = body;
        this.tokens = Tokenizer.tokenize(body);
        this.embedding = new HashMap<>();
        this.inboundLinks = inboundLinks;
        this.articleTitle = articleTitle;
    }

    @Override
    public String body() {
        return this.body;
    }

    @Override
    public String[] tokens() {
        return this.tokens;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject json = new JSONObject();
        json.put("url", "https://upload.wikimedia.org/wikipedia/commons/thumb/" + this.url);
        json.put("content", this.body);
        json.put("articleTitle", this.articleTitle);
        return json;
    }

    @Override
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

    @Override
    public Map<String, Float> getEmbedding() {
        return this.embedding;
    }

    public String getURL() {
        return this.url;
    }

}
