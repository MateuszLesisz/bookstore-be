package com.book_shop.bookstore.catalog.web;

import com.book_shop.bookstore.catalog.application.port.CatalogUseCase;
import com.book_shop.bookstore.catalog.domain.Book;
import com.book_shop.bookstore.catalog.domain.RestBook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogControllerApiTest {

    @LocalServerPort
    private int port;
    @MockBean
    CatalogUseCase catalogUseCase;
    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    public void geAllBooks() {
        //given
        Book effectiveJava = new Book("Effective Java", 2005, new BigDecimal("99.90"), 10L);
        Book concurrency = new Book("Java Concurrency", 2006, new BigDecimal("129.00"), 50L);
        when(catalogUseCase.findAll()).thenReturn(List.of(effectiveJava, concurrency));
        ParameterizedTypeReference<List<RestBook>> type = new ParameterizedTypeReference<>() {
        };

        //when
        String url = "http://localhost:" + port + "/catalog";
        RequestEntity<Void> request = RequestEntity.get(URI.create(url)).build();
        ResponseEntity<List<RestBook>> response = testRestTemplate.exchange(request, type);

        //then
        assertEquals(2, response.getBody().size());
    }
}