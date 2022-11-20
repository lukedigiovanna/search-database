package invertedindex;

import articleRetrieval.WikipediaArticle;

public class IndexData {
    // Store a reference to the document
    private WikipediaArticle document;
    // Store the weights of this word for the given document
    // this also conveniently stores the count of the word in the document
    // private float[] weights;
    // private int numWeights;

    public IndexData(WikipediaArticle document) {
        this.document = document;
        // parse the article data for 
        // this.weights = new float[20];
    }

    public WikipediaArticle getArticle() {
        return this.document;
    }

    @Override
    public int hashCode() {
        return this.document.getTitle().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof IndexData) {
            return ((IndexData) other).document.getTitle().equals(this.document.getTitle());
        }
        else {
            return false;
        }
    }

}
