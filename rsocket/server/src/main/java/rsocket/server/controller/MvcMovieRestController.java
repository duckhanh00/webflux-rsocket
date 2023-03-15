package rsocket.server.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import rsocket.client.model.AddMovieDTO;
import rsocket.server.entity.mongo.Movie;
import rsocket.server.service.MvcMovieService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mvc")
public class MvcMovieRestController {

  private final MvcMovieService movieService;

  @GetMapping(value = "/movies", produces = MediaType.APPLICATION_NDJSON_VALUE)
  public List<Movie> getMovies() {
    return movieService.getMovies();
  }

  @GetMapping("/movies/{imdb}")
  public Movie getMovie(@PathVariable String imdb) {
    return movieService.getMovie(imdb);
  }

  @PostMapping("/movies")
  public Movie addMovie(@Valid @RequestBody AddMovieDTO addMovieDTO) {
    return movieService.addMovie(addMovieDTO);
  }

  @DeleteMapping("/movies/{imdb}")
  public String deleteMovie(@PathVariable String imdb) {
    return movieService.deleteMovie(imdb).getImdb();
  }

  @PutMapping("/movies/{imdb}/like")
  public String likeMovie(@PathVariable String imdb) {
    return movieService.likeMovie(imdb);
  }

  @PutMapping("/movies/{imdb}/dislike")
  public String dislikeMovie(@PathVariable String imdb) {
    return movieService.dislikeMovie(imdb);
  }
}
