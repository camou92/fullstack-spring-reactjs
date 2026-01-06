package com.camoutech.domain;

public enum PaymentType {
    FINE, MEMBERSHIP,
    LOST_BOOK_PENALTY,

    /**
     * Payment for domaged book penalty
     */
    DAMAGED_BOOK_PENALTY,

    /**
     * Refund issued to user
     */
    REFUND
}
