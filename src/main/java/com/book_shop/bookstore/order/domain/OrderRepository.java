package com.book_shop.bookstore.order.domain;

import java.util.List;

public interface OrderRepository {
    Order save(Order order);
    List<Order> findAll();
}