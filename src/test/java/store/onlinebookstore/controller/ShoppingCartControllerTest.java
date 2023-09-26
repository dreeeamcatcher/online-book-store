package store.onlinebookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import store.onlinebookstore.dto.cartitem.CartItemResponseDto;
import store.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import store.onlinebookstore.dto.cartitem.ItemQuantity;
import store.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import store.onlinebookstore.security.JwtUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;

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
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/users/create-user.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/roles/create-roles.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/roles/add-roles-for-users.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/shoppingcarts/create-shopping-cart.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/cartitems/add-1984-to-cart-items.sql"));
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
                    new ClassPathResource("database/cartitems/delete-1984-from-cart-items.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/shoppingcarts/delete-shopping-cart.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/roles/remove-roles-from-users.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/roles/delete-roles.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/users/delete-user.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/remove-categories-from-books.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/remove-categories.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/remove-books.sql"));
        }
    }

    @Test
    @DisplayName("Get shopping cart for user")
    void getShoppingCartForUser_AuthenticatedUser_Success() throws Exception {
        // Given
        CartItemResponseDto item1 = new CartItemResponseDto()
                .setId(1L)
                .setBookId(1L)
                .setBookTitle("1984")
                .setQuantity(2);

        ShoppingCartDto expected = new ShoppingCartDto()
                .setId(1L)
                .setUserId(1L)
                .setCartItems(new HashSet<>(List.of(item1)));

        String token = jwtUtil.generateToken("user@mail.com");

        // When
        MvcResult result = mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        assertNotNull(actual);
        assertThat(actual.getCartItems()).hasSize(1);
        assertEquals(expected, actual);
    }

    @Test
    @Sql(scripts = "classpath:database/cartitems/delete-lord-of-the-rings-from-cart-items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Add item to shopping cart")
    void addToShoppingCart_ValidRequestDto_Success() throws Exception {
        // Given
        String token = jwtUtil.generateToken("user@mail.com");

        Long bookId = 2L;
        CreateCartItemRequestDto itemRequestDto = new CreateCartItemRequestDto()
                .setBookId(bookId)
                .setQuantity(5);

        String jsonRequest = objectMapper.writeValueAsString(itemRequestDto);

        MvcResult resultBeforeAdd = mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto cartBeforeAdd = objectMapper.readValue(
                resultBeforeAdd.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        // When
        mockMvc.perform(post("/api/cart")
                        .header("Authorization", "Bearer " + token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Then
        MvcResult resultAfterAdd = mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto cartAfterAdd = objectMapper.readValue(
                resultAfterAdd.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        List<CartItemResponseDto> newItem = cartAfterAdd.getCartItems().stream()
                .filter(i -> i.getBookId() == bookId)
                .toList();

        assertFalse(newItem.isEmpty());
        assertThat(cartBeforeAdd.getCartItems()).hasSize(1);
        assertThat(cartAfterAdd.getCartItems()).hasSize(2);
    }

    @Test
    @Sql(scripts = "classpath:database/cartitems/add-game-of-thrones-to-cart-items.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cartitems/delete-game-of-thrones-from-cart-items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update item quantity")
    void updateItemQuantity_ValidItemId_Success() throws Exception {
        //Given
        String token = jwtUtil.generateToken("user@mail.com");
        Long id = 3L;
        int expectedInitialQuantity = 1;
        int expectedNewQuantity = 10;

        ItemQuantity quantity = new ItemQuantity(expectedNewQuantity);

        String jsonRequest = objectMapper.writeValueAsString(quantity);

        MvcResult resultBeforeUpdate = mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto cartBeforeUpdate = objectMapper.readValue(
                resultBeforeUpdate.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        List<CartItemResponseDto> itemBeforeUpdate = cartBeforeUpdate.getCartItems().stream()
                .filter(i -> i.getId() == id)
                .toList();
        int quantityBeforeUpdate = itemBeforeUpdate.get(0).getQuantity();

        // When
        MvcResult result = mockMvc.perform(put("/api/cart/cart-items/" + id)
                        .header("Authorization", "Bearer " + token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        ShoppingCartDto cartAfterUpdate = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        List<CartItemResponseDto> itemAfterUpdate = cartAfterUpdate.getCartItems().stream()
                .filter(i -> i.getId() == id)
                .toList();
        int quantityAfterUpdate = itemAfterUpdate.get(0).getQuantity();

        assertEquals(expectedInitialQuantity, quantityBeforeUpdate);
        assertEquals(expectedNewQuantity, quantityAfterUpdate);
    }

    @Test
    @DisplayName("Update item quantity with not valid item id")
    void updateItemQuantity_NotValidItemId_NotFound() throws Exception {
        //Given
        String token = jwtUtil.generateToken("user@mail.com");
        Long nonExistingid = 100L;
        ItemQuantity quantity = new ItemQuantity(10);

        String jsonRequest = objectMapper.writeValueAsString(quantity);

        // When
        mockMvc.perform(put("/api/cart/cart-items/" + nonExistingid)
                        .header("Authorization", "Bearer " + token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql(scripts = "classpath:database/cartitems/add-sherlock-holmes-to-cart-items.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cartitems/delete-sherlock-holmes-from-cart-items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Delete item from shopping cart")
    void deleteItem_ValidId_Success() throws Exception {
        // Given
        String token = jwtUtil.generateToken("user@mail.com");
        Long id = 2L;

        MvcResult resultBeforeDelete = mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto cartBeforeDelete = objectMapper.readValue(
                resultBeforeDelete.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        List<CartItemResponseDto> itemBeforeDelete = cartBeforeDelete.getCartItems().stream()
                .filter(i -> i.getId() == id)
                .toList();

        // When
        mockMvc.perform(delete("/api/cart/cart-items/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Then
        MvcResult resultAfterDelete = mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto cartAfterDelete = objectMapper.readValue(
                resultAfterDelete.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        List<CartItemResponseDto> itemAfterDelete = cartAfterDelete.getCartItems().stream()
                .filter(i -> i.getId() == id)
                .toList();

        assertFalse(itemBeforeDelete.isEmpty());
        assertTrue(itemAfterDelete.isEmpty());
        assertEquals(2, cartBeforeDelete.getCartItems().size());
        assertEquals(1, cartAfterDelete.getCartItems().size());
    }

    @Test
    @DisplayName("Delete item from shopping cart with not valid item id")
    void deleteItem_NotValidId_NoContent() throws Exception {
        // Given
        String token = jwtUtil.generateToken("user@mail.com");
        Long nonExistingid = 100L;

        // When
        mockMvc.perform(delete("/api/cart/cart-items/" + nonExistingid)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
