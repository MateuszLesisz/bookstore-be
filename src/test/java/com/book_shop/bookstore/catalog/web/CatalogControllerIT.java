package com.book_shop.bookstore.catalog.web;

import com.book_shop.bookstore.catalog.application.port.CatalogUseCase;
import com.book_shop.bookstore.catalog.application.port.CatalogUseCase.CreateBookCommand;
import com.book_shop.bookstore.catalog.db.AuthorJpaRepository;
import com.book_shop.bookstore.catalog.domain.Author;
import com.book_shop.bookstore.catalog.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
class CatalogControllerIT {

    @Autowired
    AuthorJpaRepository authorJpaRepository;
    @Autowired
    CatalogUseCase catalogUseCase;
    @Autowired
    CatalogController controller;

    @Test
    public void getAllBooks() {
        //given
        Author bloch = authorJpaRepository.save(new Author("Joshua Bloch"));
        Author goetz = authorJpaRepository.save(new Author("Brian Goetz"));
        catalogUseCase.addBook(new CreateBookCommand(
                "Effective Java",
                Set.of(bloch.getId()),
                2005,
                new BigDecimal("99.90"),
                50L
        ));
        catalogUseCase.addBook(new CreateBookCommand(
                "Java Concurrency in Practice",
                Set.of(goetz.getId()),
                2006,
                new BigDecimal("129.90"),
                50L
        ));

        //when
        List<Book> all = controller.getAll(Optional.empty(), Optional.empty());

        //then
        assertEquals(2, all.size());
    }
}