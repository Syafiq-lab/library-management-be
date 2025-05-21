package com.mb.transactionbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb.transactionbackend.dto.BookRegistrationRequest;
import com.mb.transactionbackend.dto.BookResponse;
import com.mb.transactionbackend.model.Book;
import com.mb.transactionbackend.service.BookService;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @MockBean BookService service;

    BookRegistrationRequest dto;
    Book book;

    @BeforeEach
    void init() {
        dto = new BookRegistrationRequest("ISBN-1", "Clean Code");
        book = new Book(); book.setBookId(10L); book.setIsbn(dto.isbn()); book.setTitle(dto.title());
    }

    @Test
    @DisplayName("POST /api/books – creation through service")
    void registerBook() throws Exception {
        when(service.registerBook(dto)).thenReturn(book);

        mvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.data.isbn", is(dto.isbn())));
    }

    @Test
    @DisplayName("GET /api/books – paged listing with constructor interception")
    void listPagedBooks() throws Exception {
        List<BookResponse> list = List.of(new BookResponse(book.getBookId(), book.getIsbn(), book.getTitle()));

        /* New in Mockito-5: mockConstruction for PageImpl allows us to intercept
           constructor params & swap return object if we wish. */
        try (MockedConstruction<PageImpl> mockedPage =
                     Mockito.mockConstruction(PageImpl.class, (mock, ctx) -> {
                         // Make the constructor act as usual but we can assert inputs
                         assertEquals(list, ctx.arguments().get(0)); // first ctor arg is content
                     })) {

            when(service.listBooks(any())).thenReturn(
                    new PageImpl<>(list, PageRequest.of(0, 10, Sort.by("title")), 1)
            );

            mvc.perform(get("/api/books"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data.content", hasSize(1)))
               .andExpect(jsonPath("$.data.totalElements", is(1)));
        }
    }
}