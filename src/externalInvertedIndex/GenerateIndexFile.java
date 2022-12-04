package externalInvertedIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;

public class GenerateIndexFile {
    public static void generate(String inputData, String destination) throws FileNotFoundException, IOException {
        // this function will read in raw article data from the given filepath
        // it will construct the external inverted index file
        File file = new File(inputData);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ExternalInvertedIndex index = new ExternalInvertedIndex();
        String row;
        long bytesRead = 0;

        int max = 50000, i = 0;
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

                bytesRead += title.length() + linksStr.length() + body.length() + 3;

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

    public static void main(String[] args) {
        try {
            System.out.println("[-] Generating index file");
            long start = System.nanoTime();
            generate("articles/articles-sample.txt", "database/small.i");
            long elapsed = (System.nanoTime() - start) / 1000;
            System.out.println("[-] Finished generating index file in " + elapsed + "us");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
