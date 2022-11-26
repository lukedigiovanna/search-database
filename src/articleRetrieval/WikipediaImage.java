package articleRetrieval;

import utils.Tokenizer;

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

    public WikipediaImage(String url, String body) {
        this.url = url;
        this.body = body;
        this.tokens = Tokenizer.tokenize(body);
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
        return null;
    }

    @Override
    public float calculateRanking(Map<String, Float> searchEmbedding) {
        return 0;
    }

    @Override
    public Map<String, Float> getEmbedding() {
        return null;
    }

}
