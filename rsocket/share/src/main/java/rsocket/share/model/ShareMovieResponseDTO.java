package rsocket.share.model;

import java.time.LocalDateTime;

public record ShareMovieResponseDTO(String imdb, String title, LocalDateTime lastModifiedDate, Integer likes, Integer dislikes) {
}
