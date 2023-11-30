package com.book_shop.bookstore.order.application;

import com.book_shop.bookstore.catalog.db.BookJpaRepository;
import com.book_shop.bookstore.catalog.domain.Book;
import com.book_shop.bookstore.order.application.port.ManipulateOrderUseCase;
import com.book_shop.bookstore.order.db.OrderJpaRepository;
import com.book_shop.bookstore.order.db.RecipientJpaRepository;
import com.book_shop.bookstore.order.domain.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
@AllArgsConstructor
@Transactional
public class ManipulateOrderService implements ManipulateOrderUseCase {
    private final OrderJpaRepository orderRepository;
    private final BookJpaRepository bookRepository;
    private final RecipientJpaRepository recipientJpaRepository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Set<OrderItem> items = command.getOrderItems().stream()
                .map(this::toOrderItem)
                .collect(toSet());
        Order order = Order.builder()
                .recipient(getOrCreateRecipient(command.getRecipient()))
                .items(items)
                .build();
        Order save = orderRepository.save(order);
        bookRepository.saveAll(reduceBooks(items));
        return PlaceOrderResponse.success(save.getId());
    }

    private OrderItem toOrderItem(OrderItemCommand command) {
        Book book = bookRepository.findById(command.getBookId()).orElseThrow(() -> new IllegalArgumentException("Book does not exist."));
        if (book.getAvailable() >= command.getQuantity()) {
            return new OrderItem(book, command.getQuantity());
        }
        throw new IllegalArgumentException("Too many copies of book " + book.getId() + " requested " + command.getQuantity() + " of " + book.getAvailable() + " available.");
    }

    private Set<Book> reduceBooks(Set<OrderItem> items) {
        return items.stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() - item.getQuantity());
                    return book;
                })
                .collect(toSet());
    }

    private Recipient getOrCreateRecipient(Recipient recipient) {
        return recipientJpaRepository.findByEmailIgnoreCase(recipient.getEmail())
                .orElse(recipient);
    }

    @Override
    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public void updateOrderStatus(Long id, OrderStatus status) {
        orderRepository.findById(id)
                .ifPresent(order -> {
                    UpdateStatusResult result = order.updateStatus(status);
                    if (result.isRevoked()) {
                        bookRepository.saveAll(revokeBooks(order.getItems()));
                    }
                    orderRepository.save(order);
                });
    }

    private Set<Book> revokeBooks(Set<OrderItem> items) {
        return items.stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() + item.getQuantity());
                    return book;
                })
                .collect(toSet());
    }
}