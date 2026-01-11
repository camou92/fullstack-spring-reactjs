package com.camoutech.service;

import com.camoutech.payload.dto.WishlistDTO;
import com.camoutech.payload.response.PageResponse;

public interface WishlistService {

    WishlistDTO addToWishlist(Long bookId, String notes) throws Exception;
    void removeFromWishlist(Long bookId) throws Exception;
    PageResponse<WishlistDTO> getMyWishlist(int page, int size) throws Exception;
}
