package rsocket.client.model;

import java.time.LocalDateTime;

public record UpdateMovieActionDTO(Action action, LocalDateTime timestamp, String imdb, String payload) {

    public enum Action {
        ADDED, DELETED, LIKED, DISLIKED
    }
}