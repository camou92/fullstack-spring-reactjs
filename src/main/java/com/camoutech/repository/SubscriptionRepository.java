package com.camoutech.repository;

import com.camoutech.modal.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 03/01/2026
 */
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("select s from Subscription s where s.user.id = :userId AND " +
            "s.isActive = true and " +
             "s.startDate<=:today and s.endDate>=:today")
    Optional<Subscription> findActiveSubscriptionByUserId(
            @Param("userId") Long userId,
            @Param("today") LocalDate today
    );

    @Query("select s from Subscription  s where s.isActive=true " +
           "AND s.endDate<:today")
    List<Subscription> findExpiredActiveSubscriptions(@Param("today") LocalDate today);
}
