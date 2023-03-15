package rsocket.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rsocket.client.model.AddMovieDTO;
import rsocket.server.entity.mongo.Movie;
import rsocket.server.service.MovieService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MovieRestController {

  private final MovieService movieService;

  @GetMapping(value = "/movies", produces = MediaType.APPLICATION_NDJSON_VALUE)
  public Flux<Movie> getMovies() {
    return movieService.getMovies();
  }

  @GetMapping("/movies/{imdb}")
  public Mono<Movie> getMovie(@PathVariable String imdb) {
    return movieService.getMovie(imdb);
  }

  @PostMapping("/movies")
  public Mono<Movie> addMovie(@Valid @RequestBody AddMovieDTO addMovieDTO) {
    return movieService.addMovie(addMovieDTO);
  }

  @DeleteMapping("/movies/{imdb}")
  public Mono<String> deleteMovie(@PathVariable String imdb) {
    return movieService.deleteMovie(imdb).map(Movie::getImdb);
  }

  @PutMapping("/movies/{imdb}/like")
  public Mono<String> likeMovie(@PathVariable String imdb) {
    return movieService.likeMovie(imdb);
  }

  @PutMapping("/movies/{imdb}/dislike")
  public Mono<String> dislikeMovie(@PathVariable String imdb) {
    return movieService.dislikeMovie(imdb);
  }
}
