package store.onlinebookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import store.onlinebookstore.dto.book.BookDto;
import store.onlinebookstore.dto.book.CreateBookRequestDto;
import store.onlinebookstore.dto.category.CategoryDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/add-categories.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/add-books.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/associate-categories-to-books.sql"));
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/remove-categories-from-books.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/remove-categories.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/remove-books.sql"));
        }
    }

    @WithMockUser
    @Test
    @DisplayName("Get all books")
    void getAll_ValidPageable_Success() throws Exception {
        // When
        MvcResult result = mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        List<BookDto> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {});

        assertThat(response).hasSize(4);
    }

    @WithMockUser
    @Test
    @DisplayName("Get book by id")
    void getBookById_ValidId_Success() throws Exception {
        // Given
        Long id = 1L;

        CategoryDto sciFiDto = new CategoryDto()
                .setId(2L)
                .setName("Science Fiction")
                .setDescription("Science Fiction");

        BookDto expected = new BookDto()
                .setId(id)
                .setTitle("1984")
                .setAuthor("George Orwell")
                .setIsbn("some isbn1")
                .setPrice(BigDecimal.valueOf(19))
                .setDescription("Description")
                .setCoverImage("Cover image")
                .setCategories(new HashSet<>(List.of(sciFiDto)));

        // When
        MvcResult result = mockMvc.perform(get("/api/books/" + id))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @WithMockUser
    @Test
    @DisplayName("Get book by non existing id")
    void getBookById_NotValidId_NotFound() throws Exception {
        // Given
        Long nonExistingId = 100L;

        // When
        mockMvc.perform(get("/api/books/" + nonExistingId))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/books/remove-hound-of-baskervilles-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Create a book")
    void createBook_ValidRequestDto_Success() throws Exception {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("The Hound of the Baskervilles")
                .setAuthor("Sir Arthur Conan Doyle")
                .setIsbn("some new isbn")
                .setPrice(BigDecimal.valueOf(15))
                .setDescription("Description")
                .setCoverImage("Cover image")
                .setCategoryIds(List.of(3L));

        CategoryDto detectiveDto = new CategoryDto()
                .setId(3L)
                .setName("Detective")
                .setDescription("Detective");

        BookDto expected = new BookDto()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage())
                .setCategories(new HashSet<>(List.of(detectiveDto)));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                post("/api/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a book with not valid request")
    void createBook_NotValidRequestDto_BadRequest() throws Exception {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("The Hound of the Baskervilles")
                .setAuthor("Sir Arthur Conan Doyle");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        post("/api/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(scripts = "classpath:database/books/add-harry-potter1-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Delete a book ")
    void delete_ValidId_SuccessNoContent() throws Exception {
        // Given
        Long id = 11L;

        MvcResult resultBeforeDelete = mockMvc.perform(get("/api/books")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> booksBeforeDelete = objectMapper.readValue(
                resultBeforeDelete.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {});

        // When
        mockMvc.perform(delete("/api/books/" + id)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        // Then
        MvcResult resultAfterDelete = mockMvc.perform(get("/api/books")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> booksAfterDelete = objectMapper.readValue(
                resultAfterDelete.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {});

        assertThat(booksBeforeDelete).hasSize(5);
        assertThat(booksAfterDelete).hasSize(4);
    }

    @Test
    @DisplayName("Delete a book with not valid id")
    void delete_NotValidId_NoContent() throws Exception {
        // Given
        Long nonExistingId = 100L;

        // When
        MvcResult resultBeforeDelete = mockMvc.perform(get("/api/books")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> booksBeforeDelete = objectMapper.readValue(
                resultBeforeDelete.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {});

        mockMvc.perform(delete("/api/books/" + nonExistingId)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        MvcResult resultAfterDelete = mockMvc.perform(get("/api/books")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> booksAfterDelete = objectMapper.readValue(
                resultAfterDelete.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {});

        // Then
        assertThat(booksBeforeDelete).hasSize(4);
        assertThat(booksAfterDelete).hasSize(4);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/books/add-harry-potter2-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-harry-potter2-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update a book")
    void updateBookById_ValidIdValidRequestDto_Success() throws Exception {
        // Given
        Long id = 12L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Updated Title")
                .setAuthor("Updated Author")
                .setIsbn("updated isbn")
                .setPrice(BigDecimal.valueOf(5))
                .setDescription("Updated description")
                .setCoverImage("Updated cover image")
                .setCategoryIds(List.of(2L));

        CategoryDto sciFiDto = new CategoryDto()
                .setId(1L)
                .setName("Science Fiction")
                .setDescription("Science Fiction");

        BookDto expected = new BookDto()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage())
                .setCategories(new HashSet<>(List.of(sciFiDto)));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(put("/api/books/" + id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update a book with not valid id")
    void updateBookById_NotValidId_NotFound() throws Exception {
        // Given
        Long nonExistingId = 100L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Updated Title")
                .setAuthor("Updated Author")
                .setIsbn("updated isbn")
                .setPrice(BigDecimal.valueOf(5))
                .setDescription("Updated description")
                .setCoverImage("Updated cover image")
                .setCategoryIds(List.of(2L));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(put("/api/books/" + nonExistingId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update a book with not valid request")
    void updateBookById_NotValidRequestDto_BadRequest() throws Exception {
        // Given
        Long id = 1L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Updated Title")
                .setAuthor("Updated Author");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(put("/api/books/" + id)
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test

    @DisplayName("Search books by parameters")
    void searchBooks_ValidParameters_ReturnsListOfBooks() throws Exception {
        // When
        MvcResult resultTitleQuery = mockMvc.perform(get("/api/books/search")
                        .param("title", "game")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult resultAuthorQuery = mockMvc.perform(get("/api/books/search")
                        .param("authors", "Sir Arthur Conan Doyle")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult resultLowerPriceQuery = mockMvc.perform(get("/api/books/search")
                        .param("lowerPrice", "20")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        List<BookDto> responseTitleQuery = objectMapper.readValue(
                resultTitleQuery.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {});

        List<BookDto> responseAuthorQuery = objectMapper.readValue(
                resultAuthorQuery.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {});

        List<BookDto> responseLowerPriceQuery = objectMapper.readValue(
                resultLowerPriceQuery.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {});

        assertFalse(responseTitleQuery.isEmpty());
        assertFalse(responseAuthorQuery.isEmpty());
        assertFalse(responseLowerPriceQuery.isEmpty());

        assertThat(responseTitleQuery).hasSize(1);
        assertThat(responseAuthorQuery).hasSize(1);
        assertThat(responseLowerPriceQuery).hasSize(3);
    }

    @WithMockUser
    @Test
    @DisplayName("Search books with no parameters")
    void searchBooks_NoParameters_ReturnsListOfAllBooks() throws Exception {
        // When
        MvcResult result = mockMvc.perform(get("/api/books/search"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        List<BookDto> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {
                });

        assertFalse(response.isEmpty());
        assertThat(response).hasSize(4);
    }
}
