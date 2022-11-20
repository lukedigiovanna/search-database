package invertedindex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import articleRetrieval.WikipediaArticle;
import utils.StopWords;

public class Test {
    public static void main(String[] args) throws IOException {
        
        BufferedReader reader = new BufferedReader(new FileReader("articles/sample.txt"));
        InvertedIndex index = new InvertedIndex();

        System.out.println("Constructing index");

        String title;
        while ((title = reader.readLine()) != null) {
            String body = reader.readLine();
            WikipediaArticle article = new WikipediaArticle(title, body);
            index.add(article);
        }

        System.out.println("Finished constructing index");

        reader.close();

        System.out.println("Searching index for 'dog'");
        List<WikipediaArticle> found = index.searchTerm("dog");
        for (WikipediaArticle d : found) {
            System.out.println(d.getTitle());
        }

        StopWords.isStopWord("the");
    }
}
