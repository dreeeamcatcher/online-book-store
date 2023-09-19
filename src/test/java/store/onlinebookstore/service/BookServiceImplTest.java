package store.onlinebookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import store.onlinebookstore.dto.book.BookDto;
import store.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import store.onlinebookstore.dto.book.BookSearchParameters;
import store.onlinebookstore.dto.book.CreateBookRequestDto;
import store.onlinebookstore.dto.category.CategoryDto;
import store.onlinebookstore.exception.EntityNotFoundException;
import store.onlinebookstore.mapper.BookMapper;
import store.onlinebookstore.model.Book;
import store.onlinebookstore.model.Category;
import store.onlinebookstore.repository.book.BookRepository;
import store.onlinebookstore.repository.book.BookSpecificationBuilder;
import store.onlinebookstore.repository.category.CategoryRepository;
import store.onlinebookstore.service.impl.BookServiceImpl;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookSpecificationBuilder specificationBuilder;

    @Test
    @DisplayName("Save a book")
    public void save_ValidCreateBookRequestDto_ReturnsValidBookDto() {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("The Lord of the Rings");
        requestDto.setAuthor("J. R. R. Tolkien");
        requestDto.setIsbn("9780544003415");
        requestDto.setPrice(BigDecimal.valueOf(15.5));
        requestDto.setDescription("Awesome book");
        requestDto.setCoverImage("The Lord of the Rings image");
        requestDto.setCategoryIds(List.of(1L));

        Category fantasyCategory = new Category();
        fantasyCategory.setId(1L);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");

        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.setDescription(requestDto.getDescription());
        book.setCoverImage(requestDto.getCoverImage());

        CategoryDto fantasyCategoryDto = new CategoryDto();
        fantasyCategoryDto.setId(fantasyCategory.getId());
        fantasyCategoryDto.setName(fantasyCategory.getName());
        fantasyCategoryDto.setDescription(fantasyCategory.getDescription());

        BookDto bookDto = new BookDto();
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setCategories(new LinkedHashSet<>(List.of(fantasyCategoryDto)));

        when(categoryRepository.getCategoriesByIdIn(anyList()))
                .thenReturn(new HashSet<>(List.of(fantasyCategory)));
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        // When
        BookDto actual = bookService.save(requestDto);

        // Then
        assertEquals(bookDto, actual);
        verify(categoryRepository, times(1)).getCategoriesByIdIn(List.of(1L));
        verify(bookRepository, times(1)).save(book);
        verifyNoMoreInteractions(categoryRepository, bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find all books")
    public void findAll_ValidPageable_ReturnsAllBooks() {
        // Given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("The Lord of the Rings");
        book.setAuthor("J. R. R. Tolkien");
        book.setIsbn("9780544003415");
        book.setPrice(BigDecimal.valueOf(15.5));
        book.setDescription("Awesome book");
        book.setCoverImage("The Lord of the Rings image");

        Category fantasyCategory = new Category();
        fantasyCategory.setId(1L);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");

        book.setCategories(new HashSet<>(List.of(fantasyCategory)));

        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());

        CategoryDto fairytaleCategoryDto = new CategoryDto();
        fairytaleCategoryDto.setId(fantasyCategory.getId());
        fairytaleCategoryDto.setName(fantasyCategory.getName());
        fairytaleCategoryDto.setDescription(fantasyCategory.getDescription());

        bookDto.setCategories(new HashSet<>(List.of(fairytaleCategoryDto)));

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        // When
        List<BookDto> bookDtos = bookService.findAll(pageable);

        // Then
        assertThat(bookDtos).hasSize(1);
        assertEquals(bookDtos.get(0), bookDto);
        verify(bookRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Get a book by id")
    public void getBookById_ValidBookId_ReturnsValidBookDto() {
        // Given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("The Lord of the Rings");
        book.setAuthor("J. R. R. Tolkien");
        book.setIsbn("9780544003415");
        book.setPrice(BigDecimal.valueOf(15.5));
        book.setDescription("Awesome book");
        book.setCoverImage("The Lord of the Rings image");

        Category fantasyCategory = new Category();
        fantasyCategory.setId(1L);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");

        book.setCategories(new HashSet<>(List.of(fantasyCategory)));

        CategoryDto fantasyCategoryDto = new CategoryDto();
        fantasyCategoryDto.setId(fantasyCategory.getId());
        fantasyCategoryDto.setName(fantasyCategory.getName());
        fantasyCategoryDto.setDescription(fantasyCategory.getDescription());

        BookDto bookDto = new BookDto();
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setCategories(new LinkedHashSet<>(List.of(fantasyCategoryDto)));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        // When
        BookDto actual = bookService.getBookById(bookId);

        // Then
        assertEquals(bookDto, actual);
        verify(bookRepository, times(1)).findById(bookId);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Get a book with not valid id")
    public void getBookById_NotValidBookId_ThrowsException() {
        // Given
        Long bookId = 100L;
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                bookService.getBookById(bookId));

        // Then
        String expected = "Can't find book by id: " + bookId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Update a book")
    public void updateBookById_ValidBookId_ReturnsUpdatedBookDto() {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("The Lord of the Rings updated");
        requestDto.setAuthor("J. R. R. Tolkien");
        requestDto.setIsbn("9780544003415-updated");
        requestDto.setPrice(BigDecimal.valueOf(19.5));
        requestDto.setDescription("Awesome updated book");
        requestDto.setCoverImage("The Lord of the Rings updated image");
        requestDto.setCategoryIds(List.of(1L));

        Book bookFromRequest = new Book();
        bookFromRequest.setTitle(requestDto.getTitle());
        bookFromRequest.setAuthor(requestDto.getAuthor());
        bookFromRequest.setIsbn(requestDto.getIsbn());
        bookFromRequest.setPrice(requestDto.getPrice());
        bookFromRequest.setDescription(requestDto.getDescription());
        bookFromRequest.setCoverImage(requestDto.getCoverImage());

        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("The Lord of the Rings");
        book.setAuthor("J. R. R. Tolkien");
        book.setIsbn("9780544003415");
        book.setPrice(BigDecimal.valueOf(15.5));
        book.setDescription("Awesome book");
        book.setCoverImage("The Lord of the Rings image");

        Category fantasyCategory = new Category();
        fantasyCategory.setId(1L);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");

        CategoryDto fantasyCategoryDto = new CategoryDto();
        fantasyCategoryDto.setId(fantasyCategory.getId());
        fantasyCategoryDto.setName(fantasyCategory.getName());
        fantasyCategoryDto.setDescription(fantasyCategory.getDescription());

        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setCategories(new HashSet<>(List.of(fantasyCategoryDto)));

        BookDto updatedDto = new BookDto();
        updatedDto.setId(bookId);
        updatedDto.setTitle(bookFromRequest.getTitle());
        updatedDto.setAuthor(bookFromRequest.getAuthor());
        updatedDto.setIsbn(bookFromRequest.getIsbn());
        updatedDto.setPrice(bookFromRequest.getPrice());
        updatedDto.setDescription(bookFromRequest.getDescription());
        updatedDto.setCoverImage(bookFromRequest.getCoverImage());
        updatedDto.setCategories(new HashSet<>(List.of(fantasyCategoryDto)));;

        BookDto expectedDto = new BookDto();
        expectedDto.setId(bookId);
        expectedDto.setTitle(requestDto.getTitle());
        expectedDto.setAuthor(requestDto.getAuthor());
        expectedDto.setIsbn(requestDto.getIsbn());
        expectedDto.setPrice(requestDto.getPrice());
        expectedDto.setDescription(requestDto.getDescription());
        expectedDto.setCoverImage(requestDto.getCoverImage());
        expectedDto.setCategories(new HashSet<>(List.of(fantasyCategoryDto)));

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(categoryRepository.getCategoriesByIdIn(anyList()))
                .thenReturn(new HashSet<>(List.of(fantasyCategory)));
        when(bookMapper.toModel(requestDto)).thenReturn(bookFromRequest);
        when(bookRepository.save(bookFromRequest)).thenReturn(bookFromRequest);
        when(bookMapper.toDto(bookFromRequest)).thenReturn(updatedDto);

        // When
        BookDto updateBookDto = bookService.updateBookById(bookId, requestDto);

        // Then
        assertEquals(expectedDto, updateBookDto);
        verify(bookRepository, times(1)).findById(anyLong());
        verify(categoryRepository, times(1)).getCategoriesByIdIn(anyList());
        verify(bookRepository, times(1)).save(any(Book.class));
        verifyNoMoreInteractions(bookRepository, categoryRepository, bookMapper);
    }

    @Test
    @DisplayName("Search books")
    public void search_ValidParameters_ReturnsListOfBooks() {
        // Given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("The Lord of the Rings");
        book.setAuthor("J. R. R. Tolkien");
        book.setIsbn("9780544003415");
        book.setPrice(BigDecimal.valueOf(15.5));
        book.setDescription("Awesome book");
        book.setCoverImage("The Lord of the Rings image");

        Category fantasyCategory = new Category();
        fantasyCategory.setId(1L);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");

        CategoryDto fantasyCategoryDto = new CategoryDto();
        fantasyCategoryDto.setId(fantasyCategory.getId());
        fantasyCategoryDto.setName(fantasyCategory.getName());
        fantasyCategoryDto.setDescription(fantasyCategory.getDescription());

        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setCategories(new HashSet<>(List.of(fantasyCategoryDto)));

        BookSearchParameters parameters = new BookSearchParameters(
                new String[]{"test author"},
                new String[]{"test title"},
                new String[]{""},
                new String[]{""});

        Specification<Book> spec = (root, query, criteriaBuilder) -> null;

        when(specificationBuilder.build(parameters)).thenReturn(spec);
        when(bookRepository.findAll(spec)).thenReturn(List.of(book));
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        // When
        List<BookDto> searchedBooks = bookService.search(parameters);

        // Then
        assertThat(searchedBooks).hasSize(1);
        assertEquals(bookDto, searchedBooks.get(0));
        verify(bookRepository, times(1)).findAll(spec);
        verifyNoMoreInteractions(bookRepository, specificationBuilder, bookMapper);
    }

    @Test
    @DisplayName("Search books with not valid parameters")
    public void search_NotValidParameters_ReturnsEmptyList() {
        // Given

        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("The Lord of the Rings");
        book.setAuthor("J. R. R. Tolkien");
        book.setIsbn("9780544003415");
        book.setPrice(BigDecimal.valueOf(15.5));
        book.setDescription("Awesome book");
        book.setCoverImage("The Lord of the Rings image");

        Category fantasyCategory = new Category();
        fantasyCategory.setId(1L);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");

        CategoryDto fantasyCategoryDto = new CategoryDto();
        fantasyCategoryDto.setId(fantasyCategory.getId());
        fantasyCategoryDto.setName(fantasyCategory.getName());
        fantasyCategoryDto.setDescription(fantasyCategory.getDescription());

        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setCategories(new HashSet<>(List.of(fantasyCategoryDto)));

        BookSearchParameters parameters = new BookSearchParameters(
                new String[]{"nonexistent author"},
                new String[]{"nonexistent title"},
                new String[]{""},
                new String[]{""});

        Specification<Book> spec = (root, query, criteriaBuilder) -> null;

        when(specificationBuilder.build(parameters)).thenReturn(spec);
        when(bookRepository.findAll(spec)).thenReturn(List.of());

        // When
        List<BookDto> searchedBooks = bookService.search(parameters);

        // Then
        assertThat(searchedBooks).hasSize(0);
        verify(bookRepository, times(1)).findAll(spec);
        verifyNoMoreInteractions(bookRepository, specificationBuilder, bookMapper);
    }

    @Test
    @DisplayName("Get books by category id")
    public void getBookByCategoryId_ValidCategoryId_ReturnsListOfBooks() {
        // Given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("The Lord of the Rings");
        book.setAuthor("J. R. R. Tolkien");
        book.setIsbn("9780544003415");
        book.setPrice(BigDecimal.valueOf(15.5));
        book.setDescription("Awesome book");
        book.setCoverImage("The Lord of the Rings image");

        Category fantasyCategory = new Category();
        fantasyCategory.setId(1L);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");

        book.setCategories(new HashSet<>(List.of(fantasyCategory)));

        CategoryDto fantasyCategoryDto = new CategoryDto();
        fantasyCategoryDto.setId(fantasyCategory.getId());
        fantasyCategoryDto.setName(fantasyCategory.getName());
        fantasyCategoryDto.setDescription(fantasyCategory.getDescription());

        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());

        when(bookRepository.findAllByCategoryId(anyLong())).thenReturn(List.of(book));
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDto);

        // When
        List<BookDtoWithoutCategoryIds> booksByCategoryId = bookService.getBooksByCategoryId(1L);

        // Then
        assertThat(booksByCategoryId).hasSize(1);
        assertEquals(bookDto, booksByCategoryId.get(0));

        verify(bookRepository, times(1)).findAllByCategoryId(anyLong());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Get books by not valid category id")
    public void getBookByCategoryId_NotValidCategoryId_ReturnsEmptyList() {
        // Given
        when(bookRepository.findAllByCategoryId(anyLong())).thenReturn(List.of());

        // When
        List<BookDtoWithoutCategoryIds> booksByCategoryId = bookService.getBooksByCategoryId(1L);

        // Then
        assertThat(booksByCategoryId).hasSize(0);
        verify(bookRepository, times(1)).findAllByCategoryId(anyLong());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

}
