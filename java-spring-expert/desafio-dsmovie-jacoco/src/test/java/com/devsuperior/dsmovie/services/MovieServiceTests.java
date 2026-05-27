package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

public class MovieServiceTests {

    @InjectMocks
    private MovieService service;

    @Mock
    private MovieRepository repository;

    private Long existingId, nonExistingId, dependentId;
    private String movieTitle;
    private MovieEntity movie;
    private MovieDTO movieDTO;
    private PageImpl<MovieEntity> page;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;

        movieTitle = "Harry Potter e o Cálice de Fogo";

        movie = MovieFactory.createMovieEntity(movieTitle);
        movieDTO = new MovieDTO(movie);
        page = new PageImpl<>(List.of(movie));

        Mockito.when(repository.searchByTitle(any(), (Pageable) any())).thenReturn(page);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(movie));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.save(any())).thenReturn(movie);

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(movie);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }

    @Test
    public void findAllShouldReturnPagedMovieDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MovieDTO> result = service.findAll(movieTitle, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getSize(), 1);
        Assertions.assertEquals(result.iterator().next().getTitle(), movieTitle);
    }

    @Test
    public void findByIdShouldReturnMovieDTOWhenIdExists() {
        MovieDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingId);
        Assertions.assertEquals(result.getTitle(), movieTitle);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
    }

    @Test
    public void insertShouldReturnMovieDTO() {
        MovieDTO result = service.insert(movieDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), movie.getId());
    }

    @Test
    public void updateShouldReturnMovieDTOWhenIdExists() {
        MovieDTO result = service.update(existingId, movieDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), movie.getId());
        Assertions.assertEquals(result.getTitle(), movieDTO.getTitle());
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, movieDTO);
        });
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });
    }
}
