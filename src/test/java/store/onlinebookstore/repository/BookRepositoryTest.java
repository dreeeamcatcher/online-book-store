package store.onlinebookstore.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import store.onlinebookstore.model.Book;
import store.onlinebookstore.repository.book.BookRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Sql(scripts = {
            "classpath:database/categories/add-categories.sql",
            "classpath:database/books/add-books.sql",
            "classpath:database/categories/associate-categories-to-books.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/categories/remove-categories-from-books.sql",
            "classpath:database/categories/remove-categories.sql",
            "classpath:database/books/remove-books.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Find all books by category id")
    void findAllByCategoryId_ValidCategoryIds_ReturnListOfBooks() {
        // When
        List<Book> fantasyBooks = bookRepository.findAllByCategoryId(1L);
        List<Book> scifiBooks = bookRepository.findAllByCategoryId(2L);
        List<Book> detectiveBooks = bookRepository.findAllByCategoryId(3L);

        // Then
        assertThat(fantasyBooks).hasSize(2);
        assertThat(scifiBooks).hasSize(1);
        assertThat(detectiveBooks).hasSize(1);
        assertEquals("1984", scifiBooks.get(0).getTitle());
        assertEquals("The Adventures of Sherlock Holmes", detectiveBooks.get(0).getTitle());
    }

    @Sql(scripts = {
            "classpath:database/categories/add-categories.sql",
            "classpath:database/books/add-books.sql",
            "classpath:database/categories/associate-categories-to-books.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/categories/remove-categories-from-books.sql",
            "classpath:database/categories/remove-categories.sql",
            "classpath:database/books/remove-books.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Find all categories")
    void findAll() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Book> allBooks = bookRepository.findAll(pageable);

        // Then
        assertThat(allBooks).hasSize(4);
    }

    @Sql(scripts = {
            "classpath:database/categories/add-categories.sql",
            "classpath:database/books/add-books.sql",
            "classpath:database/categories/associate-categories-to-books.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/categories/remove-categories-from-books.sql",
            "classpath:database/categories/remove-categories.sql",
            "classpath:database/books/remove-books.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Find category by id")
    void findById() {
        // When
        Optional<Book> byId1 = bookRepository.findById(1L);
        Optional<Book> byId4 = bookRepository.findById(4L);

        // Then
        assertTrue(byId1.isPresent());
        assertTrue(byId4.isPresent());
        assertEquals("1984", byId1.get().getTitle());
        assertEquals("A Game of Thrones", byId4.get().getTitle());
    }
}
