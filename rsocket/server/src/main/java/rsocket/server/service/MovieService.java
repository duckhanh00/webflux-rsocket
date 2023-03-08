package rsocket.server.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rsocket.client.model.AddMovieDTO;import rsocket.server.entity.mongo.Movie;

public interface MovieService {

    Mono<Movie> getMovie(String imdb);

    Flux<Movie> getMovies();

    Mono<Movie> addMovie(AddMovieDTO addMovieDTO);

    Mono<Movie> deleteMovie(String imdb);

    Mono<Movie> likeMovie(String imdb);

    Mono<Movie> dislikeMovie(String imdb);
}
