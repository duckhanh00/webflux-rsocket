package rsocket.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rsocket.client.exception.RecordAlreadyExistException;import rsocket.client.model.AddMovieDTO;
import rsocket.server.entity.mongo.Movie;
import rsocket.server.mapper.MovieMapper;
import rsocket.server.repository.mongomvc.MvcMovieRepository;
import rsocket.server.service.MvcMovieService;import java.util.List;import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MvcMovieServiceImpl implements MvcMovieService {

    private final MovieMapper movieMapper;
    private final MvcMovieRepository mvcMovieRepository;

  @Override
  public Movie getMovie(String imdb) {
    return null;
  }

  @Override
  public List<Movie> getMovies() {
    return mvcMovieRepository.findAll();
  }

  @Override
  public Movie addMovie(AddMovieDTO addMovieDTO) {

    Movie movie = movieMapper.toMovie(addMovieDTO);
    Optional<Movie> checkExist = mvcMovieRepository.findById(movie.getImdb());
    if (checkExist.isPresent()) {
      throw new RecordAlreadyExistException();
    }
    mvcMovieRepository.save(movie);
    return movie;
  }

  @Override
  public Movie deleteMovie(String imdb) {
    return null;
  }

  @Override
  public String likeMovie(String imdb) {
    return null;
  }

  @Override
  public String dislikeMovie(String imdb) {
    return null;
  }
}
