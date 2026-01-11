package com.camoutech.service;

import com.camoutech.domain.BookLoanStatus;
import com.camoutech.payload.dto.BookLoanDTO;
import com.camoutech.payload.request.BookLoanSearchRequest;
import com.camoutech.payload.request.CheckinRequest;
import com.camoutech.payload.request.CheckoutRequest;
import com.camoutech.payload.request.RenewalRequest;
import com.camoutech.payload.response.PageResponse;

public interface BookLoanService {

    BookLoanDTO checkoutBook(CheckoutRequest checkoutRequest) throws Exception;

    BookLoanDTO checkoutBookForUser(Long userId, CheckoutRequest checkoutRequest) throws Exception;

    BookLoanDTO checkinBook(CheckinRequest checkinRequest) throws Exception;

    BookLoanDTO renewCheckout(RenewalRequest renewalRequest) throws Exception;

    PageResponse<BookLoanDTO> getMyBookLoans(BookLoanStatus status, int page, int size) throws Exception;

    PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest request);

    int updateOverdueBookLoan();
}
