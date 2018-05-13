package me.mjsn.mymovielist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Properties;

import org.bson.Document;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;

import static com.mongodb.client.model.Filters.*;

// For handling TMDb API calls
@Component("tmdbApi")
public class TmdbApi {

	// Loads properties from application.properties
	public Properties loadProperties() throws IOException {
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream("/application.properties"));
		return properties;
	}

	// Fetches movie information from TMDb and saves it to MongoDB
	public String tmdbFetch(int tmdbId, boolean update) throws IOException {

		// Read properties
		Properties properties = loadProperties();

		// API URL where movie info (general info, images and credits) is fetched from
		String tmdbApiUrl = "https://api.themoviedb.org/3/movie/" + tmdbId + "?api_key=" + 
				properties.getProperty("config.tmdbApiKey") + 
				"&append_to_response=credits";

		// Max age of info in days. If this has exceeded, info will be updated, otherwise nothing is done.
		int maxDays = 14;
		// Convert days to seconds
		Long maxSeconds = maxDays * 24 * 60 * 60L;

		// For testing, make the data expire in 10 seconds
		// maxSeconds = 10L;

		// Current unixtime
		long unixTime = Instant.now().getEpochSecond();

		// Connect to MongoDB, select database and collection
		MongoClient mongoClient = new MongoClient(properties.getProperty("config.mongoHost"), 
				Integer.parseInt(properties.getProperty("config.mongoPort")));
		MongoDatabase database = mongoClient.getDatabase(properties.getProperty("config.mongoDatabase"));
		MongoCollection<Document> collection = database.getCollection("movies");

		// Try to find a document from MongoDB where the tmdbId matches
		Document document = new Document();
		document = collection.find(eq("id", tmdbId)).first();

		// If no documents found or the information is too old and update is set to true, fetch the info from TMDb and save it to MongoDB
		if((document == null || document.getLong("last_updated") < unixTime-maxSeconds) && update == true) {

			// Fetch info from tmdbApiUrl and make a MongoDB Document out of it
			URL url = new URL(tmdbApiUrl);
			URLConnection con = url.openConnection();

			InputStreamReader isr = new InputStreamReader((InputStream) con.getContent(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuffer apiResponse = new StringBuffer();

			int c;
			while((c=br.read())!=-1) {
				apiResponse.append((char)c);
			}

			Document tmdbDocument = Document.parse(apiResponse.toString());

			// Append current time to the document
			tmdbDocument.append("last_updated", unixTime);

			// Save document to MongoDB
			document = collection.findOneAndReplace(eq("id", tmdbId), tmdbDocument, new FindOneAndReplaceOptions()
					.upsert(true)
					.returnDocument(ReturnDocument.AFTER));
		}

		// Close MongoDB connection
		mongoClient.close();

		// Return the document as a JSON string
		return document.toJson();

	}

	// Get already fetched movies' info from MongoDB with array of TMDb ids
	public String tmdbListFetched(ArrayList<Integer> tmdbIdList) throws IOException {

		BasicDBList or = new BasicDBList();
		for (Integer tmdbId : tmdbIdList)
			or.add(new BasicDBObject("id", tmdbId));		

		// Read properties
		Properties properties = loadProperties();

		// Connect to MongoDB, select database and collection
		MongoClient mongoClient = new MongoClient(properties.getProperty("config.mongoHost"), 
				Integer.parseInt(properties.getProperty("config.mongoPort")));
		MongoDatabase database = mongoClient.getDatabase(properties.getProperty("config.mongoDatabase"));
		MongoCollection<Document> collection = database.getCollection("movies");

		// Try to find a documents from MongoDB where the tmdbId matches
		MongoCursor<Document> cursor = collection.find(new BasicDBObject("$or", or)).iterator();

		ArrayList<String> docs = new ArrayList<String>();

		try {
			while (cursor.hasNext()) {
				docs.add(cursor.next().toJson());
			}
		} finally {
			cursor.close();
		}

		// Close MongoDB connection
		mongoClient.close();

		// Return a JSON string
		return "[" + String.join(", ", docs) + "]";


	}


	// Function for checking if MongoDB has info of a movie with tmdbId
	public boolean tmdbIdExists(int tmdbId) throws IOException {

		// Read configuration
		Properties properties = loadProperties();

		// Connect to MongoDB, select database and collection
		MongoClient mongoClient = new MongoClient(properties.getProperty("config.mongoHost"), 
				Integer.parseInt(properties.getProperty("config.mongoPort")));
		MongoDatabase database = mongoClient.getDatabase(properties.getProperty("config.mongoDatabase"));
		MongoCollection<Document> collection = database.getCollection("movies");

		// Try to find a document from MongoDB where the tmdbId matches
		Document document = new Document();
		document = collection.find(eq("id", tmdbId)).first();
		mongoClient.close();

		// If document is found, return true, otherwise return false
		if(document != null) {
			return true;
		} else {
			return false;
		}
	}

	// Fetches a list of movies from TMDb ordered by release date desc/asc
	public String tmdbList(int page, String order, String genres) throws IOException {

		if(!order.equals("asc")) {
			order = "desc";
		}

		// TMDb API limits page number to 1000
		if(page > 1000 || page < 1) {
			page = 1;
		}

		// Read properties
		Properties properties = loadProperties();

		// API URL where movie info (general info, images and credits) is fetched from
		String tmdbApiUrl = "https://api.themoviedb.org/3/discover/movie?api_key=" + 
				properties.getProperty("config.tmdbApiKey") + 
				"&page=" + page +
				"&sort_by=popularity." + order +
				"&with_genres=" + genres;

		// Max age of info in minutes. If this has exceeded, info will be updated, otherwise nothing is done.
		int maxMinutes = 10;
		// Convert days to seconds
		Long maxSeconds = maxMinutes * 60L;

		// For testing, make the data expire in 10 seconds
		// maxSeconds = 1L;

		// Current unixtime
		long unixTime = Instant.now().getEpochSecond();

		// Connect to MongoDB, select database and collection
		MongoClient mongoClient = new MongoClient(properties.getProperty("config.mongoHost"), 
				Integer.parseInt(properties.getProperty("config.mongoPort")));
		MongoDatabase database = mongoClient.getDatabase(properties.getProperty("config.mongoDatabase"));
		MongoCollection<Document> collection = database.getCollection("moviepages");

		// Try to find a document from MongoDB where the tmdbId matches
		Document document = new Document();
		document = collection.find(and(eq("page", page), eq("order", order), eq("genres", genres))).first();

		// If no documents found or the information is too old and update is set to true, fetch the info from TMDb and save it to MongoDB
		if((document == null || document.getLong("last_updated") < unixTime-maxSeconds)) {

			// Fetch info from tmdbApiUrl and make a MongoDB Document out of it
			URL url = new URL(tmdbApiUrl);
			URLConnection con = url.openConnection();

			InputStreamReader isr = new InputStreamReader((InputStream) con.getContent(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuffer apiResponse = new StringBuffer();

			int c;
			while((c=br.read())!=-1) {
				apiResponse.append((char)c);
			}

			Document tmdbDocument = Document.parse(apiResponse.toString());

			// Append current time and order to the document
			tmdbDocument.append("last_updated", unixTime);
			tmdbDocument.append("order", order);
			tmdbDocument.append("genres", genres);


			// Save document to MongoDB
			document = collection.findOneAndReplace(and(eq("page", page), eq("order", order), eq("genres", genres)), tmdbDocument, new FindOneAndReplaceOptions()
					.upsert(true)
					.returnDocument(ReturnDocument.AFTER));
		}

		// Close MongoDB connection
		mongoClient.close();

		// Return the document as a JSON string
		return document.toJson();

	}

	// Fetches search results from TMDb
	public String tmdbSearch(int page, String query) throws IOException {

		// TMDb API limits page number to 1000
		if(page > 1000 || page < 1) {
			page = 1;
		}

		// URL encode the query
		query = URLEncoder.encode(query, "UTF-8");

		// Read properties
		Properties properties = loadProperties();

		// API URL where movie info (general info, images and credits) is fetched from
		String tmdbApiUrl = "https://api.themoviedb.org/3/search/movie?api_key=" + 
				properties.getProperty("config.tmdbApiKey") + 
				"&page=" + page +
				"&query=" + query;

		// Max age of info in minutes. If this has exceeded, info will be updated, otherwise nothing is done.
		int maxMinutes = 10;
		// Convert days to seconds
		Long maxSeconds = maxMinutes * 60L;

		// For testing, make the data expire in 1 second
		// maxSeconds = 1L;

		// Current unixtime
		long unixTime = Instant.now().getEpochSecond();

		// Connect to MongoDB, select database and collection
		MongoClient mongoClient = new MongoClient(properties.getProperty("config.mongoHost"), 
				Integer.parseInt(properties.getProperty("config.mongoPort")));
		MongoDatabase database = mongoClient.getDatabase(properties.getProperty("config.mongoDatabase"));
		MongoCollection<Document> collection = database.getCollection("moviesearch");

		// Try to find a document from MongoDB where the tmdbId matches
		Document document = new Document();
		document = collection.find(and(eq("page", page), eq("query", query))).first();

		// If no documents found or the information is too old and update is set to true, fetch the info from TMDb and save it to MongoDB
		if((document == null || document.getLong("last_updated") < unixTime-maxSeconds)) {

			// Fetch info from tmdbApiUrl and make a MongoDB Document out of it
			URL url = new URL(tmdbApiUrl);
			URLConnection con = url.openConnection();

			InputStreamReader isr = new InputStreamReader((InputStream) con.getContent(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuffer apiResponse = new StringBuffer();

			int c;
			while((c=br.read())!=-1) {
				apiResponse.append((char)c);
			}

			Document tmdbDocument = Document.parse(apiResponse.toString());

			// Append current time and order to the document
			tmdbDocument.append("last_updated", unixTime);
			tmdbDocument.append("query", query);

			// Save document to MongoDB
			document = collection.findOneAndReplace(and(eq("page", page), eq("query", query)), tmdbDocument, new FindOneAndReplaceOptions()
					.upsert(true)
					.returnDocument(ReturnDocument.AFTER));
		}

		// Close MongoDB connection
		mongoClient.close();

		// Return the document as a JSON string
		return document.toJson();

	}



}
