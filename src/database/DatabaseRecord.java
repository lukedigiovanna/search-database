package database;

import java.util.Map;

/**
 * Holds relevant information to the content of a record.
 */
public class DatabaseRecord {
    // the main content of the record to be searchable for.
    private String searchableContent;
    // metadata contains additional information about the given record
    // this may include something such as a URL, title, Image, etc.
    private Map<String, String> metadata;

    public DatabaseRecord() {

    }

}