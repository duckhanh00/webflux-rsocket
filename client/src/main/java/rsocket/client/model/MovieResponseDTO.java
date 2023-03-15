package rsocket.client.model;

import java.time.LocalDateTime;

public record MovieResponseDTO(String imdb, String title, LocalDateTime lastModifiedDate, Integer likes, Integer dislikes) {
}
