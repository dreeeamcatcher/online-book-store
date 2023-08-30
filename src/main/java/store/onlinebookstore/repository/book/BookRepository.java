package store.onlinebookstore.repository.book;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import store.onlinebookstore.model.Book;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    @Query("FROM Book b LEFT JOIN FETCH b.categories c WHERE c.id = :categoryId")
    List<Book> findAllByCategoryId(Long categoryId);

    @Query(value = "FROM Book b LEFT JOIN FETCH b.categories")
    Page<Book> findAll(Pageable pageable);

    @Query(value = "FROM Book b LEFT JOIN FETCH b.categories WHERE b.id = :id")
    Optional<Book> findById(Long id);
}
