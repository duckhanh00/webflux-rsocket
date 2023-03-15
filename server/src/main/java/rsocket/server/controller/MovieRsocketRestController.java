package rsocket.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.rsocket.annotation.support.RSocketFrameTypeMessageCondition;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rsocket.client.model.AddMovieDTO;
import rsocket.server.entity.mongo.Movie;
import rsocket.server.mapper.MovieMapper;
import rsocket.server.service.MovieService;

@RequiredArgsConstructor
@Controller
public class MovieRsocketRestController {

  private static final String RSOCKET_FRAME_TYPE =
      RSocketFrameTypeMessageCondition.FRAME_TYPE_HEADER;
  private static final String CONTENT_TYPE = "contentType";

  private final MovieService movieService;

  private final MovieMapper movieMapper;

  @GetMapping(value = "/movies", produces = MediaType.APPLICATION_NDJSON_VALUE)
  public Flux<Movie> getMovies(
      @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
      @Header(CONTENT_TYPE) String contentType) {
    return movieService.getMovies();
  }

  @GetMapping("/movies/{imdb}")
  public Mono<Movie> getMovie(
      String imdb,
      @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
      @Header(CONTENT_TYPE) String contentType) {
    return movieService.getMovie(imdb);
  }

  @PostMapping("/movies")
  public Mono<Movie> addMovie(
      @Valid AddMovieDTO addMovieDTO,
      @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
      @Header(CONTENT_TYPE) String contentType) {
    return movieService.addMovie(addMovieDTO);
  }

  @DeleteMapping("/movies/{imdb}")
  public Mono<Movie> deleteMovie(
      String imdb,
      @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
      @Header(CONTENT_TYPE) String contentType) {
    return movieService.deleteMovie(imdb);
  }

  @PutMapping("/movies/{imdb}/like")
  public Mono<String> likeMovie(
      String imdb,
      @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
      @Header(CONTENT_TYPE) String contentType) {
    return movieService.likeMovie(imdb);
  }

  @PutMapping("/movies/{imdb}/dislike")
  public Mono<String> dislikeMovie(
      String imdb,
      @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
      @Header(CONTENT_TYPE) String contentType) {
    return movieService.dislikeMovie(imdb);
  }
}
