package com.camoutech.controller;

import com.camoutech.exception.BookException;
import com.camoutech.payload.dto.BookDTO;
import com.camoutech.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 02/01/2026
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/book")
public class AdminBookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) throws BookException {
        BookDTO createdBook = bookService.createBook(bookDTO);
        return ResponseEntity.ok(createdBook);
    }
}
