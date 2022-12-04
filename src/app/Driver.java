package app;

import java.io.IOException;

import externalInvertedIndex.ExternalInvertedIndex;
import utils.MemoryCheck;

public class Driver {

    public static void main(String[] args) throws IOException {
        // set up article index
        System.out.println("[-] Constructing article index");
        ExternalInvertedIndex articleIndex = new ExternalInvertedIndex("articles/articles.txt");
        articleIndex.readFromIndexFile("database/articles.i");
        System.out.println("[-] Finished article index");

        // set up image index
        System.out.println("[-] Constructing image index");
        ExternalInvertedIndex imageIndex = new ExternalInvertedIndex("articles/images.txt");
        imageIndex.readFromIndexFile("database/images.i");
        System.out.println("[-] Finished image index");

        // check on memory usage
        MemoryCheck.print();

        Server server = new Server(9000, articleIndex, imageIndex);
        server.start();
    }

}
