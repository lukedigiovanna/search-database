package articleRetrieval;

import java.util.Map;

import org.json.JSONObject;

public interface Document {
    /**
     * Gets the body of searchable content for the particular document
     * @return String of direct representation of the document
     */
    public String body();
    /**
     * Returns, in order, the unique tokens that make up the body of this document.
     * @return String array of unique tokens
     */
    public String[] tokens();
    /**
     * Gets the JSON representation of this document, specifically used when delivering
     * search results to a client.
     * @return
     */
    public JSONObject getJSON();
    /**
     * Calculates a relevancy score from a given search embedding.
     * @param searchEmbedding Vectorized form of the search
     * @return A floating-point numeric score of the relevancy
     */
    public float calculateRanking(Map<String, Float> searchEmbedding);
    /**
     * Gets the embedding map associated with this document.
     * @return The current embedding of the document
     */
    public Map<String, Float> getEmbedding();

}
