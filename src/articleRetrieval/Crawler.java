package articleRetrieval;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;

public class Crawler {
    public static void main(String[] args) throws IOException {
        Queue<String> articles = new ArrayDeque<>(500);
        articles.offer("Pet door");

        File file = new File("articles/all.txt");
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.US_ASCII);

        String[] stopIntros = {
            "Redirect to:",
            "This article needs additional citations for verification.",
            "This article does not cite any sources."
        };

        HashSet<String> seen = new HashSet<>();

        int writtenCount = 0;

        while (writtenCount < 250000) {
            // take the next article
            String title = articles.poll();
            if (seen.contains(title)) {
                System.out.println("[DUPLICATE] " + title);
                continue;
            }
            seen.add(title);
            WikipediaArticle article = Retriever.getArticle(title);
            if (article == null) {
                System.out.println("[NOT FOUND] " + title);
                continue;
            }
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
            writer.write("\"" + article.getTitle() + "\",");
            writer.write("" + article.getInboundLinks() + ",");
            // only allow the first 4000 characters to be written
            if (body.length() > 4000) {
                // chop it
                int lastIndex = 4000;
                while (body.charAt(lastIndex) != ' ') lastIndex--;
                body = body.substring(0, lastIndex);
            }
            body = body.replace("\n", "");
            body = body.replace("\"", "\"\"");
            writer.write("\"" + body + "\"\n");

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

        writer.close();

    }
}
