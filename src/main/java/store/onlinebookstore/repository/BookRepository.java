package store.onlinebookstore.repository;

import java.util.List;
import store.onlinebookstore.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
