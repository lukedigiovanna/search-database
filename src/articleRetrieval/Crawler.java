package articleRetrieval;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

public class Crawler {
    private static String[] stopIntros = {
        "Redirect to:",
        "This article needs additional citations for verification.",
        "This article does not cite any sources."
    };

    public static void main(String[] args) throws IOException {
        Queue<String> articles = new ArrayDeque<>(500);
        articles.offer("Pet door");

        boolean append = true;

        File articleFile = new File("articles/articles.txt");
        File imageFile = new File("articles/images.txt");


        
        Writer imageWriter = new OutputStreamWriter(new FileOutputStream(imageFile, append), StandardCharsets.US_ASCII);
        Writer articleWriter = new OutputStreamWriter(new FileOutputStream(articleFile, append));

        

        HashSet<String> seenArticles = new HashSet<>();
        HashSet<String> seenImages = new HashSet<>();

        int writtenCount = 0;

        while (writtenCount < 250000) {
            // take the next article
            String title = articles.poll();
            if (seenArticles.contains(title)) {
                System.out.println("[DUPLICATE] " + title);
                continue;
            }
            seenArticles.add(title);
            RetrieverResults results = Retriever.getArticle(title);
            if (results == null) {
                System.out.println("[NOT FOUND] " + title);
                continue;
            }
            WikipediaArticle article = results.article;
            // determine if we should reject this article based on its body
            String body = article.body();
            boolean reject = false;
            for (String stopIntro : stopIntros) {
                if (body.startsWith(stopIntro)) {
                    reject = true;
                    break;
                }
            }
            if (reject) {
                System.out.println("[REJECTED] " + title);
                continue;
            }
            // otherwise, write the article
            System.out.println(article.getTitle());

            articleWriter.write(article.getTitle() + "\n");
            articleWriter.write(article.getInboundLinks() + "\n");
            // only allow the first 4000 characters to be written
            if (body.length() > 4000) {
                // chop it
                int lastIndex = 4000;
                while (body.charAt(lastIndex) != ' ') lastIndex--;
                body = body.substring(0, lastIndex);
            }
            
            articleWriter.write(body + "\n");

            List<WikipediaImage> images = results.images;
            for (WikipediaImage image : images) {
                if (!seenImages.contains(image.getURL())) {
                    seenImages.add(image.getURL());
                    // write the image
                    imageWriter.write(image.getURL() + "\n");
                    imageWriter.write(article.getTitle() + "\n");
                    imageWriter.write(image.body() + "\n");
                    imageWriter.write(article.getInboundLinks() + "\n");
                }
            }

            writtenCount++;

            // offer a few articles if the queue is running low
            if (articles.size() < 450) {
                for (int i = 0; i < Math.min(8, article.getLinkedArticle().size()); i++) {
                    String candidate = article.getLinkedArticle().get(
                        (int)(Math.random() * article.getLinkedArticle().size()));
                    if (!candidate.contains(":")) {
                        articles.offer(candidate);
                    }
                }
            }
        }

        articleWriter.close();
        imageWriter.close();

    }
}
