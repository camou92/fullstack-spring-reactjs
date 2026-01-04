package com.camoutech.controller;

import com.camoutech.exception.GenreException;
import com.camoutech.payload.dto.GenreDTO;
import com.camoutech.payload.response.ApiResponse;
import com.camoutech.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 28/12/2025
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    // CREATE
    @PostMapping
    public ResponseEntity<GenreDTO> addGenre(@RequestBody GenreDTO genreDTO) {
        GenreDTO createdGenreDTO = genreService.createGenre(genreDTO);
        return ResponseEntity.ok(createdGenreDTO);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    // GET BY ID ✅
    @GetMapping("/{genreId}")
    public ResponseEntity<GenreDTO> getGenreById(
            @PathVariable Long genreId
    ) throws GenreException {
        return ResponseEntity.ok(genreService.getGenreById(genreId));
    }

    // UPDATE ✅
    @PutMapping("/{genreId}")
    public ResponseEntity<GenreDTO> updateGenre(
            @PathVariable Long genreId,
            @RequestBody GenreDTO genreDTO
    ) throws GenreException {
        return ResponseEntity.ok(genreService.updateGenre(genreId, genreDTO));
    }

    // SOFT DELETE
    @DeleteMapping("/{genreId}")
    public ResponseEntity<ApiResponse> deleteGenre(
            @PathVariable Long genreId
    ) throws GenreException {
        genreService.deleteGenre(genreId);
        return ResponseEntity.ok(
                new ApiResponse("genre deleted - soft delete", true)
        );
    }

    // HARD DELETE
    @DeleteMapping("/{genreId}/hard")
    public ResponseEntity<ApiResponse> hardDeleteGenre(
            @PathVariable Long genreId
    ) throws GenreException {
        genreService.hardDeleteGenre(genreId);
        return ResponseEntity.ok(
                new ApiResponse("genre deleted - hard delete", true)
        );
    }

    // TOP LEVEL
    @GetMapping("/top-level")
    public ResponseEntity<List<GenreDTO>> getTopLevelGenres() {
        return ResponseEntity.ok(genreService.getTopLevelGenres());
    }

    // COUNT
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalActiveGenres() {
        return ResponseEntity.ok(genreService.getTotalActiveGenres());
    }

    // BOOK COUNT
    @GetMapping("/{id}/book-count")
    public ResponseEntity<Long> getBookCountByGenres(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.getBookCountByGenre(id));
    }
}
