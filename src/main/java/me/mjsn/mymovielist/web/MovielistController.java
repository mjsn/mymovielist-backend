package me.mjsn.mymovielist.web;


import java.io.IOException;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import me.mjsn.mymovielist.TmdbApi;



@Controller
public class MovielistController {

	// Fetch and display info from TMDb by id
	@RequestMapping(value="/api/tmdb/movie/{id}", method = RequestMethod.GET, produces={"application/json"})
	@ResponseBody
	public String tmdbFetch(@PathVariable("id") int tmdbId, 
			@RequestParam(value = "update", required = false, defaultValue = "true") boolean update) throws IOException {
		TmdbApi tmdbApi = new TmdbApi();

		return tmdbApi.tmdbFetch(tmdbId, update);
	}

	// Get already fetched movies' info from MongoDB with array of TMDb ids
	@RequestMapping(value="/api/tmdb/fetched", method = RequestMethod.POST, produces={"application/json"})
	@ResponseBody
	public String tmdbListFetched(@RequestBody ArrayList<Integer> tmdbIdList) throws IOException {
		TmdbApi tmdbApi = new TmdbApi();

		return tmdbApi.tmdbListFetched(tmdbIdList);
	}

	// Fetch list of movies from TMDb
	@RequestMapping(value="/api/tmdb/movies", method = RequestMethod.GET, produces={"application/json"})
	@ResponseBody
	public String tmdbList(@RequestParam(value = "page", required = false, defaultValue = "1") int page, 
			@RequestParam(value = "order", required = false, defaultValue = "desc") String order,
			@RequestParam(value = "genres", required = false, defaultValue = "") String genres) throws IOException {
		TmdbApi tmdbApi = new TmdbApi();

		return tmdbApi.tmdbList(page, order, genres);
	}

	// Search movies from TMDb
	@RequestMapping(value="/api/tmdb/search", method = RequestMethod.GET, produces={"application/json"})
	@ResponseBody
	public String tmdbSearch(@RequestParam(value = "page", required = false, defaultValue = "1") int page, 
			@RequestParam(value = "query", required = true) String query) throws IOException {
		TmdbApi tmdbApi = new TmdbApi();

		return tmdbApi.tmdbSearch(page, query);
	}
}