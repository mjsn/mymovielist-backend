package me.mjsn.mymovielist.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

@RepositoryRestResource(excerptProjection = Users.class)
public interface UserRepository extends CrudRepository<User, UUID>, QuerydslPredicateExecutor<User> {

	Page<User> findAll(Pageable pageable);


	User findByUsername(@Param("username") String username);

	// Only admins can delete user accounts
	@PreAuthorize("hasRole('ADMIN')")
	@Override
	void delete(User user);

	// Get every user's statistics
	List userStats();
	
	// Get one user's statistics
	UserStat userStat(@Param("username") String username);

}