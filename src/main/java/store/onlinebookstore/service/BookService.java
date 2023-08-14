package store.onlinebookstore.service;

import java.util.List;
import store.onlinebookstore.model.Book;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
