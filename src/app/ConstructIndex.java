package app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import articleRetrieval.WikipediaArticle;
import articleRetrieval.WikipediaImage;
import invertedindex.InvertedIndex;

public class ConstructIndex {

    public static InvertedIndex<WikipediaArticle> constructArticleIndex(String filePath)
            throws FileNotFoundException, IOException {
        InvertedIndex<WikipediaArticle> index = new InvertedIndex<>();

        int max = 1000000;
        int i = 0;
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String row;
        while ((row = reader.readLine()) != null) {
            try {
                if (row.length() == 0) {
                    break;
                }

                String title = row;
                String linksStr = reader.readLine();
                int links = Integer.parseInt(linksStr);
                String body = reader.readLine(); // remaining of row

                WikipediaArticle article = new WikipediaArticle(title, body, links);
                index.add(article);
            } catch (Exception e) {
                e.printStackTrace();
                reader.close();
                return null;
            }

            if (++i >= max)
                break;
        }
        reader.close();

        System.out.println(i);

        index.print();

        return index;
    }

    public static InvertedIndex<WikipediaImage> constructImageIndex(String filePath)
            throws FileNotFoundException, IOException {
        InvertedIndex<WikipediaImage> index = new InvertedIndex<>();

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String row;
        while ((row = reader.readLine()) != null) {
            try {
                if (row.length() == 0) {
                    break;
                }
                String articleTitle = row;
                int linkCount = Integer.parseInt(reader.readLine());
                int imgCount = Integer.parseInt(reader.readLine());
                System.out.println(imgCount);
                for (int i = 0; i < imgCount; i++) {
                    String url = reader.readLine();
                    String caption = reader.readLine();
                    // if (caption == null) {
                    //     caption = "";
                    // }
                    WikipediaImage image = new WikipediaImage(url, caption, linkCount, articleTitle);
                    index.add(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
                reader.close();
                return null;
            }
        }
        reader.close();

        index.print();

        return index;
    }

}
