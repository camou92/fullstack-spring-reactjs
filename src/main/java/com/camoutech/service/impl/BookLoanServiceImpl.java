package com.camoutech.service.impl;

import com.camoutech.domain.BookLoanStatus;
import com.camoutech.domain.BookLoanype;
import com.camoutech.exception.BookException;
import com.camoutech.mapper.BookLoanMapper;
import com.camoutech.modal.Book;
import com.camoutech.modal.BookLoan;
import com.camoutech.modal.User;
import com.camoutech.payload.dto.BookDTO;
import com.camoutech.payload.dto.BookLoanDTO;
import com.camoutech.payload.dto.SubscriptionDTO;
import com.camoutech.payload.request.BookLoanSearchRequest;
import com.camoutech.payload.request.CheckinRequest;
import com.camoutech.payload.request.CheckoutRequest;
import com.camoutech.payload.request.RenewalRequest;
import com.camoutech.payload.response.PageResponse;
import com.camoutech.repository.BookLoanRepository;
import com.camoutech.repository.BookRepository;
import com.camoutech.service.BookLoanService;
import com.camoutech.service.SubscriptionService;
import com.camoutech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookLoanServiceImpl implements BookLoanService {

    private final BookLoanRepository bookLoanRepository;
    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final BookRepository bookRepository;
    private final BookLoanMapper bookLoanMapper;

    @Override
    public BookLoanDTO checkoutBook(CheckoutRequest checkoutRequest) throws Exception {

        User user = userService.getCurrentUser();

        return checkoutBookForUser(user.getId(), checkoutRequest);
    }

    @Override
    public BookLoanDTO checkoutBookForUser(Long userId, CheckoutRequest checkoutRequest) throws Exception {
        // 1. validate user exist
        User user = userService.findById(userId);

        // 2. validate user has active subscription
        SubscriptionDTO subscription = subscriptionService
                .getUsersActiveSubscription(user.getId());

        // 3. validate book exists and is available
        Book book = bookRepository.findById(checkoutRequest.getBookId())
                .orElseThrow(()-> new BookException("book not found with id " +checkoutRequest.getBookId()));

        if (!book.getActive()) {
            throw new BookException("book is not active");
        }
        if (book.getAvailableCopies()<=0) {
            throw new BookException("book is not available");
        }

        // 4. check if user already has this book checkout
        if (bookLoanRepository.hasActiveCheckout(userId, book.getId())) {
            throw new BookException("book already has active checkout");
        }

        // 5. check user's active checkout limit
        long activeCheckouts = bookLoanRepository.countActiveBookLoansByUser(userId);
        int maxBooksAllowed = subscription.getMaxBooksAllowed();

        if (activeCheckouts >= maxBooksAllowed) {
            throw new Exception("you have reached your maximum number of books allowed");
        }

        // 6. Check for overdue books
        long overdueCount = bookLoanRepository.countOverdueBookLoansByUser(userId);
        if (overdueCount > 0) {
            throw new Exception("first return old overdue book!");
        }

        // 7. create book loan
        BookLoan bookLoan = BookLoan.builder()
                .user(user)
                .book(book)
                .type(BookLoanype.CHECKOUT)
                .status(BookLoanStatus.CHECKED_OUT)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(checkoutRequest.getCheckoutDays()))
                .renewalCount(0)
                .maxRenewals(2)
                .notes(checkoutRequest.getNotes())
                .isOverdue(false)
                .overdueDays(0)
                .build();

        // 9. update book available copies
        book.setAvailableCopies(book.getAvailableCopies()-1);
        bookRepository.save(book);

        // 10. save book loan
        BookLoan savedBookLoan = bookLoanRepository.save(bookLoan);

        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public BookLoanDTO checkinBook(CheckinRequest checkinRequest) throws Exception {

        // 1. validate book loan exist
        BookLoan bookLoan = bookLoanRepository.findById(checkinRequest.getBookLoanId())
                .orElseThrow(()-> new Exception("bookloan not found"));

        // 2. check if already returned
        if (!bookLoan.isActive()) {
            throw new BookException("book loan is not active");
        }

        // set return date
        bookLoan.setReturnDate(LocalDate.now());

        BookLoanStatus condition = checkinRequest.getCondition();
        if (condition == null) {
            condition = BookLoanStatus.RETURNED;
        }
        bookLoan.setStatus(condition);

        bookLoan.setOverdueDays(0);
        bookLoan.setIsOverdue(false);

        bookLoan.setNotes("book returned by user");

        // update book avaibility
        if (condition!=BookLoanStatus.LOST) {
            Book book = bookLoan.getBook();
            book.setAvailableCopies(book.getAvailableCopies()+1);
            bookRepository.save(book);
        }

        BookLoan savedBookLoan = bookLoanRepository.save(bookLoan);

        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public BookLoanDTO renewCheckout(RenewalRequest renewalRequest) throws Exception {

        // 1. validate book loan exist
        BookLoan bookLoan = bookLoanRepository.findById(renewalRequest.getBookLoanId())
                .orElseThrow(()-> new Exception("bookloan not found"));

        // 2. Check if can be renewed
        if (!bookLoan.canRenew()) {
            throw new BookException("book cannot be renewed");
        }

        // update due date
        bookLoan.setDueDate(bookLoan.getDueDate()
                .plusDays(renewalRequest.getExtensionDays()));

        bookLoan.setRenewalCount(bookLoan.getRenewalCount()+1);

        bookLoan.setNotes("book renewed by user");

        BookLoan savedBookLoan = bookLoanRepository.save(bookLoan);

        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public PageResponse<BookLoanDTO> getMyBookLoans(BookLoanStatus status, int page, int size) throws Exception {

        User currentUser = userService.getCurrentUser();
        Page<BookLoan> bookLoanPage;

        if (status != null) {
            // returned only active checkouts, sorted by due date
            Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
            bookLoanPage = bookLoanRepository.findByStatusAndUser(status, currentUser, pageable);
        } else {
            // return all history (both active and returned), sorted by creation date descending
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            bookLoanPage = bookLoanRepository.findByUserId(currentUser.getId(), pageable);
        }
        return convertToPageResponse(bookLoanPage);
    }

    @Override
    public PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest searchRequest) {
        // Build pageable with sorting, size, etc.
        Pageable pageable = createPageable(
                searchRequest.getPage(),
                searchRequest.getSize(),
                searchRequest.getSortBy(),
                searchRequest.getSortDirection()
        );

        Page<BookLoan> bookLoanPage;

        // Apply filtering logic dynamically
        if (Boolean.TRUE.equals(searchRequest.getOverdueOnly())) {
            // Fetch overdue loans
            bookLoanPage = bookLoanRepository.findOverdueBookLoans(LocalDate.now(), pageable);
        }

        else if (searchRequest.getUserId() != null) {
            // fetchs loans by specific user
            bookLoanPage = bookLoanRepository.findByUserId(searchRequest.getUserId(), pageable);
        }
        else if (searchRequest.getBookId() != null) {
            // fetch loans by specific book
            bookLoanPage = bookLoanRepository.findByBookId(searchRequest.getBookId(), pageable);
        }
        else if (searchRequest.getStatus() != null) {
            // Fetch loans by loan status
            bookLoanPage = bookLoanRepository.findByStatus(searchRequest.getStatus(), pageable);
        }
        else if (searchRequest.getStartDate() != null && searchRequest.getEndDate() != null) {
            // Fetch loans within date range
            bookLoanPage = bookLoanRepository.findBookLoansByDateRange(
                    searchRequest.getStartDate(),
                    searchRequest.getEndDate(),
                    pageable
            );
        }
        else {
            bookLoanPage = bookLoanRepository.findAll(pageable);
        }

        // Convert entities to DTOs and wrap in response object

        return convertToPageResponse(bookLoanPage);
    }

    @Override
    public int updateOverdueBookLoan() {
        Pageable pageable = PageRequest.of(0, 1000);
        Page<BookLoan> overduePage = bookLoanRepository
                .findOverdueBookLoans(LocalDate.now(), pageable);

        int updateCount = 0;
        for (BookLoan bookLoan : overduePage.getContent()) {
            if (bookLoan.getStatus() == BookLoanStatus.CHECKED_OUT) {
                bookLoan.setStatus(BookLoanStatus.OVERDUE);
                bookLoan.setIsOverdue(true);

                // Calculate overdue days
                int overdueDays = calculateOverdueDays(
                        bookLoan.getDueDate(),
                        LocalDate.now()
                );

                bookLoanRepository.save(bookLoan);
                updateCount++;
            }
        }
        return updateCount;
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        size = Math.min(size, 10);
        size = Math.max(size, 1);

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        return PageRequest.of(page,size,sort);
    }

    private PageResponse<BookLoanDTO> convertToPageResponse(Page<BookLoan> bookLoanPage) {
        List<BookLoanDTO> bookLoanDTOS = bookLoanPage.getContent()
                .stream()
                .map(bookLoanMapper::toDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(bookLoanDTOS,
                bookLoanPage.getNumber(),
                bookLoanPage.getSize(),
                bookLoanPage.getTotalElements(),
                bookLoanPage.getTotalPages(),
                bookLoanPage.isLast(),
                bookLoanPage.isFirst(),
                bookLoanPage.isEmpty());
    }

    public int calculateOverdueDays(LocalDate dueDate, LocalDate today) {
        return today.isAfter(dueDate)
                ? (int) ChronoUnit.DAYS.between(dueDate, today)
                : 0;
    }


    /*public int calculateOverdueDat(LocalDate dueDate, LocalDate today) {
        if (today.isBefore(dueDate) || today.isEqual(dueDate)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(dueDate, today);
    }*/
}
