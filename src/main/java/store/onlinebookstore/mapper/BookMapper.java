package store.onlinebookstore.mapper;

import org.mapstruct.Mapper;
import store.onlinebookstore.config.MapperConfig;
import store.onlinebookstore.dto.BookDto;
import store.onlinebookstore.dto.CreateBookRequestDto;
import store.onlinebookstore.model.Book;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);
}
