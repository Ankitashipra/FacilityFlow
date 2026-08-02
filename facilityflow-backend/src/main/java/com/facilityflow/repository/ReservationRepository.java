package com.facilityflow.repository;

import com.facilityflow.entity.Reservation;
import com.facilityflow.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>,
        JpaSpecificationExecutor<Reservation> {

    Page<Reservation> findByRequestedById(Long userId, Pageable pageable);

    Page<Reservation> findByRoomId(Long roomId, Pageable pageable);

    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);

    @Query("select r from Reservation r where r.room.id = :roomId " +
           "and r.status in ('PENDING','APPROVED') " +
           "and r.startTime < :endTime and r.endTime > :startTime " +
           "and (:excludeId is null or r.id <> :excludeId)")
    List<Reservation> findOverlapping(@Param("roomId") Long roomId,
                                       @Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime,
                                       @Param("excludeId") Long excludeId);

    @Query("select r from Reservation r where r.status = 'APPROVED' and r.endTime < :now")
    List<Reservation> findExpiredApproved(@Param("now") LocalDateTime now);

    @Query("select r from Reservation r where r.status = 'PENDING' and r.startTime < :now")
    List<Reservation> findStalePending(@Param("now") LocalDateTime now);

    @Query("select count(r) from Reservation r where r.status = 'APPROVED'")
    long countActiveReservations();

    @Query("select r from Reservation r where r.room.id = :roomId and r.startTime >= :from and r.startTime < :to")
    List<Reservation> findCalendarForRoom(@Param("roomId") Long roomId,
                                           @Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to);
}
