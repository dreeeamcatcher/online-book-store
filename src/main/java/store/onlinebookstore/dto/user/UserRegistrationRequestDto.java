package store.onlinebookstore.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import store.onlinebookstore.validator.FieldMatch;

@Data
@FieldMatch(field = "password",
        fieldMatch = "repeatPassword",
        message = "The password fields must match!")
public class UserRegistrationRequestDto {
    @NotNull
    @Length(min = 4, max = 50)
    private String email;
    @NotNull
    @Length(min = 6, max = 100)
    private String password;
    @NotNull
    private String repeatPassword;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String shippingAddress;
}

