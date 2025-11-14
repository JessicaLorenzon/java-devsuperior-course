package com.devsuperior.dsmeta.services;

import com.devsuperior.dsmeta.dto.ReportDTO;
import com.devsuperior.dsmeta.dto.SaleMinDTO;
import com.devsuperior.dsmeta.dto.SummaryDTO;
import com.devsuperior.dsmeta.entities.Sale;
import com.devsuperior.dsmeta.repositories.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class SaleService {

    @Autowired
    private SaleRepository repository;

    public SaleMinDTO findById(Long id) {
        Optional<Sale> result = repository.findById(id);
        Sale entity = result.get();
        return new SaleMinDTO(entity);
    }

    public List<SummaryDTO> searchSummary(String minDateString, String maxDateString) {
        LocalDate maxDate = maxDateString == null
                ? LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault())
                : convertStringToLocalDate(maxDateString);

        LocalDate minDate = minDateString == null
                ? maxDate.minusYears(1)
                : convertStringToLocalDate(minDateString);

        return repository.searchSummary(minDate, maxDate);
    }

    public Page<ReportDTO> searchReport(String minDateString, String maxDateString, String name, Pageable pageable) {
        LocalDate maxDate = maxDateString == null
                ? LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault())
                : convertStringToLocalDate(maxDateString);

        LocalDate minDate = minDateString == null
                ? maxDate.minusYears(1)
                : convertStringToLocalDate(minDateString);

        return repository.searchReport(minDate, maxDate, name, pageable);
    }

    private LocalDate convertStringToLocalDate(String dateString) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateString, fmt);
    }
}
