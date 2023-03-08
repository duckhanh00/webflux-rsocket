package rsocket.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rsocket.client.exception.MovieNotFoundException;
import rsocket.client.exception.RecordAlreadyExistException;
import rsocket.client.model.AddMovieDTO;
import rsocket.server.entity.mongo.Movie;
import rsocket.server.mapper.MovieMapper;
import rsocket.server.repository.mongo.MovieRepository;
import rsocket.server.service.MovieService;

@RequiredArgsConstructor
@Service
public class MovieServiceImpl implements MovieService {

  private final MovieMapper movieMapper;
  private final MovieRepository movieRepository;

  @Override
  public Mono<Movie> getMovie(String imdb) {
    return movieRepository
        .findById(imdb)
        .switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)));
  }

  @Override
  public Flux<Movie> getMovies() {
    return movieRepository.findAll();
  }

  @Override
  public Mono<Movie> addMovie(AddMovieDTO addMovieDTO) {
    Movie movie = movieMapper.toMovie(addMovieDTO);
    return movieRepository
        .findById(movie.getImdb())
        .flatMap(item -> Mono.error(new RecordAlreadyExistException()))
        .then(Mono.just(movie))
        .switchIfEmpty(movieRepository.save(movie));
  }

  @Override
  public Mono<Movie> deleteMovie(String imdb) {
    return getMovie(imdb)
        .map(
            item -> {
              movieRepository.delete(item);
              return item;
            })
        .switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)));
  }

  @Override
  public Mono<Movie> likeMovie(String imdb) {
    return getMovie(imdb)
        .flatMap(
            item -> {
              item.setLikes(item.getLikes() + 1);
              return movieRepository.save(item);
            })
        .switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)));
  }

  @Override
  public Mono<Movie> dislikeMovie(String imdb) {
    return getMovie(imdb)
        .flatMap(
            item -> {
              item.setDislikes(item.getDislikes() + 1);
              return movieRepository.save(item);
            })
        .switchIfEmpty(Mono.error(new MovieNotFoundException(imdb)));
  }
}
