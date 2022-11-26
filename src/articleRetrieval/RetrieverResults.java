package articleRetrieval;

import java.util.List;

public class RetrieverResults {
    public WikipediaArticle article;
    public List<WikipediaImage> images;

    public RetrieverResults(WikipediaArticle article, List<WikipediaImage> images) {
        this.article = article;
        this.images = images;
    }
}
