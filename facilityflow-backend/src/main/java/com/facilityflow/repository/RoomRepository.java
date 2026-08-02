package com.facilityflow.repository;

import com.facilityflow.entity.Room;
import com.facilityflow.entity.RoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    Optional<Room> findByCode(String code);

    Page<Room> findByFloorId(Long floorId, Pageable pageable);

    Page<Room> findByStatus(RoomStatus status, Pageable pageable);

    @Query("select r from Room r where lower(r.name) like lower(concat('%', :term, '%')) " +
           "or lower(r.code) like lower(concat('%', :term, '%'))")
    Page<Room> search(@Param("term") String term, Pageable pageable);

    @Query("select r.floor.building.name as buildingName, count(res) as reservationCount " +
           "from Reservation res join res.room r " +
           "where res.status = 'APPROVED' group by r.floor.building.name order by count(res) desc")
    java.util.List<Object[]> findTopBuildingsByReservations();
}
