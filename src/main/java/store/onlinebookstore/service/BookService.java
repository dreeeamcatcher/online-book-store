package store.onlinebookstore.service;

import java.util.List;
import store.onlinebookstore.dto.BookDto;
import store.onlinebookstore.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    List<BookDto> findAll();

    BookDto getBookById(Long id);

    void deleteById(Long id);

    BookDto updateBookById(Long id, CreateBookRequestDto book);
}
