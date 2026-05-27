package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

public class ScoreServiceTests {

    @InjectMocks
    private ScoreService service;

    @Mock
    private ScoreRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private MovieRepository movieRepository;

    private Long existingId, nonExistingId, movieId;
    private ScoreEntity score;
    private ScoreDTO scoreDTO;
    private UserEntity user;
    private MovieEntity movie;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        existingId = 1L;
        nonExistingId = 2L;

        score = ScoreFactory.createScoreEntity();
        scoreDTO = new ScoreDTO(existingId, score.getValue());

        movie = MovieFactory.createMovieEntity();
        user = UserFactory.createUserEntity();

        movieId = scoreDTO.getMovieId();

        movie.getScores().add(score);

        Mockito.when(userService.authenticated()).thenReturn(user);

        Mockito.when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        Mockito.when(movieRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.saveAndFlush(any())).thenReturn(score);

        Mockito.when(movieRepository.save(any())).thenReturn(movie);
    }

    @Test
    public void saveScoreShouldReturnMovieDTO() {
        MovieDTO result = service.saveScore(scoreDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), movieId);
    }

    @Test
    public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
        ScoreDTO invalidDTO = new ScoreDTO(nonExistingId, score.getValue());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.saveScore(invalidDTO);
        });
    }
}
