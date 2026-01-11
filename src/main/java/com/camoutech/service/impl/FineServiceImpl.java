package com.camoutech.service.impl;

import com.camoutech.domain.FineStatus;
import com.camoutech.domain.FineType;
import com.camoutech.domain.PaymentGateway;
import com.camoutech.domain.PaymentType;
import com.camoutech.mapper.FineMapper;
import com.camoutech.modal.Book;
import com.camoutech.modal.BookLoan;
import com.camoutech.modal.Fine;
import com.camoutech.modal.User;
import com.camoutech.payload.dto.BookDTO;
import com.camoutech.payload.dto.FineDTO;
import com.camoutech.payload.request.CreateFineRequest;
import com.camoutech.payload.request.PaymentInitiateRequest;
import com.camoutech.payload.request.WaiveFineRequest;
import com.camoutech.payload.response.PageResponse;
import com.camoutech.payload.response.PaymentInitiateResponse;
import com.camoutech.repository.BookLoanRepository;
import com.camoutech.repository.FineRepository;
import com.camoutech.service.FineService;
import com.camoutech.service.PaymentService;
import com.camoutech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FineServiceImpl implements FineService {
    private final BookLoanRepository bookLoanRepository;
    private final FineRepository fineRepository;
    private final FineMapper fineMapper;
    private final UserService userService;
    private final PaymentService paymentService;

    @Override
    public FineDTO createFine(CreateFineRequest createFineRequest) throws Exception {

        // 1. validate book loan exist
        BookLoan bookLoan = bookLoanRepository.findById(createFineRequest.getBookLoanId())
                .orElseThrow(()-> new Exception("Book loan doesn't exist"));

        // 2. create fine
        Fine fine = Fine.builder()
                .bookLoan(bookLoan)
                .user(bookLoan.getUser())
                .type(createFineRequest.getType())
                .amount(createFineRequest.getAmount())
                .status(FineStatus.PENDING)
                .reason(createFineRequest.getReason())
                .notes(createFineRequest.getNotes())
                .build();
        Fine savedFine = fineRepository.save(fine);
        return fineMapper.toDOTO(savedFine);
    }

    @Override
    public PaymentInitiateResponse payFine(Long fineId, String transactionId) throws Exception {
        // 1. validate fine exist
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(()-> new Exception("Fine doesn't exist"));

        // 2. check already paid
        if (fine.getStatus().equals(FineStatus.PAID)) {
            throw new Exception("fine already paid");
        }

        if (fine.getStatus().equals(FineStatus.WAIVED)) {
            throw new Exception("fine already waived");
        }

        // 3. initiate payment

        User user = userService.getCurrentUser();

        PaymentInitiateRequest request = PaymentInitiateRequest.builder()
                .userId(user.getId())
                .fineId(fine.getId())
                .paymentType(PaymentType.FINE)
                .gateway(PaymentGateway.STRIPE)
                .amount(fine.getAmount())
                .description("library fine payment")
                .build();

        return paymentService.initiatePayment(request);
    }

    @Override
    public void markFineAsPaid(Long fineId, Long amount, String transactionId) throws Exception {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(()-> new Exception("Fine not found with id: " + fineId));

        // Apply payment amount safely
        fine.applyPayment(amount);
        fine.setTransactionId(transactionId);
        fine.setStatus(FineStatus.PAID);
        fine.setUpdatedAt(LocalDateTime.now());

        fineRepository.save(fine);
    }

    @Override
    public FineDTO waiveFine(WaiveFineRequest waiveFineRequest) throws Exception {

        Fine fine = fineRepository.findById(waiveFineRequest.getFineId())
                .orElseThrow(()-> new Exception("Fine not found with id: "));

        // 2. Check if already waived or paid
        if (fine.getStatus() == FineStatus.WAIVED) {
            throw new Exception("Fine has already been waived");
        }

        if (fine.getStatus() == FineStatus.PAID) {
            throw new Exception("Fine has already been paid and cannot be waived");
        }

        // 3. Waive the fine
        User currentAdmin = userService.getCurrentUser();
        fine.waive(currentAdmin, waiveFineRequest.getReason());

        // 4. Save and return
        Fine savedFine = fineRepository.save(fine);

        return fineMapper.toDOTO(savedFine);
    }

    @Override
    public List<FineDTO> getMyFines(FineStatus status, FineType type) throws Exception {

        User currentUser = userService.getCurrentUser();
        List<Fine> fines;

        // Apply filters based on parameters
        if (status != null && type != null) {
            // Both filters
            fines = fineRepository.findByUserId(currentUser.getId()).stream()
                    .filter(f -> f.getStatus() == status && f.getType() == type)
                    .toList();
        } else if (status != null) {
            // status filter only
            fines = fineRepository.findByUserId(currentUser.getId()).stream()
                    .filter(f -> f.getStatus() == status)
                    .collect(Collectors.toList());
        } else if (type != null) {
            // Type filter only
            fines = fineRepository.findByUserIdAndType(currentUser.getId(), type);
        } else {
            // No filter - all fines for user
            fines = fineRepository.findByUserId(currentUser.getId());
        }
        return fines.stream().map(
                fineMapper::toDOTO
        ).collect(Collectors.toList());
    }

    @Override
    public PageResponse<FineDTO> getAllFines(FineStatus status, FineType type, Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending());

        Page<Fine> finePage = fineRepository.findAllWithFilters(
                userId,
                status,
                type,
                pageable
        );
        return convertToPageResponse(finePage);
    }

    private PageResponse<FineDTO> convertToPageResponse(Page<Fine> finePage) {
        List<FineDTO> fineDTOS = finePage.getContent()
                .stream()
                .map(fineMapper::toDOTO)
                .toList();

        return new PageResponse<>(fineDTOS,
                finePage.getNumber(),
                finePage.getSize(),
                finePage.getTotalElements(),
                finePage.getTotalPages(),
                finePage.isLast(),
                finePage.isFirst(),
                finePage.isEmpty());
    }
}
