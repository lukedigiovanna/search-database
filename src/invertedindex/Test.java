package invertedindex;

import java.util.List;
import articleRetrieval.WikipediaArticle;
import utils.StopWords;

public class Test {
    public static void main(String[] args) {
        WikipediaArticle d1 = new WikipediaArticle("Sentence 1", "The lazy dog jumped over the river. It turns out he isn't that lazy.");
        WikipediaArticle d2 = new WikipediaArticle("Sentence 2", "How about that for a turn of events! In the second inning, baseball has never been more exciting!");
        WikipediaArticle d3 = new WikipediaArticle("Sentence 3", "What is the difference between a horse and a donkey? I don't really know, but I do like dogs");

        InvertedIndex index = new InvertedIndex();
        index.add(d1);
        index.add(d2);
        index.add(d3);

        List<WikipediaArticle> found = index.searchTerm("dog");
        for (WikipediaArticle d : found) {
            System.out.println(d.getTitle() + ": " + d.body());
        }

        StopWords.isStopWord("the");
    }
}
