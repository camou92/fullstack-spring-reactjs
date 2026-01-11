package com.camoutech.service.impl;

import com.camoutech.domain.BookLoanStatus;
import com.camoutech.mapper.BookReviewMapper;
import com.camoutech.modal.Book;
import com.camoutech.modal.BookLoan;
import com.camoutech.modal.BookReview;
import com.camoutech.modal.User;
import com.camoutech.payload.dto.BookReviewDTO;
import com.camoutech.payload.dto.WishlistDTO;
import com.camoutech.payload.request.CreateReviewRequest;
import com.camoutech.payload.request.UpdateReviewRequest;
import com.camoutech.payload.response.PageResponse;
import com.camoutech.repository.BookLoanRepository;
import com.camoutech.repository.BookRepository;
import com.camoutech.repository.BookReviewRepository;
import com.camoutech.service.BookReviewService;
import com.camoutech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookReviewServiceImpl implements BookReviewService {

    private final UserService userService;
    private final BookRepository bookRepository;
    private final BookReviewRepository bookReviewRepository;
    private final BookReviewMapper bookReviewMapper;
    private final BookLoanRepository bookLoanRepository;

    @Override
    public BookReviewDTO createReview(CreateReviewRequest request) throws Exception {

        // 1. fetch the logged user
        User user = userService.getCurrentUser();

        // validate book exist
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(()-> new Exception("book not found !"));

        // check if user has already reviewed the book
        if (bookReviewRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
            throw new Exception("you have already reviewed this book!");
        }

        // check if user has read the book
        boolean hasReadBook = hasUserReadBook(user.getId(), book.getId());
        if (!hasReadBook) {
            throw new Exception("you have not read this book!");
        }

        // create review
        BookReview bookReview = new BookReview();
        bookReview.setUser(user);
        bookReview.setBook(book);
        bookReview.setRating(request.getRating());
        bookReview.setReviewText(request.getReviewText());
        bookReview.setTitle(request.getTitle());
        BookReview savedBookReview = bookReviewRepository.save(bookReview);
        return bookReviewMapper.toDTO(savedBookReview);
    }


    @Override
    public BookReviewDTO updateReview(Long reviewId, UpdateReviewRequest request) throws Exception {

        User user = userService.getCurrentUser();

        // find the review
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(()-> new Exception("review not found!"));

        // check if logged user is the owner of the review
        if (!bookReview.getUser().getId().equals(user.getId())) {
            throw new Exception("you have not reviewed this book!");
        }

        // update review
        bookReview.setReviewText(request.getReviewText());
        bookReview.setTitle(request.getTitle());
        bookReview.setRating(request.getRating());

        BookReview savedBookReview = bookReviewRepository.save(bookReview);

        return bookReviewMapper.toDTO(savedBookReview);
    }

    @Override
    public void deleteReview(Long reviewId) throws Exception {

        User currentUser = userService.getCurrentUser();

        // Find the review
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new Exception("Review not found with id: " + reviewId));

        // Check if current user is the owner of the review
        if (!bookReview.getUser().getId().equals(currentUser.getId())) {
            throw new Exception("You can only delete your own reviews");
        }

        bookReviewRepository.delete(bookReview);
    }

    @Override
    public PageResponse<BookReviewDTO> getReviewsByBookId(Long id, int page, int size) throws Exception {

        Book book = bookRepository.findById(id).orElseThrow(
                ()-> new Exception("book not found by id!")
        );
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").descending());
        Page<BookReview> reviewPage = bookReviewRepository.findByBook(book,pageable);
        return convertToPageResponse(reviewPage);
    }

    private PageResponse<BookReviewDTO> convertToPageResponse(Page<BookReview> reviewPage) {
        List<BookReviewDTO> reviewDTOS = reviewPage.getContent()
                .stream()
                .map(bookReviewMapper::toDTO)
                .toList();

        return new PageResponse<>(reviewDTOS,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast(),
                reviewPage.isFirst(),
                reviewPage.isEmpty());
    }

    private boolean hasUserReadBook(Long userId, Long bookId) {
        List<BookLoan> bookLoans = bookLoanRepository.findByBookId(bookId);

        return bookLoans.stream()
                .anyMatch(loan->loan.getUser().getId().equals(userId) &&
                        loan.getStatus() == BookLoanStatus.RETURNED);
    }
}
