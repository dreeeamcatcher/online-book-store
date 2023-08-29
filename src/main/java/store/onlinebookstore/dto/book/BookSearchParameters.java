package store.onlinebookstore.dto.book;

public record BookSearchParameters(String[] authors,
                                   String[] title,
                                   String[] lowerPrice,
                                   String[] upperPrice) {
    
}
