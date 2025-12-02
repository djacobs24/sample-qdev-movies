package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("genres", movieService.getAllGenres());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Searches for movies based on query parameters.
     * Arrr! This be the treasure hunt endpoint, me hearty!
     * 
     * @param name Movie name to search for (optional)
     * @param id Movie ID to search for (optional)
     * @param genre Movie genre to search for (optional)
     * @param model Spring model for HTML response
     * @return Search results view name
     */
    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Ahoy! Starting treasure hunt with name: {}, id: {}, genre: {}", name, id, genre);
        
        // Validate that at least one search parameter is provided
        if ((name == null || name.trim().isEmpty()) && 
            id == null && 
            (genre == null || genre.trim().isEmpty())) {
            
            logger.warn("No search criteria provided, ye scurvy landlubber!");
            model.addAttribute("title", "Search the Seven Seas");
            model.addAttribute("message", "Arrr! Ye need to provide some search criteria to find yer treasure, matey!");
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("genres", movieService.getAllGenres());
            return "movies";
        }
        
        try {
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            // Prepare search summary for display
            String searchSummary = buildSearchSummary(name, id, genre, searchResults.size());
            
            model.addAttribute("movies", searchResults);
            model.addAttribute("searchSummary", searchSummary);
            model.addAttribute("searchName", name);
            model.addAttribute("searchId", id);
            model.addAttribute("searchGenre", genre);
            model.addAttribute("genres", movieService.getAllGenres());
            
            if (searchResults.isEmpty()) {
                model.addAttribute("noResultsMessage", 
                    "Shiver me timbers! No treasure found matching yer search, matey. Try different criteria!");
            }
            
            return "search-results";
            
        } catch (Exception e) {
            logger.error("Blimey! Error during treasure hunt: {}", e.getMessage());
            model.addAttribute("title", "Search Error");
            model.addAttribute("message", "Arrr! Something went wrong during the treasure hunt: " + e.getMessage());
            return "error";
        }
    }

    /**
     * REST API endpoint for movie search (returns JSON).
     * This be for the tech-savvy pirates who prefer their treasure in JSON format!
     * 
     * @param name Movie name to search for (optional)
     * @param id Movie ID to search for (optional)
     * @param genre Movie genre to search for (optional)
     * @return JSON response with search results
     */
    @GetMapping("/api/movies/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMoviesApi(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("API treasure hunt requested with name: {}, id: {}, genre: {}", name, id, genre);
        
        Map<String, Object> response = new HashMap<>();
        
        // Validate that at least one search parameter is provided
        if ((name == null || name.trim().isEmpty()) && 
            id == null && 
            (genre == null || genre.trim().isEmpty())) {
            
            response.put("success", false);
            response.put("message", "At least one search parameter (name, id, or genre) must be provided");
            response.put("results", List.of());
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            response.put("success", true);
            response.put("message", "Search completed successfully");
            response.put("results", searchResults);
            response.put("totalResults", searchResults.size());
            response.put("searchCriteria", Map.of(
                "name", name != null ? name : "",
                "id", id != null ? id : "",
                "genre", genre != null ? genre : ""
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("API search error: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Search failed: " + e.getMessage());
            response.put("results", List.of());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Builds a pirate-themed search summary message.
     * 
     * @param name Search name criteria
     * @param id Search ID criteria
     * @param genre Search genre criteria
     * @param resultCount Number of results found
     * @return Formatted search summary
     */
    private String buildSearchSummary(String name, Long id, String genre, int resultCount) {
        StringBuilder summary = new StringBuilder("Ahoy! Searched for treasure");
        
        boolean hasMultipleCriteria = false;
        if (name != null && !name.trim().isEmpty()) {
            summary.append(" with name containing '").append(name).append("'");
            hasMultipleCriteria = true;
        }
        
        if (id != null) {
            if (hasMultipleCriteria) summary.append(" and");
            summary.append(" with ID ").append(id);
            hasMultipleCriteria = true;
        }
        
        if (genre != null && !genre.trim().isEmpty()) {
            if (hasMultipleCriteria) summary.append(" and");
            summary.append(" in genre '").append(genre).append("'");
        }
        
        summary.append(". Found ").append(resultCount).append(" movie");
        if (resultCount != 1) {
            summary.append("s");
        }
        summary.append(" in our treasure chest, me hearty!");
        
        return summary.toString();
    }
}