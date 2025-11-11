package com.devsuperior.dsmeta.controllers;

import com.devsuperior.dsmeta.dto.SaleMinDTO;
import com.devsuperior.dsmeta.dto.SumaryDTO;
import com.devsuperior.dsmeta.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/sales")
public class SaleController {

    @Autowired
    private SaleService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<SaleMinDTO> findById(@PathVariable Long id) {
        SaleMinDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/report")
    public ResponseEntity<?> getReport() {
        // TODO
        return null;
    }

    @GetMapping(value = "/summary")
    public ResponseEntity<List<SumaryDTO>> getSummary(@RequestParam(required = false) String minDate,
            @RequestParam(required = false) String maxDate) {
        List<SumaryDTO> result = service.searchSumary(minDate, maxDate);
        return ResponseEntity.ok(result);
    }
}
