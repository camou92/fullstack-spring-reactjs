package com.camoutech.service.impl;

import com.camoutech.mapper.WishlistMapper;
import com.camoutech.modal.Book;
import com.camoutech.modal.BookLoan;
import com.camoutech.modal.User;
import com.camoutech.modal.Wishlist;
import com.camoutech.payload.dto.BookLoanDTO;
import com.camoutech.payload.dto.WishlistDTO;
import com.camoutech.payload.response.PageResponse;
import com.camoutech.repository.BookRepository;
import com.camoutech.repository.WishlistRepository;
import com.camoutech.service.UserService;
import com.camoutech.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    private final UserService userService;
    private final BookRepository bookRepository;
    private final WishlistRepository wishlistRepository;
    private final WishlistMapper wishlistMapper;

    @Override
    public WishlistDTO addToWishlist(Long bookId, String notes) throws Exception {
        User user = userService.getCurrentUser();

        // Validate book exist
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new Exception("Book not found"));

        // Check if book is already in wishlist
        if (wishlistRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            throw new Exception("book is already in your wishlist");
        }

        // create wishlist
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setBook(book);
        wishlist.setNotes(notes);
        Wishlist saved = wishlistRepository.save(wishlist);
        return wishlistMapper.toDTO(saved);
    }

    @Override
    public void removeFromWishlist(Long bookId) throws Exception {

        User user = userService.getCurrentUser();

        Wishlist wishlist = wishlistRepository.findByUserIdAndBookId(
                user.getId(),
                bookId
        );

        if (wishlist == null) {
            throw new Exception("book is not in your wishlist");
        }
        wishlistRepository.delete(wishlist);

    }

    @Override
    public PageResponse<WishlistDTO> getMyWishlist(int page, int size) throws Exception {
        Long userId = userService.getCurrentUser().getId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("addedAt").descending());

        Page<Wishlist> wishlistPage = wishlistRepository.findByUserId(userId, pageable);

        return convertToPageResponse(wishlistPage);
    }

    private PageResponse<WishlistDTO> convertToPageResponse(Page<Wishlist> wishlistPage) {
        List<WishlistDTO> wishlistDTOS = wishlistPage.getContent()
                .stream()
                .map(wishlistMapper::toDTO)
                .toList();

        return new PageResponse<>(wishlistDTOS,
                wishlistPage.getNumber(),
                wishlistPage.getSize(),
                wishlistPage.getTotalElements(),
                wishlistPage.getTotalPages(),
                wishlistPage.isLast(),
                wishlistPage.isFirst(),
                wishlistPage.isEmpty());
    }
}
