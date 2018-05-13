package me.mjsn.mymovielist.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;


@Entity
public class UserStat {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(
			name = "uuid2",
			strategy = "uuid2"
			)
	@Column(name= "id")
	private UUID id;

	@Column(name = "username")
	private String username;

	@Column(name = "seen_total")
	private int seen_total;

	@Column(name = "favs_total")
	private int favs_total;

	@Column(name = "later_total")
	private int later_total;

	@Column(name = "signup_time")
	private Long signup_time;

	public UserStat(UUID id, String username, int seen_total, int favs_total, int later_total, Long signup_time) {
		super();
		this.id = id;
		this.username = username;
		this.seen_total = seen_total;
		this.favs_total = favs_total;
		this.later_total = later_total;
		this.signup_time = signup_time;
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

	public int getSeen_total() {
		return seen_total;
	}

	public void setSeen_total(int seen_total) {
		this.seen_total = seen_total;
	}

	public int getFavs_total() {
		return favs_total;
	}

	public void setFavs_total(int favs_total) {
		this.favs_total = favs_total;
	}

	public int getLater_total() {
		return later_total;
	}

	public void setLater_total(int later_total) {
		this.later_total = later_total;
	}

	public Long getSignup_time() {
		return signup_time;
	}

	public void setSignup_time(Long signup_time) {
		this.signup_time = signup_time;
	}

	@Override
	public String toString() {
		return "UserStat [id=" + id + ", username=" + username + ", seen_total=" + seen_total + ", favs_total="
				+ favs_total + ", later_total=" + later_total + ", signup_time=" + signup_time + "]";
	}


}
