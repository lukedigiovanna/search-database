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

import externalInvertedIndex.ExternalInvertedIndex;
import invertedindex.DocumentWeightPair;
import utils.Pair;

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

    private static String getParam(Map<String, String> params, String key) {
        if (params.containsKey(key)) {
            return params.get(key);
        } else {
            return "";
        }
    }

    private static boolean isNumeric(String s) {
        return s.matches("^[0-9]+");
    }

    private static int ARTICLE = 0, IMAGE = 1;

    private static void handleSearchRequest(HttpExchange he, ExternalInvertedIndex index, int type)
            throws IOException {
        // extract parameters
        Map<String, String> params = getQueryParams(he.getRequestURI().getQuery());
        String term = getParam(params, "term");
        String limitS = getParam(params, "limit");
        int limit = 100;
        if (isNumeric(limitS)) {
            limit = Math.min(limit, Integer.parseInt(limitS));
        }
        String offsetS = getParam(params, "offset");
        int offset = 0;
        if (offsetS.length() > 0) {
            offset = Integer.parseInt(offsetS);
        }

        // perform the search
        long currTime = System.currentTimeMillis();
        List<Pair<Integer, Float>> searchPairs = index.search(term);
        List<DocumentWeightPair> found;
        if (type == ARTICLE) {
            found = index.getArticleData(searchPairs, offset, limit);
        } else {
            found = index.getImageData(searchPairs, offset, limit);
        }
        long elapsed = System.currentTimeMillis() - currTime;
        JSONObject response = new JSONObject();
        JSONArray results = new JSONArray();

        for (int i = 0; i < found.size(); i++) {
            DocumentWeightPair pair = found.get(i);
            JSONObject articleJSON = pair.document.getJSON();
            articleJSON.put("score", pair.weight);
            results.put(articleJSON);
        }

        // write top level JSON object
        response.put("results", results);
        response.put("time", elapsed);
        response.put("resultCount", searchPairs.size());

        // write raw output
        String res = response.toString();
        byte[] rawData = res.getBytes();
        he.sendResponseHeaders(200, rawData.length);
        OutputStream os = he.getResponseBody();
        os.write(rawData);
        os.close();
    }

    private static void handleHTMLRequest(HttpExchange he, String filePath) throws IOException {
        String indexHtml = "";
        BufferedReader inp = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = inp.readLine()) != null) {
            indexHtml += line + "\n";
        }
        inp.close();
        String res = indexHtml;
        he.sendResponseHeaders(200, res.length());
        OutputStream os = he.getResponseBody();
        os.write(res.getBytes());
        os.close();
    }

    private HttpServer server;
    private int port;

    public Server(int port, ExternalInvertedIndex articleIndex, ExternalInvertedIndex imageIndex)
            throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.port = port;
        this.server.createContext("/api/search", new HttpHandler() {
            @Override
            public void handle(HttpExchange he) throws IOException {
                handleSearchRequest(he, articleIndex, ARTICLE);
            }
        });
        this.server.createContext("/api/image-search", new HttpHandler() {
            @Override
            public void handle(HttpExchange he) throws IOException {
                handleSearchRequest(he, imageIndex, IMAGE);
            }
        });
        this.server.createContext("/search", new HttpHandler() {
            @Override
            public void handle(HttpExchange he) throws IOException {
                handleHTMLRequest(he, "public_html/searchpage.html");
            }
        });
        this.server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange he) throws IOException {
                handleHTMLRequest(he, "public_html/home.html");
            }
        });
        this.server.setExecutor(null);
    }

    public void start() {
        this.server.start();
        System.out.println("[-] Started API server locally on port " + this.port);
    }
}
