# Details

Date : 2022-12-06 10:45:05

Directory /Users/lukedigiovanna/Programming/java-projects/SearchDatabase

Total : 51 files,  2747 codes, 359 comments, 295 blanks, all 3401 lines

[Summary](results.md) / Details / [Diff Summary](diff.md) / [Diff Details](diff-details.md)

## Files
| filename | language | code | comment | blank | total |
| :--- | :--- | ---: | ---: | ---: | ---: |
| [README.md](/README.md) | Markdown | 3 | 0 | 1 | 4 |
| [bin/app/ConstructIndex.class](/bin/app/ConstructIndex.class) | Java | 37 | 0 | 0 | 37 |
| [bin/app/Driver.class](/bin/app/Driver.class) | Java | 17 | 0 | 0 | 17 |
| [bin/app/Server$1.class](/bin/app/Server$1.class) | Java | 11 | 0 | 1 | 12 |
| [bin/app/Server$2.class](/bin/app/Server$2.class) | Java | 11 | 0 | 1 | 12 |
| [bin/app/Server$3.class](/bin/app/Server$3.class) | Java | 12 | 0 | 0 | 12 |
| [bin/app/Server$4.class](/bin/app/Server$4.class) | Java | 12 | 0 | 0 | 12 |
| [bin/app/Server.class](/bin/app/Server.class) | Java | 85 | 0 | 1 | 86 |
| [bin/articleRetrieval/Crawler.class](/bin/articleRetrieval/Crawler.class) | Java | 90 | 0 | 2 | 92 |
| [bin/articleRetrieval/Document.class](/bin/articleRetrieval/Document.class) | Java | 6 | 0 | 0 | 6 |
| [bin/articleRetrieval/Retriever.class](/bin/articleRetrieval/Retriever.class) | Java | 72 | 0 | 1 | 73 |
| [bin/articleRetrieval/RetrieverResults.class](/bin/articleRetrieval/RetrieverResults.class) | Java | 7 | 0 | 0 | 7 |
| [bin/articleRetrieval/WikipediaArticle.class](/bin/articleRetrieval/WikipediaArticle.class) | Java | 33 | 0 | 0 | 33 |
| [bin/articleRetrieval/WikipediaImage.class](/bin/articleRetrieval/WikipediaImage.class) | Java | 23 | 0 | 0 | 23 |
| [bin/externalInvertedIndex/DocumentData.class](/bin/externalInvertedIndex/DocumentData.class) | Java | 8 | 0 | 0 | 8 |
| [bin/externalInvertedIndex/ExternalDocument.class](/bin/externalInvertedIndex/ExternalDocument.class) | Java | 14 | 0 | 0 | 14 |
| [bin/externalInvertedIndex/ExternalInvertedIndex.class](/bin/externalInvertedIndex/ExternalInvertedIndex.class) | Java | 175 | 0 | 0 | 175 |
| [bin/externalInvertedIndex/GenerateIndexFile.class](/bin/externalInvertedIndex/GenerateIndexFile.class) | Java | 63 | 0 | 0 | 63 |
| [bin/externalInvertedIndex/TestIndex.class](/bin/externalInvertedIndex/TestIndex.class) | Java | 26 | 0 | 0 | 26 |
| [bin/invertedindex/DocumentWeightPair.class](/bin/invertedindex/DocumentWeightPair.class) | Java | 12 | 0 | 0 | 12 |
| [bin/invertedindex/InvertedIndex.class](/bin/invertedindex/InvertedIndex.class) | Java | 45 | 0 | 0 | 45 |
| [bin/utils/MemoryCheck.class](/bin/utils/MemoryCheck.class) | Java | 21 | 0 | 0 | 21 |
| [bin/utils/Pair.class](/bin/utils/Pair.class) | Java | 19 | 0 | 0 | 19 |
| [bin/utils/Stemmer.class](/bin/utils/Stemmer.class) | Java | 52 | 9 | 0 | 61 |
| [bin/utils/StopWords.class](/bin/utils/StopWords.class) | Java | 30 | 0 | 1 | 31 |
| [bin/utils/StringUtils.class](/bin/utils/StringUtils.class) | Java | 7 | 0 | 0 | 7 |
| [bin/utils/Tokenizer.class](/bin/utils/Tokenizer.class) | Java | 20 | 0 | 0 | 20 |
| [public_html/home.html](/public_html/home.html) | HTML | 47 | 0 | 2 | 49 |
| [public_html/searchpage.html](/public_html/searchpage.html) | HTML | 212 | 0 | 18 | 230 |
| [src/app/ConstructIndex.java](/src/app/ConstructIndex.java) | Java | 70 | 0 | 17 | 87 |
| [src/app/Driver.java](/src/app/Driver.java) | Java | 19 | 3 | 9 | 31 |
| [src/app/Server.java](/src/app/Server.java) | Java | 125 | 11 | 19 | 155 |
| [src/articleRetrieval/Crawler.java](/src/articleRetrieval/Crawler.java) | Java | 102 | 11 | 20 | 133 |
| [src/articleRetrieval/Document.java](/src/articleRetrieval/Document.java) | Java | 10 | 22 | 5 | 37 |
| [src/articleRetrieval/Retriever.java](/src/articleRetrieval/Retriever.java) | Java | 99 | 11 | 17 | 127 |
| [src/articleRetrieval/RetrieverResults.java](/src/articleRetrieval/RetrieverResults.java) | Java | 10 | 0 | 4 | 14 |
| [src/articleRetrieval/WikipediaArticle.java](/src/articleRetrieval/WikipediaArticle.java) | Java | 75 | 24 | 18 | 117 |
| [src/articleRetrieval/WikipediaImage.java](/src/articleRetrieval/WikipediaImage.java) | Java | 59 | 5 | 13 | 77 |
| [src/externalInvertedIndex/DocumentData.java](/src/externalInvertedIndex/DocumentData.java) | Java | 11 | 3 | 3 | 17 |
| [src/externalInvertedIndex/ExternalDocument.java](/src/externalInvertedIndex/ExternalDocument.java) | Java | 19 | 0 | 6 | 25 |
| [src/externalInvertedIndex/ExternalInvertedIndex.java](/src/externalInvertedIndex/ExternalInvertedIndex.java) | Java | 258 | 62 | 36 | 356 |
| [src/externalInvertedIndex/GenerateIndexFile.java](/src/externalInvertedIndex/GenerateIndexFile.java) | Java | 90 | 7 | 16 | 113 |
| [src/externalInvertedIndex/TestIndex.java](/src/externalInvertedIndex/TestIndex.java) | Java | 32 | 0 | 8 | 40 |
| [src/invertedindex/DocumentWeightPair.java](/src/invertedindex/DocumentWeightPair.java) | Java | 14 | 0 | 5 | 19 |
| [src/invertedindex/InvertedIndex.java](/src/invertedindex/InvertedIndex.java) | Java | 77 | 19 | 14 | 110 |
| [src/utils/MemoryCheck.java](/src/utils/MemoryCheck.java) | Java | 15 | 4 | 5 | 24 |
| [src/utils/Pair.java](/src/utils/Pair.java) | Java | 25 | 0 | 7 | 32 |
| [src/utils/Stemmer.java](/src/utils/Stemmer.java) | Java | 418 | 144 | 34 | 596 |
| [src/utils/StopWords.java](/src/utils/StopWords.java) | Java | 27 | 9 | 5 | 41 |
| [src/utils/StringUtils.java](/src/utils/StringUtils.java) | Java | 6 | 0 | 2 | 8 |
| [src/utils/Tokenizer.java](/src/utils/Tokenizer.java) | Java | 16 | 15 | 4 | 35 |

[Summary](results.md) / Details / [Diff Summary](diff.md) / [Diff Details](diff-details.md)