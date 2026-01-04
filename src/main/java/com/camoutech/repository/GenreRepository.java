package com.camoutech.repository;

import com.camoutech.modal.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 28/12/2025
 */
public interface GenreRepository extends JpaRepository<Genre, Long> {

    List<Genre> findByActiveTrueOrderByDisplayOrderAsc();

    List<Genre> findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();

    List<Genre> findByParentGenreIdAndActiveTrueOrderByDisplayOrderAsc(Long parentGenreId);

    long countByActiveTrue();

    /*@Query("select count(b) from book b where b.genre.id=:genreId")
    long countBooksByGenre(@Param("genreId") Long genreId);*/
}
