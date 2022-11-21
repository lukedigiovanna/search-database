package invertedindex;

import articleRetrieval.WikipediaArticle;

public class ArticleWeightPair implements Comparable<ArticleWeightPair> {
    protected WikipediaArticle article;
    protected Float weight;

    public ArticleWeightPair(WikipediaArticle article, Float weight) {
        this.article = article;
        this.weight = weight;
    }

    @Override
    public int compareTo(ArticleWeightPair o) {
        return this.weight.compareTo(o.weight);
    }
}
