package store.onlinebookstore.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import store.onlinebookstore.dto.book.BookDto;
import store.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import store.onlinebookstore.dto.book.BookSearchParameters;
import store.onlinebookstore.dto.book.CreateBookRequestDto;
import store.onlinebookstore.exception.EntityNotFoundException;
import store.onlinebookstore.mapper.BookMapper;
import store.onlinebookstore.mapper.CategoryMapper;
import store.onlinebookstore.model.Book;
import store.onlinebookstore.model.Category;
import store.onlinebookstore.repository.book.BookRepository;
import store.onlinebookstore.repository.book.BookSpecificationBuilder;
import store.onlinebookstore.service.BookService;
import store.onlinebookstore.service.CategoryService;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Set<Category> categories = getCategoriesFromIds(requestDto.getCategoryIds());

        Book book = bookMapper.toModel(requestDto);
        book.setCategories(categories);

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
    public BookDto updateBookById(Long id, CreateBookRequestDto requestDto) {
        // Check if book with given index is present in the DB
        BookDto bookById = getBookById(id);

        Set<Category> categoriesFromRequest = getCategoriesFromIds(requestDto.getCategoryIds());

        Book updatedBook = bookMapper.toModel(requestDto);
        updatedBook.setId(bookById.getId());
        updatedBook.setCategories(categoriesFromRequest);

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

    @Override
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long id) {
        return bookRepository.findAllByCategoryId(id)
                .stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

    private Set<Category> getCategoriesFromIds(List<Long> ids) {
        Set<Category> categories = new HashSet<>();

        for (Long id : ids) {
            categories.add(categoryMapper.toModel(categoryService.getById(id)));
        }
        return categories;
    }
}
