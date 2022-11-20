package articleRetrieval;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import org.jsoup.Jsoup;

/**
 * Functionality to make requests to Wikipedia API to gather
 * articles.
 */
public class Retriever {
    private static JSONObject get(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            return new JSONObject(content.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static WikipediaArticle getArticle(String articleTitle) {
        // makes requests to the wikipedia API to get the content of the given article
        try {
            JSONObject article = get("https://en.wikipedia.org/w/api.php?action=parse&page="+articleTitle+"&prop=text&format=json");            
            JSONObject parse = article.getJSONObject("parse");
            String fullTitle = parse.getString("title");
            String htmlContent = parse.getJSONObject("text")
                                .getString("*");
            String doc = Jsoup.parse(htmlContent).text();
            
            // TODO: get list of linked articles.
            JSONObject links = get("https://en.wikipedia.org/w/api.php?action=query&titles=" + articleTitle + "&prop=links&format=json&pllimit=max");
            System.out.println(links.toString(5));

            JSONObject pages = links.getJSONObject("query").getJSONObject("pages");
            String pageName = pages.keys().next();
            JSONArray arr = pages.getJSONObject(pageName).getJSONArray("links");
            Iterator<Object> it = arr.iterator();
            List<String> linksTo = new ArrayList<>();

            while (it.hasNext()) {
                
                try {
                    linksTo.add( 
                        ((JSONObject)it.next()).getString("title")
                    );
                }
                catch (Exception e) {

                }
            }

            return new WikipediaArticle(fullTitle, doc, linksTo);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            
            return null;
        }
        
    }
}
