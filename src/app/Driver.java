package app;

import java.io.IOException;

import articleRetrieval.*;
import invertedindex.InvertedIndex;
import utils.MemoryCheck;

public class Driver {

    public static void main(String[] args) throws IOException {
        // set up article index
        System.out.println("[-] Constructing article index");
        InvertedIndex<WikipediaArticle> articleIndex = ConstructIndex
                .constructArticleIndex("articles/articles-sample.txt");
        System.out.println("[-] Finished article index");

        // set up image index
        System.out.println("[-] Constructing image index");
        InvertedIndex<WikipediaImage> imageIndex = ConstructIndex
                .constructImageIndex("articles/images-sample.txt");
        System.out.println("[-] Finished image index");

        // check on memory usage
        MemoryCheck.print();

        Server server = new Server(9000, articleIndex, imageIndex);
        server.start();
    }

}
