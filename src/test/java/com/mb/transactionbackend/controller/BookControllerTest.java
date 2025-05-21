package com.mb.transactionbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb.transactionbackend.dto.BookRegistrationRequest;
import com.mb.transactionbackend.dto.BookResponse;
import com.mb.transactionbackend.model.Book;
import com.mb.transactionbackend.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)   // disable Spring Security filters
@ActiveProfiles("test")
class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookService service;

    private BookRegistrationRequest dto;
    private Book book;

    @BeforeEach
    void init() {
        dto = new BookRegistrationRequest(
                "BOOK-10",
                "ISBN-1",
                "Clean Code",
                "Robert C. Martin"
        );

        book = new Book();
        book.setBookId(dto.bookId());
        book.setIsbn(dto.isbn());
        book.setTitle(dto.title());
        book.setAuthor(dto.author());
    }

    @Test
    @DisplayName("POST /api/books – creation through service returns 201")
    void registerBook() throws Exception {
        when(service.registerBook(any(BookRegistrationRequest.class)))
                .thenReturn(book);

        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())                   // expect 201 Created
                .andExpect(jsonPath("$.data.isbn").value(dto.isbn()));
    }

    @Test
    @DisplayName("GET /api/books – simple list of books")
    void listBooks() throws Exception {
        List<BookResponse> list = List.of(new BookResponse(
                book.getBookId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                false
        ));

        when(service.listBooks())
                .thenReturn(list);

        mvc.perform(get("/api/books/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].bookId").value("BOOK-10"));
    }

    @Test
    @DisplayName("GET /api/books – paged listing with default params")
    void listPagedBooks() throws Exception {
        List<BookResponse> list = List.of(new BookResponse(
                book.getBookId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                false
        ));
        PageRequest pageReq = PageRequest.of(0, 10, Sort.by("title").ascending());
        Page<BookResponse> page = new PageImpl<>(list, pageReq, list.size());

        when(service.listBooks(any(Pageable.class)))
                .thenReturn(page);

        mvc.perform(get("/api/books/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
}
