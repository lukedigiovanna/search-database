package invertedindex;

public class IndexData {
    // Store a reference to the document
    private Document document;
    // Store the weights of this word for the given document
    // this also conveniently stores the count of the word in the document
    private float[] weights;
    private int numWeights;

    public IndexData(Document document) {
        this.document = document;
        this.weights = new float[20];
    }

    public Document getDocument() {
        return this.document;
    }

    public void addWeight(int wordIndex) {

    }

}
