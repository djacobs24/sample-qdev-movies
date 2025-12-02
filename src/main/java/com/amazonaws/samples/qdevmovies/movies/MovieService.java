package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Searches for movies based on the provided criteria.
     * Arrr! This method be the treasure hunter that finds yer movies, matey!
     * 
     * @param name Movie name to search for (partial match, case-insensitive)
     * @param id Movie ID to search for (exact match)
     * @param genre Movie genre to search for (exact match, case-insensitive)
     * @return List of movies matching the search criteria
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Ahoy! Searching for treasure with criteria - name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> results = movies.stream()
            .filter(movie -> matchesSearchCriteria(movie, name, id, genre))
            .collect(Collectors.toList());
            
        logger.info("Found {} movies in our treasure chest, me hearty!", results.size());
        return results;
    }

    /**
     * Checks if a movie matches the search criteria.
     * This be the crew member that decides if a movie be worthy treasure!
     * 
     * @param movie The movie to check
     * @param name Name criteria (can be null)
     * @param id ID criteria (can be null)
     * @param genre Genre criteria (can be null)
     * @return true if movie matches all provided criteria
     */
    private boolean matchesSearchCriteria(Movie movie, String name, Long id, String genre) {
        // If name is provided, check for partial match (case-insensitive)
        if (name != null && !name.trim().isEmpty()) {
            if (!movie.getMovieName().toLowerCase().contains(name.toLowerCase().trim())) {
                return false;
            }
        }
        
        // If ID is provided, check for exact match
        if (id != null) {
            if (!movie.getId().equals(id)) {
                return false;
            }
        }
        
        // If genre is provided, check for exact match (case-insensitive)
        if (genre != null && !genre.trim().isEmpty()) {
            if (!movie.getGenre().toLowerCase().equals(genre.toLowerCase().trim())) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Gets all unique genres from the movie collection.
     * Arrr! This method shows ye all the types of treasure we have aboard!
     * 
     * @return List of unique genres
     */
    public List<String> getAllGenres() {
        return movies.stream()
            .map(Movie::getGenre)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}
