package app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.*;

import com.sun.net.httpserver.*;

import articleRetrieval.Document;
import articleRetrieval.WikipediaArticle;
import articleRetrieval.WikipediaImage;
import invertedindex.DocumentWeightPair;
import invertedindex.InvertedIndex;

/**
 * Sets up a server that will handle requests for search operations.
 * 
 * Constructs a RESTful API for use on the inverted index.
 * 
 * Currently does not support CRUD operations (only READ)
 */
public class Server {
    private static Map<String, String> getQueryParams(String query) {
        Map<String, String> params = new LinkedHashMap<>();

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int equalIndex = pair.indexOf("=");
            params.put(pair.substring(0, equalIndex), pair.substring(equalIndex + 1));
        }

        return params;
    }

    private static <T extends Document> void handleSearchRequest(HttpExchange he, InvertedIndex<T> index) throws IOException {
        Map<String, String> params = getQueryParams(he.getRequestURI().getQuery());
                String term = params.get("term");
                long currTime = System.currentTimeMillis();
                List<DocumentWeightPair> found = index.search(term);
                long elapsed = System.currentTimeMillis() - currTime;
                JSONObject response = new JSONObject();
                JSONArray results = new JSONArray();
                for (DocumentWeightPair pair : found) {
                    JSONObject articleJSON = pair.document.getJSON();
                    articleJSON.put("score", pair.weight);
                    results.put(articleJSON);
                }
                response.put("results", results);
                response.put("time", elapsed);
                String res = response.toString();
                he.sendResponseHeaders(200, res.length());
                OutputStream os = he.getResponseBody();
                os.write(res.getBytes());
                os.close();
    }

    private HttpServer server;
    private int port;

    public Server(int port, InvertedIndex<WikipediaArticle> articleIndex, InvertedIndex<WikipediaImage> imageIndex) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.port = port;
        this.server.createContext("/api/search", new HttpHandler() {
            @Override
            public void handle(HttpExchange he) throws IOException {
                handleSearchRequest(he, articleIndex);
            }
        });
        this.server.createContext("/api/image-search", new HttpHandler() {
            @Override
            public void handle(HttpExchange he) throws IOException {
                handleSearchRequest(he, imageIndex);
            }
        });
        this.server.createContext("/", new HttpHandler() {
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
        this.server.setExecutor(null);
    }

    public void start() {
        this.server.start();
        System.out.println("[-] Started API server locally on port " + this.port);
    }
}
