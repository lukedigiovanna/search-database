package articleRetrieval;

public class Crawler {
    public static void main(String[] args) {

        WikipediaArticle article = Retriever.getArticle("Pet_door");

        article.saveData("articles");

        for (String s : article.getLinkedArticle()) {
            System.out.println(s);
        }
    }
}
