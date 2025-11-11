package com.devsuperior.dsmeta.repositories;

import com.devsuperior.dsmeta.dto.SumaryDTO;
import org.apache.tomcat.jni.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devsuperior.dsmeta.entities.Sale;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("""
            SELECT new com.devsuperior.dsmeta.dto.SumaryDTO (obj.seller.name, SUM(obj.amount))
            FROM Sale obj
            WHERE obj.date BETWEEN :minDate AND :maxDate
            GROUP BY obj.seller.name
            """)
    List<SumaryDTO> searchSumary(@Param("minDate") LocalDate minDate, @Param("maxDate") LocalDate maxDate);
}
