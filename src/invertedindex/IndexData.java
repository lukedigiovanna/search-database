package invertedindex;

import articleRetrieval.WikipediaArticle;

public class IndexData {
    // Store a reference to the document
    private WikipediaArticle document;
    
    public IndexData(WikipediaArticle document) {
        this.document = document;
    }

    public WikipediaArticle getArticle() {
        return this.document;
    }

    @Override
    public int hashCode() {
        return this.document.getTitle().hashCode();
    }

    /**
     * Implements the equals method based on the title of the article.
     * This is under the assumption that no two articles have the same title.
     */
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
