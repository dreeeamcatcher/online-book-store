package store.onlinebookstore.dto;

public record BookSearchParameters(String[] authors,
                                   String[] title,
                                   String[] lowerPrice,
                                   String[] upperPrice) {
    
}
