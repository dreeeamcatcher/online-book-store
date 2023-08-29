package store.onlinebookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotEmpty
        @Email
        @Length(min = 4, max = 50)
        String email,
        @NotEmpty
        @Length(min = 6, max = 100)
        String password
) {
}
