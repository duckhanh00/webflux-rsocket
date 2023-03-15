package rsocket.server.service;

import rsocket.client.model.AddMovieDTO;
import rsocket.server.entity.mongo.Movie;import java.util.List;

public interface MvcMovieService {

    Movie getMovie(String imdb);

    List<Movie> getMovies();

    Movie addMovie(AddMovieDTO addMovieDTO);

    Movie deleteMovie(String imdb);

    String likeMovie(String imdb);

    String dislikeMovie(String imdb);
}