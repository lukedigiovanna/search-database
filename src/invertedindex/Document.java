package invertedindex;

public class Document {
    private String body;
    private String title;
    

    public Document(String title, String body) {
        this.body = body;
        this.title = title;
    }

    public String body() {
        return this.body;
    }   
}
