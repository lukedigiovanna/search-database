package articleRetrieval;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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

    public WikipediaArticle(String title, String body, List<String> linksTo) {
        this.body = body;
        // tokenize the body
        this.tokens =  Tokenizer.tokenize(body);
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
