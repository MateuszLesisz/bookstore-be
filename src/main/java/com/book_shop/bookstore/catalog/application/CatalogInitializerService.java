package com.book_shop.bookstore.catalog.application;

import com.book_shop.bookstore.catalog.application.port.CatalogInitializerUseCase;
import com.book_shop.bookstore.catalog.application.port.CatalogUseCase;
import com.book_shop.bookstore.catalog.application.port.CatalogUseCase.CreateBookCommand;
import com.book_shop.bookstore.catalog.db.AuthorJpaRepository;
import com.book_shop.bookstore.catalog.domain.Book;
import com.book_shop.bookstore.order.application.port.ManipulateOrderUseCase;
import com.book_shop.bookstore.order.application.port.QueryOrderUseCase;
import com.book_shop.bookstore.order.domain.Recipient;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class CatalogInitializerService implements CatalogInitializerUseCase {

    private final CatalogUseCase catalogUseCase;
    private final ManipulateOrderUseCase placeOrder;
    private final QueryOrderUseCase queryOrder;
    private final AuthorJpaRepository authorRepository;

    @Override
    @Transactional
    public void initialize() {
        initData();
        placeOrder();
    }

    private void initData() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("books.csv").getInputStream()))) {
            CsvToBean<CsvBook> build = new CsvToBeanBuilder<CsvBook>(reader)
                    .withType(CsvBook.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            build.stream().forEach(this::initBook);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse CSV file", e);
        }
    }

    private void initBook(CsvBook csvBook) {
        CreateBookCommand command = new CreateBookCommand(csvBook.title, Set.of(), csvBook.year, csvBook.amount, 50L);
        catalogUseCase.addBook(command);
    }

    private void placeOrder() {
        Book effectiveJava = catalogUseCase.findOneByTitle("Effective Java").orElseThrow(() -> new IllegalArgumentException("Cannot find a book"));
        Book puzzlers = catalogUseCase.findOneByTitle("Java Puzzlers").orElseThrow(() -> new IllegalArgumentException("Cannot find a book"));

        Recipient recipient = Recipient.builder()
                .name("Jan Kowalski")
                .phone("123-456-789")
                .street("Armii Krajowej 31")
                .city("Kraków")
                .zipCode("30-150")
                .email("jan@example.org")
                .build();

        ManipulateOrderUseCase.PlaceOrderCommand command = ManipulateOrderUseCase.PlaceOrderCommand
                .builder()
                .recipient(recipient)
                .orderItem(new ManipulateOrderUseCase.OrderItemCommand(effectiveJava.getId(), 12))
                .orderItem(new ManipulateOrderUseCase.OrderItemCommand(puzzlers.getId(), 12))
                .build();

        ManipulateOrderUseCase.PlaceOrderResponse response = placeOrder.placeOrder(command);
        String result = response.handle(
                orderId -> "Created ORDER with id: " + orderId,
                error -> "Failed to created order: " + error
        );
        log.info(result);

        // list all orders
        queryOrder.findAll()
                .forEach(order -> log.info("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice() + " DETAILS: " + order));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class CsvBook {
        @CsvBindByName
        private String title;
        @CsvBindByName
        private String authors;
        @CsvBindByName
        private Integer year;
        @CsvBindByName
        private BigDecimal amount;
        @CsvBindByName
        private String thumbnail;
    }
}