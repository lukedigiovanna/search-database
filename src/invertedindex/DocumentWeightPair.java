package invertedindex;

import articleRetrieval.Document;

public class DocumentWeightPair implements Comparable<DocumentWeightPair> {
    public Document document;
    public Float weight;

    public DocumentWeightPair(Document document, Float weight) {
        this.document = document;
        this.weight = weight;
    }

    @Override
    public int compareTo(DocumentWeightPair o) {
        return this.weight.compareTo(o.weight);
    }
}
