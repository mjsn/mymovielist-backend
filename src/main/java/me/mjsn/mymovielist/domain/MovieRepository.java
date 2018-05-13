package me.mjsn.mymovielist.domain;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

public interface MovieRepository extends CrudRepository<Movie, UUID>, QuerydslPredicateExecutor<Movie> {

	Page<Movie> findAll(Pageable pageable);

	Movie findById(@Param("id") int id);

	Page<Movie> findByTmdbId(@Param("tmdbId") int tmdbId, Pageable pageable);

	Page<Movie> findByUser_Username(@Param("username") String username, Pageable pageable);

	// Movie item's user has to match the user posting it or you have to be an admin and the movie has to be in MongoDB
	@PreAuthorize("(hasRole('ADMIN') or #movie.user.username == principal.username) and @tmdbApi.tmdbIdExists(#movie.tmdbId)")
	@Override
	Movie save(@Param("movie") Movie movie);

	// Movie item's user has to match the user deleting it or you have to be an admin
	@PreAuthorize("hasRole('ADMIN') or #movie.user.username == principal.username")
	@Override
	void delete(@Param("movie") Movie movie);

}