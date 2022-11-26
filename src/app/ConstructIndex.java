package app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import articleRetrieval.WikipediaArticle;
import articleRetrieval.WikipediaImage;
import invertedindex.InvertedIndex;

public class ConstructIndex {
    
    private static String nextCSV(String row) {
        if (row.charAt(0) == '\"') {
            // then continue parsing this value as a quote initiated value
            int i = 1;
            // keep going until we find a single quotation
            while (true) {
                if (row.charAt(i) == '\"' && (i == row.length() - 1 || row.charAt(i + 1) != '\"')) {
                    // this is the stop condition (when we find a single quote on its own)
                    break;
                }
                i++;
            }
            return row.substring(1, i);
        }
        else {
            // just find either the next comma or the end of the string
            int commaIndex = row.indexOf(',');
            if (commaIndex >= 0) {
                return row.substring(0, commaIndex);
            }
            else {
                return row;
            }
        }
    }

    public static InvertedIndex<WikipediaArticle> constructArticleIndex(String filePath) throws FileNotFoundException, IOException {
        InvertedIndex<WikipediaArticle> index = new InvertedIndex<>();
        
        int max = 12972;
        int i = 0;
        BufferedReader reader = new BufferedReader(new FileReader("articles/sample.csv"));
        String row;
        while ((row = reader.readLine()) != null) {
            try {
                String title = nextCSV(row);
                row = row.substring(3 + title.length());
                String linksStr = nextCSV(row);
                int links = Integer.parseInt(linksStr);
                row = row.substring(linksStr.length() + 2, row.length() - 1);
                String body = row; // remaining of row
                body = body.replace("\"\"", "\"");

                WikipediaArticle article = new WikipediaArticle(title, body, links);
                index.add(article);
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Broken " + i);
                reader.close();
                return null;
            }
            
            if (++i >= max) break;
        }
        reader.close();

        return index;
    }

    public static InvertedIndex<WikipediaImage> constructImageIndex(String filePath) {
        return null;
    }

}
