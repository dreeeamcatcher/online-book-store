package store.onlinebookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.onlinebookstore.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
