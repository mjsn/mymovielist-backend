package me.mjsn.mymovielist;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import me.mjsn.mymovielist.domain.User;
import me.mjsn.mymovielist.web.UserDetailServiceImpl;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private UserDetailServiceImpl userDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.httpBasic()
		.and()
		.authorizeRequests()
		.antMatchers(HttpMethod.POST, "/api/users").permitAll()
		.and()
		.authorizeRequests()
		.antMatchers(HttpMethod.PUT, "/api/users/**").hasAuthority("ROLE_ADMIN")
		.and()
		.authorizeRequests()
		.antMatchers(HttpMethod.PATCH, "/api/users/**").hasAuthority("ROLE_ADMIN")
		.and()
		.authorizeRequests()
		.antMatchers("/api/user").permitAll()
		.and()
		.authorizeRequests()
		.anyRequest().authenticated()
		.and()
		.logout()
		.logoutUrl("/api/logout")
		.permitAll()
		.and()
		.cors()
		.and()
		.csrf().disable();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("https://mml.mjsn.me"));
		configuration.setAllowedMethods(Arrays.asList("GET","POST","DELETE","PUT","OPTIONS","PATCH"));
		configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "xsrf-token", "www-authenticate", "accept", "X-Requested-With"));
		configuration.setExposedHeaders(Arrays.asList("xsrf-token"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.userDetailsService).passwordEncoder(User.PASSWORD_ENCODER);
	}
}