package com.camoutech.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistDTO {

    private Long id;
    private Long userId;
    private String userFullName;
    private BookDTO book;
    private LocalDateTime addedAt;
    private String notes;

}
