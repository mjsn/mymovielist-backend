package me.mjsn.mymovielist.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.annotation.JsonProperty;

@SqlResultSetMapping(name = "userStats", classes = { 
		@ConstructorResult(
				targetClass = UserStat.class, 
				columns = {
						@ColumnResult(name = "id", type = UUID.class),
						@ColumnResult(name = "username", type = String.class),
						@ColumnResult(name = "seen_total", type = Integer.class),
						@ColumnResult(name = "favs_total", type = Integer.class),
						@ColumnResult(name = "later_total", type = Integer.class),
						@ColumnResult(name = "signup_time", type = Long.class)
				})
})

@NamedNativeQueries({
	@NamedNativeQuery(query = "select user.id, username, "
			+ "(select count(*) from movie where user.id = movie.user_id and movie.status = 2) seen_total, "
			+ "(select count(*) from movie where user.id = movie.user_id and movie.fav = 1) favs_total, "
			+ "(select count(*) from movie where user.id = movie.user_id and movie.status = 1) later_total, "
			+ "user.time signup_time from user left join movie on user.id = movie.user_id group by user.username order by user.time",
			name = "User.userStats", resultSetMapping="userStats"),

	@NamedNativeQuery(query = "select user.id, username, "
			+ "(select count(*) from movie where user.id = movie.user_id and movie.status = 2) seen_total, "
			+ "(select count(*) from movie where user.id = movie.user_id and movie.fav = 1) favs_total, "
			+ "(select count(*) from movie where user.id = movie.user_id and movie.status = 1) later_total, "
			+ "user.time signup_time from user left join movie on user.id = movie.user_id where user.username=:username group by user.username order by user.time",
			name = "User.userStat", resultSetMapping="userStats")
})

@Entity
public class User {
	public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(
			name = "uuid2",
			strategy = "uuid2"
			)
	@Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID id;

	@Pattern(regexp = "^[A-Za-z0-9]+$")
	@Column(nullable = false, unique = true, length = 20)
	@Length(min = 4, max = 20)
	private String username;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(nullable = false)
	private String password;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Column(nullable = false)
	private String role = "ROLE_USER";

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(nullable = false)
	private String email;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long time = Instant.now().getEpochSecond();

	@OneToMany(mappedBy = "user", orphanRemoval = true)
	private List<Movie> movieList = new ArrayList<Movie>();


	public User() {}


	public User(UUID id, String username, String password, String role, String email, Long time, List<Movie> movieList) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.role = role;
		this.email = email;
		this.time = time;
		this.movieList = movieList;
	}

	public User(String username, String password, String role, String email, Long time, List<Movie> movieList) {
		super();
		this.username = username;
		this.password = password;
		this.role = role;
		this.email = email;
		this.time = time;
		this.movieList = movieList;
	}


	public UUID getId() {
		return id;
	}


	public void setId(UUID id) {
		this.id = id;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public Long getTime() {
		return time;
	}


	public void setTime(Long time) {
		this.time = time;
	}


	public List<Movie> getMovieList() {
		return movieList;
	}


	public void setMovieList(List<Movie> movieList) {
		this.movieList = movieList;
	}


	public void setPassword(String password) {
		this.password = PASSWORD_ENCODER.encode(password);
	}

	public String getPassword() {
		return password;
	}


	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", role=" + role + ", email="
				+ email + ", movieList=" + movieList + "]";
	}

}
