package externalInvertedIndex;

import utils.Tokenizer;

public class ExternalDocument {
    public int links;
    public String title;
    public String body;
    public long offset;

    private String[] tokens;

    public ExternalDocument(String title, String body, int links, long offset) {
        this.title = title;
        this.body = body;
        this.links = links;
        this.offset = offset;
        this.tokens = Tokenizer.tokenize(title + " " + body);
    }

    public String[] tokens() {
        return this.tokens;
    }
}
