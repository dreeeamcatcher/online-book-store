package store.onlinebookstore.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import store.onlinebookstore.dto.book.BookDto;
import store.onlinebookstore.dto.book.BookSearchParameters;
import store.onlinebookstore.dto.book.CreateBookRequestDto;
import store.onlinebookstore.exception.EntityNotFoundException;
import store.onlinebookstore.mapper.BookMapper;
import store.onlinebookstore.model.Book;
import store.onlinebookstore.repository.book.BookRepository;
import store.onlinebookstore.repository.book.BookSpecificationBuilder;
import store.onlinebookstore.service.BookService;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
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

    @Override
    public List<BookDto> search(BookSearchParameters searchParameters) {
        Specification<Book> spec = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(spec)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
