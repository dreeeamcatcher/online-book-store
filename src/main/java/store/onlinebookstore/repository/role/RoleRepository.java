package store.onlinebookstore.repository.role;

import org.springframework.data.jpa.repository.JpaRepository;
import store.onlinebookstore.model.Role;
import store.onlinebookstore.model.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByName(RoleName name);
}
