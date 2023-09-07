package store.onlinebookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import store.onlinebookstore.model.Status;

public record OrderStatusRequest(@NotNull Status status) {
}
