package store.onlinebookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import store.onlinebookstore.config.MapperConfig;
import store.onlinebookstore.dto.book.BookDto;
import store.onlinebookstore.dto.book.CreateBookRequestDto;
import store.onlinebookstore.model.Book;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Book toModel(CreateBookRequestDto requestDto);
}
