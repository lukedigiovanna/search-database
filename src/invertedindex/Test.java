package invertedindex;

import java.util.Set;

public class Test {
    public static void main(String[] args) {
        Document d1 = new Document("Sentence 1", "The lazy dog jumped over the river. It turns out he isn't that lazy.");
        Document d2 = new Document("Sentence 2", "How about that for a turn of events! In the second inning, baseball has never been more exciting!");
        Document d3 = new Document("Sentence 3", "What is the difference between a horse and a donkey? I don't really know, but I do like dogs");

        InvertedIndex index = new InvertedIndex();
        index.add(d1);
        index.add(d2);
        index.add(d3);

        Set<Document> found = index.search("the");
        for (Document d : found) {
            System.out.println(d.body());
        }

    }
}
