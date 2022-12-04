package externalInvertedIndex;

import java.util.List;

import utils.Pair;

public class TestIndex {
    public static void main(String[] args) {
        String database = "database/small.i";

        ExternalInvertedIndex index = new ExternalInvertedIndex();
        System.out.println("[-] Loading index");
        long start = System.nanoTime();
        index.readFromIndexFile(database);
        long elapsed = (System.nanoTime() - start) / 1000;
        System.out.println("[-] Finished loading index in " + elapsed + "us");
        System.out.println("[-] Searching \"german\"");
        start = System.currentTimeMillis();
        List<Pair<Integer, Float>> results = index.search("german");
        elapsed = System.currentTimeMillis() - start;
        System.out.println("[-] Finished searching in " + elapsed + "ms and found " + results.size() + " results");
        // for (Pair<Integer, Float> result : results) {
        //     System.out.println(result);
        // }
    }
}
