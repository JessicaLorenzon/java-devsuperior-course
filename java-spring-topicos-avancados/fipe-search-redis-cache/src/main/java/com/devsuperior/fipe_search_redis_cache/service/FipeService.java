package com.devsuperior.fipe_search_redis_cache.service;

import com.devsuperior.fipe_search_redis_cache.dto.ConsultaFipeDTO;
import com.devsuperior.fipe_search_redis_cache.repository.ReferenciaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FipeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FipeService.class);

    private final ReferenciaRepository referenciaRepository;

    public FipeService(ReferenciaRepository referenciaRepository) {
        this.referenciaRepository = referenciaRepository;
    }

    @Cacheable
    public ConsultaFipeDTO consultar(Long modeloId, Integer anoModelo) {
        LOGGER.debug("Cache MISS — consultando PostgreSQL: modelo={}, ano={}", modeloId, anoModelo);

        referenciaRepository.simulateDelay();

        return referenciaRepository
                .findReferencias(modeloId, anoModelo)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Consulta FIPE não encontrada: modelo=%d, ano=%d"
                                .formatted(modeloId, anoModelo)));
    }

    @CacheEvict
    public void invalidar(Long modeloId, Integer anoModelo) {
        LOGGER.debug("Cache invalidado: modelo={}, ano={}", modeloId, anoModelo);
    }
}
