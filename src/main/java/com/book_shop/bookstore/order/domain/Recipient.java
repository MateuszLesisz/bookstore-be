package com.book_shop.bookstore.order.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Recipient {
    String name;
    String phone;
    String street;
    String city;
    String zipCode;
    String email;
}