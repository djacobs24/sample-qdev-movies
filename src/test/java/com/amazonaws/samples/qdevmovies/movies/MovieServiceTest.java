package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Arrr! These be the tests for our MovieService treasure hunting methods, matey!
 * We be making sure our crew members work properly on the high seas!
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    void setUp() {
        // Initialize our treasure chest before each test, ye scallywag!
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Should load all movies from treasure chest")
    void testGetAllMovies() {
        // Arrange & Act
        List<Movie> movies = movieService.getAllMovies();
        
        // Assert - Make sure we have our expected treasure!
        assertNotNull(movies, "Movies list should not be null, arrr!");
        assertFalse(movies.isEmpty(), "Should have movies in our treasure chest!");
        assertEquals(12, movies.size(), "Should have 12 movies as per our JSON treasure map!");
    }

    @Test
    @DisplayName("Should find movie by valid ID like a true pirate")
    void testGetMovieByValidId() {
        // Arrange
        Long validId = 1L;
        
        // Act
        Optional<Movie> movie = movieService.getMovieById(validId);
        
        // Assert
        assertTrue(movie.isPresent(), "Should find the treasure with valid ID!");
        assertEquals("The Prison Escape", movie.get().getMovieName(), "Should find the correct movie treasure!");
        assertEquals(validId, movie.get().getId(), "Movie ID should match what we searched for!");
    }

    @Test
    @DisplayName("Should return empty when searching for non-existent treasure")
    void testGetMovieByInvalidId() {
        // Arrange
        Long invalidId = 999L;
        
        // Act
        Optional<Movie> movie = movieService.getMovieById(invalidId);
        
        // Assert
        assertFalse(movie.isPresent(), "Should not find treasure that doesn't exist!");
    }

    @Test
    @DisplayName("Should handle null ID like a seasoned pirate")
    void testGetMovieByNullId() {
        // Act
        Optional<Movie> movie = movieService.getMovieById(null);
        
        // Assert
        assertFalse(movie.isPresent(), "Should not find treasure with null ID!");
    }

    @Test
    @DisplayName("Should handle negative ID gracefully")
    void testGetMovieByNegativeId() {
        // Act
        Optional<Movie> movie = movieService.getMovieById(-1L);
        
        // Assert
        assertFalse(movie.isPresent(), "Should not find treasure with negative ID!");
    }

    @Test
    @DisplayName("Should search by movie name (partial match, case-insensitive)")
    void testSearchMoviesByName() {
        // Act - Search for movies with "the" in the name
        List<Movie> results = movieService.searchMovies("the", null, null);
        
        // Assert
        assertNotNull(results, "Search results should not be null!");
        assertFalse(results.isEmpty(), "Should find movies with 'the' in the name!");
        
        // Verify all results contain "the" (case-insensitive)
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"), 
                "Movie '" + movie.getMovieName() + "' should contain 'the'!");
        }
    }

    @Test
    @DisplayName("Should search by exact ID match")
    void testSearchMoviesByExactId() {
        // Act
        List<Movie> results = movieService.searchMovies(null, 5L, null);
        
        // Assert
        assertNotNull(results, "Search results should not be null!");
        assertEquals(1, results.size(), "Should find exactly one movie with ID 5!");
        assertEquals("Life Journey", results.get(0).getMovieName(), "Should find the correct movie!");
        assertEquals(5L, results.get(0).getId(), "Movie ID should match search criteria!");
    }

    @Test
    @DisplayName("Should search by genre (case-insensitive)")
    void testSearchMoviesByGenre() {
        // Act - Search for Drama movies
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        
        // Assert
        assertNotNull(results, "Search results should not be null!");
        assertFalse(results.isEmpty(), "Should find Drama movies in our treasure chest!");
        
        // Verify all results are Drama genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"), 
                "Movie '" + movie.getMovieName() + "' should be Drama genre!");
        }
    }

    @Test
    @DisplayName("Should search with multiple criteria (AND logic)")
    void testSearchMoviesWithMultipleCriteria() {
        // Act - Search for movies with "the" in name AND Crime/Drama genre
        List<Movie> results = movieService.searchMovies("the", null, "Crime/Drama");
        
        // Assert
        assertNotNull(results, "Search results should not be null!");
        
        // Verify all results match both criteria
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"), 
                "Movie should contain 'the' in name!");
            assertEquals("crime/drama", movie.getGenre().toLowerCase(), 
                "Movie should be Crime/Drama genre!");
        }
    }

    @Test
    @DisplayName("Should return empty list when no treasure matches criteria")
    void testSearchMoviesNoResults() {
        // Act - Search for non-existent movie
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        
        // Assert
        assertNotNull(results, "Search results should not be null!");
        assertTrue(results.isEmpty(), "Should return empty list when no matches found!");
    }

    @Test
    @DisplayName("Should handle empty string search gracefully")
    void testSearchMoviesWithEmptyString() {
        // Act - Search with empty name
        List<Movie> results = movieService.searchMovies("", null, null);
        
        // Assert
        assertNotNull(results, "Search results should not be null!");
        assertEquals(12, results.size(), "Empty string should return all movies!");
    }

    @Test
    @DisplayName("Should handle whitespace-only search parameters")
    void testSearchMoviesWithWhitespace() {
        // Act - Search with whitespace-only parameters
        List<Movie> results = movieService.searchMovies("   ", null, "   ");
        
        // Assert
        assertNotNull(results, "Search results should not be null!");
        assertEquals(12, results.size(), "Whitespace-only parameters should return all movies!");
    }

    @Test
    @DisplayName("Should search case-insensitively for movie names")
    void testSearchMoviesCaseInsensitive() {
        // Act - Search with different cases
        List<Movie> upperResults = movieService.searchMovies("THE", null, null);
        List<Movie> lowerResults = movieService.searchMovies("the", null, null);
        List<Movie> mixedResults = movieService.searchMovies("ThE", null, null);
        
        // Assert
        assertEquals(upperResults.size(), lowerResults.size(), 
            "Upper and lower case searches should return same number of results!");
        assertEquals(lowerResults.size(), mixedResults.size(), 
            "Mixed case search should return same number of results!");
    }

    @Test
    @DisplayName("Should get all unique genres from treasure chest")
    void testGetAllGenres() {
        // Act
        List<String> genres = movieService.getAllGenres();
        
        // Assert
        assertNotNull(genres, "Genres list should not be null!");
        assertFalse(genres.isEmpty(), "Should have genres in our collection!");
        
        // Check for expected genres from our test data
        assertTrue(genres.contains("Drama"), "Should contain Drama genre!");
        assertTrue(genres.contains("Crime/Drama"), "Should contain Crime/Drama genre!");
        assertTrue(genres.contains("Action/Crime"), "Should contain Action/Crime genre!");
        
        // Verify no duplicates (since we use distinct())
        assertEquals(genres.size(), genres.stream().distinct().count(), 
            "Genres list should not contain duplicates!");
    }

    @Test
    @DisplayName("Should search by exact genre match (case-insensitive)")
    void testSearchMoviesByExactGenreMatch() {
        // Act - Search for exact "Drama" genre (not "Crime/Drama")
        List<Movie> dramaResults = movieService.searchMovies(null, null, "Drama");
        
        // Assert
        assertNotNull(dramaResults, "Search results should not be null!");
        
        // Verify all results are exactly "Drama" genre, not "Crime/Drama"
        for (Movie movie : dramaResults) {
            assertEquals("drama", movie.getGenre().toLowerCase(), 
                "Should match exact genre, not partial match!");
        }
    }

    @Test
    @DisplayName("Should combine ID and name search criteria properly")
    void testSearchMoviesByIdAndName() {
        // Act - Search for movie with ID 1 AND name containing "Prison"
        List<Movie> results = movieService.searchMovies("Prison", 1L, null);
        
        // Assert
        assertNotNull(results, "Search results should not be null!");
        assertEquals(1, results.size(), "Should find exactly one movie matching both criteria!");
        assertEquals(1L, results.get(0).getId(), "Should match the ID criteria!");
        assertTrue(results.get(0).getMovieName().toLowerCase().contains("prison"), 
            "Should match the name criteria!");
    }

    @Test
    @DisplayName("Should return empty when ID and name don't match same movie")
    void testSearchMoviesConflictingCriteria() {
        // Act - Search for movie with ID 1 but name containing "Family" (which is ID 2)
        List<Movie> results = movieService.searchMovies("Family", 1L, null);
        
        // Assert
        assertNotNull(results, "Search results should not be null!");
        assertTrue(results.isEmpty(), "Should return empty when criteria conflict!");
    }
}