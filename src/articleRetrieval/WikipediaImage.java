package articleRetrieval;

import utils.Tokenizer;
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

    public String body() {
        return this.body;
    }

    public String[] tokens() {
        return this.tokens;
    }

    public JSONObject getJSON() {
        return null;
    }

}
