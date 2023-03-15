package rsocket.client.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record AddMovieDTO(@NotBlank String imdb, @Size(min = 1, max = 30) String title) {
}