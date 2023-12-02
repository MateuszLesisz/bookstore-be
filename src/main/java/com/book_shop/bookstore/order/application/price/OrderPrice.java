package com.book_shop.bookstore.order.application.price;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class OrderPrice {
    BigDecimal itemsPrice;
    BigDecimal deliveryPrice;
    BigDecimal discounts;

    public BigDecimal finalPrice() {
        return itemsPrice.add(deliveryPrice).add(discounts);
    }
}