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

    public static void main(String[] args) throws IOException {
        InvertedIndex index = new InvertedIndex();
        
        System.out.println("Constructing index");
        
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
                System.out.println("fuck " + i);
                reader.close();
                return;
            }
            
            if (++i >= max) break;
        }
        reader.close();

        System.out.println("Finished constructing index");
        reader.close();


        // launch a server
        HttpServer server = HttpServer.create(new InetSocketAddress(9000), 0);
        server.createContext("/api/search", new HttpHandler() {
            @Override
            public void handle(HttpExchange he) throws IOException {
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
