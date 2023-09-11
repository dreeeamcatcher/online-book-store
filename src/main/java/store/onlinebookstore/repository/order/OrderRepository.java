package store.onlinebookstore.repository.order;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import store.onlinebookstore.model.Order;
import store.onlinebookstore.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "FROM Order o LEFT JOIN FETCH o.orderItems oi WHERE o.user.id = :userId",
            countQuery = "SELECT COUNT(*) FROM Order o where o.user.id = :userId")
    List<Order> findAllByUserId(Long userId, Pageable pageable);

    @Query("FROM Order o "
            + "LEFT JOIN FETCH o.orderItems oi "
            + "WHERE o.id = :id")
    Optional<Order> findOrderById(Long id);

    Optional<Order> findOrderByUserAndId(User user, Long id);

}
