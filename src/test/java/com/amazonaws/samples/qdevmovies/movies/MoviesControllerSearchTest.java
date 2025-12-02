package com.amazonaws.samples.qdevmovies.movies;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Arrr! These be the tests for our search treasure hunt endpoints, me hearty!
 * We be testing both HTML and API responses like true pirates!
 */
@WebMvcTest(MoviesController.class)
class MoviesControllerSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @MockBean
    private ReviewService reviewService;

    private List<Movie> testMovies;
    private List<String> testGenres;

    @BeforeEach
    void setUp() {
        // Prepare test treasure for our searches!
        testMovies = Arrays.asList(
            new Movie(1L, "The Prison Escape", "John Director", 1994, "Drama", "Test description", 142, 5.0),
            new Movie(2L, "The Family Boss", "Michael Filmmaker", 1972, "Crime/Drama", "Test description", 175, 5.0),
            new Movie(3L, "Space Wars", "George Director", 1977, "Adventure/Sci-Fi", "Test description", 121, 4.0)
        );
        
        testGenres = Arrays.asList("Drama", "Crime/Drama", "Adventure/Sci-Fi", "Action/Crime");
        
        // Setup default mock behaviors
        when(movieService.getAllMovies()).thenReturn(testMovies);
        when(movieService.getAllGenres()).thenReturn(testGenres);
    }

    @Test
    @DisplayName("Should display search form on main movies page")
    void testMoviesPageContainsSearchForm() throws Exception {
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(view().name("movies"))
                .andExpect(model().attributeExists("movies"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Hunt for Yer Perfect Treasure")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Start Treasure Hunt!")));
    }

    @Test
    @DisplayName("Should search movies by name successfully")
    void testSearchMoviesByName() throws Exception {
        // Arrange
        List<Movie> searchResults = Arrays.asList(testMovies.get(0), testMovies.get(1));
        when(movieService.searchMovies("the", null, null)).thenReturn(searchResults);

        // Act & Assert
        mockMvc.perform(get("/movies/search")
                .param("name", "the"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attributeExists("movies"))
                .andExpect(model().attributeExists("searchSummary"))
                .andExpect(model().attribute("searchName", "the"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Treasure Hunt Results")));
    }

    @Test
    @DisplayName("Should search movies by ID successfully")
    void testSearchMoviesById() throws Exception {
        // Arrange
        List<Movie> searchResults = Arrays.asList(testMovies.get(0));
        when(movieService.searchMovies(null, 1L, null)).thenReturn(searchResults);

        // Act & Assert
        mockMvc.perform(get("/movies/search")
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attributeExists("movies"))
                .andExpect(model().attribute("searchId", 1L));
    }

    @Test
    @DisplayName("Should search movies by genre successfully")
    void testSearchMoviesByGenre() throws Exception {
        // Arrange
        List<Movie> searchResults = Arrays.asList(testMovies.get(0));
        when(movieService.searchMovies(null, null, "Drama")).thenReturn(searchResults);

        // Act & Assert
        mockMvc.perform(get("/movies/search")
                .param("genre", "Drama"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attributeExists("movies"))
                .andExpect(model().attribute("searchGenre", "Drama"));
    }

    @Test
    @DisplayName("Should handle search with multiple criteria")
    void testSearchMoviesWithMultipleCriteria() throws Exception {
        // Arrange
        List<Movie> searchResults = Arrays.asList(testMovies.get(1));
        when(movieService.searchMovies("family", null, "Crime/Drama")).thenReturn(searchResults);

        // Act & Assert
        mockMvc.perform(get("/movies/search")
                .param("name", "family")
                .param("genre", "Crime/Drama"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attribute("searchName", "family"))
                .andExpect(model().attribute("searchGenre", "Crime/Drama"));
    }

    @Test
    @DisplayName("Should return to movies page when no search criteria provided")
    void testSearchWithoutCriteria() throws Exception {
        mockMvc.perform(get("/movies/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("movies"))
                .andExpect(model().attributeExists("movies"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ye need to provide some search criteria")));
    }

    @Test
    @DisplayName("Should handle empty search results gracefully")
    void testSearchWithNoResults() throws Exception {
        // Arrange
        when(movieService.searchMovies("nonexistent", null, null)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/movies/search")
                .param("name", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attributeExists("noResultsMessage"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("No treasure found")));
    }

    @Test
    @DisplayName("Should return JSON response for API search endpoint")
    void testApiSearchEndpoint() throws Exception {
        // Arrange
        List<Movie> searchResults = Arrays.asList(testMovies.get(0));
        when(movieService.searchMovies("prison", null, null)).thenReturn(searchResults);

        // Act & Assert
        mockMvc.perform(get("/api/movies/search")
                .param("name", "prison")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results[0].movieName").value("The Prison Escape"))
                .andExpect(jsonPath("$.totalResults").value(1));
    }

    @Test
    @DisplayName("Should return bad request for API search without criteria")
    void testApiSearchWithoutCriteria() throws Exception {
        mockMvc.perform(get("/api/movies/search")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("At least one search parameter (name, id, or genre) must be provided"));
    }

    @Test
    @DisplayName("Should return empty results for API search with no matches")
    void testApiSearchWithNoResults() throws Exception {
        // Arrange
        when(movieService.searchMovies("nonexistent", null, null)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/movies/search")
                .param("name", "nonexistent")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.results").isEmpty())
                .andExpect(jsonPath("$.totalResults").value(0));
    }

    @Test
    @DisplayName("Should handle API search with all parameters")
    void testApiSearchWithAllParameters() throws Exception {
        // Arrange
        List<Movie> searchResults = Arrays.asList(testMovies.get(0));
        when(movieService.searchMovies("prison", 1L, "Drama")).thenReturn(searchResults);

        // Act & Assert
        mockMvc.perform(get("/api/movies/search")
                .param("name", "prison")
                .param("id", "1")
                .param("genre", "Drama")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.searchCriteria.name").value("prison"))
                .andExpect(jsonPath("$.searchCriteria.id").value("1"))
                .andExpect(jsonPath("$.searchCriteria.genre").value("Drama"));
    }

    @Test
    @DisplayName("Should handle whitespace-only search parameters")
    void testSearchWithWhitespaceParameters() throws Exception {
        mockMvc.perform(get("/movies/search")
                .param("name", "   ")
                .param("genre", "   "))
                .andExpect(status().isOk())
                .andExpect(view().name("movies"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ye need to provide some search criteria")));
    }

    @Test
    @DisplayName("Should preserve search parameters in search results form")
    void testSearchResultsFormPreservesParameters() throws Exception {
        // Arrange
        List<Movie> searchResults = Arrays.asList(testMovies.get(0));
        when(movieService.searchMovies("prison", 1L, "Drama")).thenReturn(searchResults);

        // Act & Assert
        mockMvc.perform(get("/movies/search")
                .param("name", "prison")
                .param("id", "1")
                .param("genre", "Drama"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attribute("searchName", "prison"))
                .andExpect(model().attribute("searchId", 1L))
                .andExpect(model().attribute("searchGenre", "Drama"));
    }
}