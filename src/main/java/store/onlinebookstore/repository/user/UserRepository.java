package store.onlinebookstore.repository.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import store.onlinebookstore.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    @Query("from User u left join u.roles")
    List<String> findRolesForUser(User user);
}
