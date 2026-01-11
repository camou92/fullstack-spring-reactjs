package com.camoutech.service;

import com.camoutech.payload.dto.BookReviewDTO;
import com.camoutech.payload.request.CreateReviewRequest;
import com.camoutech.payload.request.UpdateReviewRequest;
import com.camoutech.payload.response.PageResponse;

public interface BookReviewService {

    BookReviewDTO createReview(CreateReviewRequest request) throws Exception;

    BookReviewDTO updateReview(Long reviewId, UpdateReviewRequest request) throws Exception;

    void deleteReview(Long reviewId) throws Exception;

    PageResponse<BookReviewDTO> getReviewsByBookId(Long id, int page, int size) throws Exception;
}
