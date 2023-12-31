package com.book_shop.bookstore.order.application.port;

import com.book_shop.bookstore.commons.Either;
import com.book_shop.bookstore.order.domain.Delivery;
import com.book_shop.bookstore.order.domain.OrderStatus;
import com.book_shop.bookstore.order.domain.Recipient;
import lombok.*;

import java.util.List;

public interface ManipulateOrderUseCase {

    PlaceOrderResponse placeOrder(PlaceOrderCommand command);
    void deleteOrderById(Long id);
    UpdateStatusResponse updateOrderStatus(UpdateStatusCommand command);


    @Builder
    @Value
    @AllArgsConstructor
    class PlaceOrderCommand {
        @Singular
        List<OrderItemCommand> orderItems;
        Recipient recipient;
        Delivery delivery;
    }

    @Value
    class OrderItemCommand {
        Long bookId;
        int quantity;
    }

    @Value
    class UpdateStatusCommand {
        Long orderId;
        OrderStatus status;
        String email;
    }

    class PlaceOrderResponse extends Either<String, Long> {
        public PlaceOrderResponse(boolean success, String left, Long right) {
            super(success, left, right);
        }

        public static PlaceOrderResponse success(Long orderId) {
            return new PlaceOrderResponse(true, null, orderId);
        }

        public static PlaceOrderResponse failure(String error) {
            return new PlaceOrderResponse(false, error, null);
        }
    }

    class UpdateStatusResponse extends Either<String, OrderStatus> {
        public UpdateStatusResponse(boolean success, String left, OrderStatus right) {
            super(success, left, right);
        }

        public static UpdateStatusResponse success(OrderStatus status) {
            return new UpdateStatusResponse(true, null, status);
        }

        public static UpdateStatusResponse failure(String status) {
            return new UpdateStatusResponse(false, status, null);
        }
    }
}
