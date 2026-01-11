package com.camoutech.service;

import com.camoutech.payload.dto.ReservationDTO;
import com.camoutech.payload.request.ReservationRequest;
import com.camoutech.payload.request.ReservationSearchRequest;
import com.camoutech.payload.response.PageResponse;

public interface ReservationService {

    ReservationDTO createReservation(ReservationRequest reservationRequest) throws Exception;

    ReservationDTO createReservationForUser(ReservationRequest reservationRequest, Long userId) throws Exception;

    ReservationDTO cancelReservation(Long reservationId) throws Exception;
    ReservationDTO fulfillReservation(Long reservationId) throws Exception;

    PageResponse<ReservationDTO> getMyReservation(ReservationSearchRequest searchRequest) throws Exception;

    PageResponse<ReservationDTO> searchReservation(ReservationSearchRequest searchRequest);
}
