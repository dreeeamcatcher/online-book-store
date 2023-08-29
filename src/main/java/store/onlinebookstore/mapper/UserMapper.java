package store.onlinebookstore.mapper;

import org.mapstruct.Mapper;
import store.onlinebookstore.config.MapperConfig;
import store.onlinebookstore.dto.user.UserResponseDto;
import store.onlinebookstore.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponse(User savedUser);
}
