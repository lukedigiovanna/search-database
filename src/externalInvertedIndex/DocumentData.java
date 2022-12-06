package externalInvertedIndex;

/**
 * Struct to store the most pertinent information for rank
 */
public class DocumentData {
    public long offset; // corresponding byte offset of the `articles` database file
    public int links; // number of links that point to this article
    public short length; // length of the content of this article

    public DocumentData(long offset, int links, short length) {
        this.offset = offset;
        this.links = links;
        this.length = length;
    }
}
