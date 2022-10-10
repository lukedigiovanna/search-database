package database;

public class Database {
    
    // store global word counts.
    private Corpus corpus; 

    public Database() {

    }

    public void insert() {

    }

    /**
     * Queries the Database with the given search terms.
     * Performs a relational full text search on the B+ Tree structure.
     * @param terms Array of no-space string terms to search for
     * @return Matching records.
     */
    public DatabaseRecord[] query(String[] terms) {
        return null;
    }

}
