package invertedindex;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import articleRetrieval.WikipediaArticle;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.*;

public class Test {
    private static Map<String, String> getQueryParams(String query) {
        Map<String, String> params = new LinkedHashMap<>();

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int equalIndex = pair.indexOf("=");
            params.put(pair.substring(0, equalIndex), pair.substring(equalIndex + 1));
        }

        return params;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("articles/sample.txt"));
        InvertedIndex index = new InvertedIndex();

        System.out.println("Constructing index");

        int max = 5000;
        int i = 0;
        String title;
        while ((title = reader.readLine()) != null) {
            String body = reader.readLine();
            WikipediaArticle article = new WikipediaArticle(title, body);
            index.add(article);
            i++;
            if (i >= max)
                break;
        }

        System.out.println("Finished constructing index");
        reader.close();


        // launch a server
        HttpServer server = HttpServer.create(new InetSocketAddress(9000), 0);
        server.createContext("/api/search", new HttpHandler() {
            @Override
            public void handle(HttpExchange he) throws IOException {
                System.out.println("Recieved search request");
                Map<String, String> params = getQueryParams(he.getRequestURI().getQuery());
                String term = params.get("term");
                List<ArticleWeightPair> found = index.search(term);
                JSONObject response = new JSONObject();
                JSONArray results = new JSONArray();
                for (ArticleWeightPair pair : found) {
                    JSONObject articleJSON = pair.article.getJSON();
                    articleJSON.put("score", pair.weight);
                    results.put(articleJSON);
                }
                response.put("results", results);
                System.out.println(results);
                String res = response.toString();
                he.sendResponseHeaders(200, res.length());
                OutputStream os = he.getResponseBody();
                os.write(res.getBytes());
                os.close();
            }
        });
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange he) throws IOException {
                String indexHtml = "";
                BufferedReader inp = new BufferedReader(new FileReader("public_html/index.html"));
                String line;
                while ((line = inp.readLine()) != null) {
                    indexHtml += line;
                }
                inp.close();
                String res = indexHtml;
                he.sendResponseHeaders(200, res.length());
                OutputStream os = he.getResponseBody();
                os.write(res.getBytes());
                os.close();
            }
        });
        server.setExecutor(null);
        server.start();
        System.out.println("Started server on port 9000");

        index.search("united states");
    }
}
