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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
            return null;
        }
    }

    public static RetrieverResults getArticle(String articleTitle) {
        articleTitle = articleTitle.replace(' ', '_');
        // makes requests to the wikipedia API to get the content of the given article
        try {
            JSONObject article = get("https://en.wikipedia.org/w/api.php?action=parse&page="+articleTitle+"&prop=text&format=json&redirects=true");            
            JSONObject parse = article.getJSONObject("parse");
            if (parse == null) {
                return null;
            }
            String fullTitle = parse.getString("title");
            String htmlContent = parse.getJSONObject("text")
                                .getString("*");
            Document htmlDoc = Jsoup.parse(htmlContent);
            String doc = htmlDoc.children().select("p")
                            .text() // extract content
                            .replaceAll("\\[.*\\]", "") // remove references
                            .replace("\n", ""); // remove new line chars
            // select all img tags
            Elements elements = htmlDoc.select(".thumb");
            for (Element el : elements) {
                // System.out.println(el.attr("src"));
                Element img = el.selectFirst("img");
                String alt = img.attr("alt");
                String url = img.attr("src").substring(47);
                String caption = el.text().replaceAll("\\[.*\\]", "");
                System.out.println(alt + " " + caption);
                System.out.println(url);

            }

            JSONObject links = get("https://en.wikipedia.org/w/api.php?action=query&titles=" + articleTitle + "&prop=links&format=json&pllimit=max");
            
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
                catch (Exception e) {}
            }

            JSONObject inbound = get("https://linkcount.toolforge.org/api/?page=" + articleTitle + "&project=en.wikipedia.org&namespaces=0");
            int inboundCount = inbound.getJSONObject("wikilinks").getInt("all");

            WikipediaArticle wikiArticle = new WikipediaArticle(fullTitle, doc, inboundCount, linksTo);

            RetrieverResults results = new RetrieverResults(wikiArticle, null);

            return results;
        }
        catch (Exception ex) {
            return null;
        }
        
    }


    /**
     * Tests single instance of Retriever.getArticle function.
     * @param args
     */
    public static void main(String[] args) {
        Retriever.getArticle("Donald Trump");
    }

}
