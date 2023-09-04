package store.onlinebookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import store.onlinebookstore.config.MapperConfig;
import store.onlinebookstore.dto.cartitem.CartItemResponseDto;
import store.onlinebookstore.model.Book;
import store.onlinebookstore.model.CartItem;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(target = "bookId", source = "book", qualifiedByName = "bookToBookId")
    @Mapping(target = "bookTitle", source = "book", qualifiedByName = "bookToBookTitle")
    CartItemResponseDto toDto(CartItem item);

    @Named("bookToBookId")
    static Long toBookId(Book book) {
        return book.getId();
    }

    @Named("bookToBookTitle")
    static String toBookTitle(Book book) {
        return book.getTitle();
    }
}
