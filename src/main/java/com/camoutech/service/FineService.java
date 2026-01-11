package com.camoutech.service;

import com.camoutech.domain.FineStatus;
import com.camoutech.domain.FineType;
import com.camoutech.payload.dto.FineDTO;
import com.camoutech.payload.request.CreateFineRequest;
import com.camoutech.payload.request.WaiveFineRequest;
import com.camoutech.payload.response.PageResponse;
import com.camoutech.payload.response.PaymentInitiateResponse;

import java.util.List;

public interface FineService {

    FineDTO createFine(CreateFineRequest createFineRequest) throws Exception;

    PaymentInitiateResponse payFine(Long fineId, String transactionId) throws Exception;

    void markFineAsPaid(Long fineId, Long amount, String transactionId) throws Exception;

    FineDTO waiveFine(WaiveFineRequest waiveFineRequest) throws Exception;

    List<FineDTO> getMyFines(FineStatus status, FineType type) throws Exception;

    PageResponse<FineDTO> getAllFines(FineStatus status, FineType type, Long userId, int page, int size);

}
