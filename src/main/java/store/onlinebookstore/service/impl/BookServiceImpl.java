package store.onlinebookstore.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.onlinebookstore.dto.BookDto;
import store.onlinebookstore.dto.CreateBookRequestDto;
import store.onlinebookstore.exception.EntityNotFoundException;
import store.onlinebookstore.mapper.BookMapper;
import store.onlinebookstore.model.Book;
import store.onlinebookstore.repository.BookRepository;
import store.onlinebookstore.service.BookService;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id: " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto updateBookById(Long id, CreateBookRequestDto bookDto) {
        // Check if book with given index is present in the DB
        BookDto bookById = getBookById(id);

        Book updatedBook = bookMapper.toModel(bookDto);
        updatedBook.setId(bookById.getId());

        return bookMapper.toDto(bookRepository.save(updatedBook));
    }
}
