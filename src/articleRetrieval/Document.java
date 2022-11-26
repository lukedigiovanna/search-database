package articleRetrieval;

public interface Document {
    
    public String body();

    public String[] tokens();

    public JSONObject getJSON();
}
