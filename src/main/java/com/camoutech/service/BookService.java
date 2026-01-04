package com.camoutech.service;

import com.camoutech.exception.BookException;
import com.camoutech.payload.dto.BookDTO;
import com.camoutech.payload.request.BookSearchRequest;
import com.camoutech.payload.response.PageResponse;

import java.util.List;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 29/12/2025
 */
public interface BookService {

    BookDTO createBook(BookDTO bookDTO) throws BookException;
    List<BookDTO> createBooksBulk(List<BookDTO> bookDTOS) throws BookException;
    BookDTO getBookById(Long bookId) throws BookException;
    BookDTO getBookByISBN(String isbn) throws BookException;
    BookDTO updateBook(Long bookId, BookDTO bookDTO) throws BookException;
    void deleteBook(Long bookId) throws BookException;
    void hardDeleteBook(Long bookId) throws BookException;

    PageResponse<BookDTO> searchBooksWithFilters(BookSearchRequest searchRequest);

    long getTotalActiveBooks();

    long getTotalAvailableBooks();
}
