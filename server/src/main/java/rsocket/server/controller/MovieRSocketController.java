package rsocket.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.messaging.rsocket.annotation.support.RSocketFrameTypeMessageCondition;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rsocket.client.model.AddMovieDTO;
import rsocket.server.entity.mongo.Movie;
import rsocket.server.mapper.MovieMapper;
import rsocket.server.service.MovieService;

@RequiredArgsConstructor
@Controller
public class MovieRSocketController {

  private static final String RSOCKET_FRAME_TYPE =
      RSocketFrameTypeMessageCondition.FRAME_TYPE_HEADER;
  private static final String CONTENT_TYPE = "contentType";

  // -- Request-Stream
  // ===================
  private final MovieService movieService;

  // -- Request-Response
  // =====================
  private final MovieMapper movieMapper;

//  @GetMapping(value = "/movies", produces = MediaType.APPLICATION_NDJSON_VALUE)
  @MessageMapping("get.movies")
  public Flux<Movie> getMovies(
      @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
      @Header(CONTENT_TYPE) String contentType) {
    return movieService.getMovies();
  }

//  @GetMapping("/movies/{imdb}")
  @MessageMapping("get.movie")
  public Mono<Movie> getMovie(
      String imdb,
      @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
      @Header(CONTENT_TYPE) String contentType) {
    return movieService.getMovie(imdb);
  }

//  @PostMapping("/movies")
  @MessageMapping("add.movie")
  public Mono<Movie> addMovie(
      @Valid AddMovieDTO addMovieDTO,
      @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
      @Header(CONTENT_TYPE) String contentType) {
    return movieService.addMovie(addMovieDTO);
  }

//  @DeleteMapping("/movies/{imdb}")
  @MessageMapping("delete.movie")
  public Mono<Movie> deleteMovie(
      String imdb,
      @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
      @Header(CONTENT_TYPE) String contentType) {
    return movieService.deleteMovie(imdb);
  }

//  @PutMapping("/movies/{imdb}/like")
  @MessageMapping("like.movie")
  public Mono<String> likeMovie(
      String imdb,
      @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
      @Header(CONTENT_TYPE) String contentType) {
    return movieService.likeMovie(imdb);
  }

//  @PutMapping("/movies/{imdb}/dislike")
  @MessageMapping("dislike.movie")
  public Mono<String> dislikeMovie(
      String imdb,
      @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
      @Header(CONTENT_TYPE) String contentType) {
    return movieService.dislikeMovie(imdb);
  }

  //  @MessageMapping("select.movies")
  //  public Flux<String> selectMovies(
  //      Flux<String> imdbs,
  //      @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
  //      @Header(CONTENT_TYPE) String contentType) {
  //    return imdbs
  //        .flatMap(movieService::getMovie)
  //        .map(
  //            movie ->
  //                String.format(
  //                    "| IMBD: %-10s | TITLE: %-30s | LIKES: %-5s | DISLIKES: %-5s |",
  //                    movie.getImdb(), movie.getTitle(), movie.getLikes(), movie.getDislikes()));
  //  }

  @ConnectMapping("client.registration")
  public Mono<Void> clientRegistration(
      RSocketRequester rSocketRequester,
      @Payload String clientId,
      @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
      @Header(CONTENT_TYPE) String contentType) {
    return Mono.empty();
  }
}
