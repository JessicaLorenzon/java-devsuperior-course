package com.devsuperior.dsmeta.repositories;

import com.devsuperior.dsmeta.dto.ReportDTO;
import com.devsuperior.dsmeta.dto.SummaryDTO;
import com.devsuperior.dsmeta.entities.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("""
             SELECT new com.devsuperior.dsmeta.dto.ReportDTO(obj.id, obj.date, obj.amount, obj.seller.name)
             FROM Sale obj
             WHERE obj.date BETWEEN :minDate AND :maxDate
             AND UPPER(obj.seller.name) LIKE UPPER(CONCAT('%', :name, '%'))
            """)
    Page<ReportDTO> searchReport(@Param("minDate") LocalDate minDate,
                                 @Param("maxDate") LocalDate maxDate, @Param("name") String name, Pageable pageable);

    @Query("""
            SELECT new com.devsuperior.dsmeta.dto.SummaryDTO (obj.seller.name, SUM(obj.amount))
            FROM Sale obj
            WHERE obj.date BETWEEN :minDate AND :maxDate
            GROUP BY obj.seller.name
            """)
    List<SummaryDTO> searchSummary(@Param("minDate") LocalDate minDate, @Param("maxDate") LocalDate maxDate);
}
