package externalInvertedIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;

public class GenerateIndexFile {
    public static void generateArticleIndex(String inputData, String destination)
            throws FileNotFoundException, IOException {
        // this function will read in raw article data from the given filepath
        // it will construct the external inverted index file
        File file = new File(inputData);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ExternalInvertedIndex index = new ExternalInvertedIndex(inputData);
        String row;
        long bytesRead = 0;

        int max = 500000, i = 0;
        while ((row = reader.readLine()) != null) {
            try {
                if (row.length() == 0) {
                    break;
                }

                String title = row;
                String linksStr = reader.readLine();
                int links = Integer.parseInt(linksStr);
                String body = reader.readLine();

                ExternalDocument article = new ExternalDocument(title, body, links, bytesRead);

                bytesRead += (title.length() + linksStr.length() + body.length() + 3);

                index.add(article);
            } catch (Exception e) {
                e.printStackTrace();
                reader.close();
            }
            if (++i >= max)
                break;
        }
        // once the external index has been constructed, we must write it to disk
        index.write(destination);
    }

    public static void generateImageIndex(String inputData, String destination)
            throws FileNotFoundException, IOException {
        // this function will read in raw article data from the given filepath
        // it will construct the external inverted index file
        File file = new File(inputData);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ExternalInvertedIndex index = new ExternalInvertedIndex(inputData);
        String row;
        long bytesRead = 0;

        int max = 500000, j = 0;
        while ((row = reader.readLine()) != null) {
            try {
                if (row.length() == 0) {
                    break;
                }

                String title = row;
                String linksStr = reader.readLine();
                int links = Integer.parseInt(linksStr);
                String countStr = reader.readLine();
                int count = Integer.parseInt(countStr);
                bytesRead += title.length() + linksStr.length() + countStr.length() + 3;

                for (int i = 0; i < count; i++) {
                    String url = reader.readLine();
                    String body = reader.readLine();
                    ExternalDocument article = new ExternalDocument(url, body, links, bytesRead);
                    bytesRead += url.length() + body.length() + 2;
                    index.add(article);
                }

            } catch (Exception e) {
                e.printStackTrace();
                reader.close();
            }
            if (++j >= max)
                break;
        }
        // once the external index has been constructed, we must write it to disk
        index.write(destination);
    }

    public static void main(String[] args) {
        try {
            System.out.println("[-] Generating article index file");
            long start = System.nanoTime();
            generateArticleIndex("articles/articles.txt", "database/articles.i");
            long elapsed = (System.nanoTime() - start) / 1000;
            System.out.println("[-] Finished generating index file in " + elapsed + "us");

            System.out.println("[-] Generating image index file");
            start = System.nanoTime();
            generateImageIndex("articles/images.txt", "database/images.i");
            elapsed = (System.nanoTime() - start) / 1000;
            System.out.println("[-] Finished generating index file in " + elapsed + "us");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
