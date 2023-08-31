package store.onlinebookstore.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import store.onlinebookstore.dto.book.BookDto;
import store.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import store.onlinebookstore.dto.book.BookSearchParameters;
import store.onlinebookstore.dto.book.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    List<BookDto> findAll(Pageable pageable);

    BookDto getBookById(Long id);

    void deleteById(Long id);

    BookDto updateBookById(Long id, CreateBookRequestDto book);

    List<BookDto> search(BookSearchParameters searchParameters);

    List<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long id);
}
