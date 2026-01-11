package com.camoutech.controller;

import com.camoutech.domain.BookLoanStatus;
import com.camoutech.payload.dto.BookLoanDTO;
import com.camoutech.payload.request.BookLoanSearchRequest;
import com.camoutech.payload.request.CheckinRequest;
import com.camoutech.payload.request.CheckoutRequest;
import com.camoutech.payload.request.RenewalRequest;
import com.camoutech.payload.response.ApiResponse;
import com.camoutech.payload.response.PageResponse;
import com.camoutech.service.BookLoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-loans")
public class BookLoanController {

    private final BookLoanService bookLoanService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutBook(@Valid @RequestBody CheckoutRequest checkoutRequest) throws Exception {
        BookLoanDTO bookLoanDTO = bookLoanService.checkoutBook(checkoutRequest);
        return new ResponseEntity<>(bookLoanDTO, HttpStatus.CREATED);
    }

    @PostMapping("/checkout/user/{userId}")
    public ResponseEntity<?> checkoutBookForUser(
            @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest checkoutRequest) throws Exception {
        BookLoanDTO bookLoanDTO = bookLoanService.checkoutBookForUser(userId, checkoutRequest);
        return new ResponseEntity<>(bookLoanDTO, HttpStatus.CREATED);
    }

    @PostMapping("/checkin")
    public ResponseEntity<?> checkin(
            @Valid @RequestBody CheckinRequest checkinRequest) throws Exception {
        BookLoanDTO bookLoanDTO = bookLoanService.checkinBook(checkinRequest);
        return new ResponseEntity<>(bookLoanDTO, HttpStatus.OK);
    }

    @PostMapping("/renew")
    public ResponseEntity<?> renew(
            @Valid @RequestBody RenewalRequest renewalRequest) throws Exception {
        BookLoanDTO bookLoanDTO = bookLoanService.renewCheckout(renewalRequest);
        return new ResponseEntity<>(bookLoanDTO, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyBookLoans(
            @RequestParam(required = false)BookLoanStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) throws Exception {
        PageResponse<BookLoanDTO> bookLoans = bookLoanService.getMyBookLoans(status, page, size);
        return ResponseEntity.ok(bookLoans);
    }

    @PostMapping("/search")
    public ResponseEntity<?> getAllBookLoans(@RequestBody BookLoanSearchRequest searchRequest) {
        PageResponse<BookLoanDTO> bookLoans = bookLoanService.getBookLoans(searchRequest);
        return ResponseEntity.ok(bookLoans);
    }

    @PostMapping("/admin/update-overdue")
    public ResponseEntity<?> updatedOverdueBookLoans() {

        int updateCount = bookLoanService.updateOverdueBookLoan();
        return ResponseEntity.ok(
                new ApiResponse("overdue book loans are updated", true));
    }
}
