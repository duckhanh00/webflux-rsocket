package rsocket.share.command;

import com.google.gson.Gson;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.reactive.function.client.WebClient;
import rsocket.share.model.ShareAddMovieDTO;
import rsocket.share.model.ShareMovieResponseDTO;

@ShellComponent
@ShellCommandGroup("movie-server REST commands")
public class MovieServerRestCommand {

  private final WebClient webClient;
  private final Gson gson;

  public MovieServerRestCommand(WebClient webClient, Gson gson) {
    this.webClient = webClient;
    this.gson = gson;
  }

  @ShellMethod(key = "get-movies-rest", value = "Get all movies using REST")
  public String getMoviesRest() {
    List<String> movies =
        webClient
            .get()
            .retrieve()
            .bodyToFlux(ShareMovieResponseDTO.class)
            .map(gson::toJson)
            .collectList()
            .block();
    return Objects.requireNonNull(movies).stream()
        .collect(Collectors.joining(System.lineSeparator()));
  }

  @ShellMethod(key = "get-movie-rest", value = "Get movie by imdb using REST")
  public String getMovieRest(String imdb) {
    return webClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/{imdb}").build(imdb))
        .retrieve()
        .bodyToMono(ShareMovieResponseDTO.class)
        .map(gson::toJson)
        .block();
  }

  @ShellMethod(key = "add-movie-rest", value = "Add movie using REST")
  public String addMovieRest(String imdb, String title) {
    ShareAddMovieDTO addMovieRequest = new ShareAddMovieDTO(imdb, title);
    return webClient
        .post()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(addMovieRequest)
        .retrieve()
        .bodyToMono(ShareMovieResponseDTO.class)
        .map(gson::toJson)
        .block();
  }

  @ShellMethod(key = "delete-movie-rest", value = "Delete movie using REST")
  public String deleteMovieRest(String imdb) {
    return webClient
        .delete()
        .uri(uriBuilder -> uriBuilder.path("/{imdb}").build(imdb))
        .retrieve()
        .bodyToMono(String.class)
        .map(gson::toJson)
        .block();
  }

  @ShellMethod(key = "like-movie-rest", value = "Like movie using REST")
  public String likeMovieRest(String imdb) {
     return webClient
            .get()
            .uri(uriBuilder -> uriBuilder.path("/{imdb}/like").build(imdb))
            .retrieve()
            .bodyToMono(String.class)
            .block();
  }

  @ShellMethod(key = "dislike-movie-rest", value = "Dislike movie using REST")
  public String dislikeMovieRest(String imdb) {
    webClient
        .put()
        .uri(uriBuilder -> uriBuilder.path("/{imdb}/dislike").build(imdb))
        .retrieve()
        .bodyToMono(Void.class)
        .block();
    return "Dislike submitted";
  }
}
