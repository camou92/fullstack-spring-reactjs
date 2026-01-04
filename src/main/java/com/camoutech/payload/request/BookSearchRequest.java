package com.camoutech.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 29/12/2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSearchRequest {

    private String searchTerm;
    private Long genreId;
    private Boolean availableOnly;
    private Integer page=0;
    private Integer size=20;
    private String sortBy="createdAt";
    private String sortDirection="DESC";
}
