package store.onlinebookstore.dto.order;

import jakarta.validation.constraints.NotNull;

public record OrderShippingAddressRequest(@NotNull String shippingAddress) {
}
