package com.facilityflow.repository;

import com.facilityflow.entity.Asset;
import com.facilityflow.entity.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long>, JpaSpecificationExecutor<Asset> {

    Optional<Asset> findByAssetTag(String assetTag);

    boolean existsByAssetTag(String assetTag);

    Page<Asset> findByRoomId(Long roomId, Pageable pageable);

    Page<Asset> findByStatus(AssetStatus status, Pageable pageable);

    @Query("select a from Asset a where a.warrantyExpiryDate between :start and :end")
    List<Asset> findExpiringWarranties(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("select count(a) from Asset a")
    long countAll();

    long countByStatus(AssetStatus status);

    @Query("select a from Asset a where lower(a.name) like lower(concat('%', :term, '%')) " +
           "or lower(a.assetTag) like lower(concat('%', :term, '%'))")
    Page<Asset> search(@Param("term") String term, Pageable pageable);
}
