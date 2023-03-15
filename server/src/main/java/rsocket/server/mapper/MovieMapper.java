package rsocket.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rsocket.client.model.AddMovieDTO;
import rsocket.server.entity.mongo.Movie;

@Mapper(componentModel = "spring")
public interface MovieMapper {

  @Mapping(target = "likes", ignore = true)
  @Mapping(target = "dislikes", ignore = true)
  @Mapping(target = "lastModifiedDate", ignore = true)
  Movie toMovie(AddMovieDTO addMovieDTO);
}
