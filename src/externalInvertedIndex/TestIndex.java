package externalInvertedIndex;

import java.util.List;

import utils.Pair;

public class TestIndex {
    public static void main(String[] args) {

        ExternalInvertedIndex articleIndex = new ExternalInvertedIndex("articles/articles.txt");
        System.out.println("[-] Loading article index");
        long start = System.nanoTime();
        articleIndex.readFromIndexFile("database/articles.i");
        long elapsed = (System.nanoTime() - start) / 1000;
        System.out.println("[-] Finished loading index in " + elapsed + "us");
        articleIndex.validate();

        ExternalInvertedIndex imageIndex = new ExternalInvertedIndex("articles/images.txt");
        System.out.println("[-] Loading article index");
        start = System.nanoTime();
        imageIndex.readFromIndexFile("database/images.i");
        elapsed = (System.nanoTime() - start) / 1000;
        System.out.println("[-] Finished loading index in " + elapsed + "us");

        System.out.println("[-] Searching \"german\"");
        start = System.currentTimeMillis();
        List<Pair<Integer, Float>> results = articleIndex.search("german");
        articleIndex.getArticleData(results, 0, 20);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("[-] Finished searching in " + elapsed + "ms and found " + results.size() + " results");

        System.out.println("[-] Searching \"german\"");
        start = System.currentTimeMillis();
        results = imageIndex.search("united");
        imageIndex.getImageData(results, 0, 100);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("[-] Finished searching in " + elapsed + "ms and found " + results.size() + " results");
    }
}
