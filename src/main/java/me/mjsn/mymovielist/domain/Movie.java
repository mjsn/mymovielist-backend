package me.mjsn.mymovielist.domain;

import java.time.Instant;
import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(uniqueConstraints=
@UniqueConstraint(columnNames = {"user_id", "tmdbId"}))
public class Movie {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(
			name = "uuid2",
			strategy = "uuid2"
			)
	@Column(name="id", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID id;

	private int tmdbId;

	@Access(AccessType.PROPERTY)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="user_id", nullable = false, updatable = false)
	private User user;

	@Range(min = 0, max = 5)
	private int score;

	@Range(min = 0, max = 2)
	private int status;

	@Range(min = 0, max = 1)
	private int fav;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long time = Instant.now().getEpochSecond();

	@Length(min = 0, max = 2000)
	private String notes;

	public Movie() {}

	public Movie(UUID id, int tmdbId, User user, int score, int status, int fav, Long time, String notes) {
		super();
		this.id = id;
		this.tmdbId = tmdbId;
		this.user = user;
		this.score = score;
		this.status = status;
		this.fav = fav;
		this.time = time;
		this.notes = notes;
	}

	public Movie(int tmdbId, User user, int score, int status, int fav, Long time, String notes) {
		super();
		this.tmdbId = tmdbId;
		this.user = user;
		this.score = score;
		this.status = status;
		this.fav = fav;
		this.time = time;
		this.notes = notes;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public int getTmdbId() {
		return tmdbId;
	}

	public void setTmdbId(int tmdbId) {
		this.tmdbId = tmdbId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getFav() {
		return fav;
	}

	public void setFav(int fav) {
		this.fav = fav;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public String toString() {
		return "Movie [id=" + id + ", tmdbId=" + tmdbId + ", user=" + user + ", score=" + score + ", status=" + status
				+ ", fav=" + fav + ", time=" + time + ", notes=" + notes + "]";
	}

}
