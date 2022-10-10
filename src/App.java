import database.Database;

public class App {
    public static void main(String[] args) throws Exception {
        Database database = new Database();
        database.insert();
    }
}
