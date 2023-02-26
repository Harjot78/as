package org.example.controller;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.example.model.Audio;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiResponse;
//import io.swagger.annotations.ApiResponses;



@WebServlet(name = "audio", value = "audio")


public class ResourceServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    /*
     * ConcurrentHashMap is thread safe; 
     */
    ConcurrentHashMap<String, Audio> audioDB = new ConcurrentHashMap<>();
    
    /*
     * simply emulation of in memory database;  
     */
    private static int totalCopiesSold = 0; // New variable to store the total number of copies sold for all audios
    //private HashMap<String, Audio> audioDB = new HashMap<>();

    @Override
    public void init() throws ServletException {
        Audio artist1 = new Audio();
        artist1.setId("id_1");
        artist1.setArtistName("artist_name_1");
        artist1.setTrackTitle("track_title_1");
        artist1.setAlbumTitle("album_title_1");
        artist1.setTrackNumber(1);
        artist1.setYear(2021);
        artist1.setNumberOfReviews(10);
        artist1.setNumofCopiesSold(10000);

        audioDB.put("id_1", artist1);

        Audio artist2 = new Audio();
        artist2.setId("id_2");
        artist2.setArtistName("artist_name_2");
        artist2.setTrackTitle("track_title_2");
        artist2.setAlbumTitle("album_title_2");
        artist2.setTrackNumber(2);
        artist2.setYear(2022);
        artist2.setNumberOfReviews(20);
        artist2.setNumofCopiesSold(20000);

        audioDB.put("id_2", artist2);

        Audio artist3 = new Audio();
        artist3.setId("id_3");
        artist3.setArtistName("artist_name_3");
        artist3.setTrackTitle("track_title_3");
        artist3.setAlbumTitle("album_title_3");
        artist3.setTrackNumber(3);
        artist3.setYear(2023);
        artist3.setNumberOfReviews(30);
        artist3.setNumofCopiesSold(30000);

        audioDB.put("id_3", artist3);

        // Update the total number of copies sold
        totalCopiesSold += artist1.getNumofCopiesSold() + artist2.getNumofCopiesSold() + artist3.getNumofCopiesSold();
    }
    
//    @ApiOperation(httpMethod = "GET", value = "Resource to get Audio", nickname = "getAudio")
//    @ApiResponses({@ApiResponse(code = 500, message = "Invalid input")})
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "artistName", value = "Artist Name", required = false, dataType = "string", paramType =
//                    "query"),
//            @ApiImplicitParam(name = "trackTitle", value = "Track Title", required = false, dataType = "string", paramType =
//                    "query"),
//            @ApiImplicitParam(name = "albumTitle", value = "Album Title", required = false, dataType = "string", paramType
//                    = "query")})
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String artistName = request.getParameter("artistName");
        String trackTitle = request.getParameter("trackTitle");
        String albumTitle = request.getParameter("albumTitle");
        Integer trackNumber = null;
        if (request.getParameter("trackNumber") != null) {
            trackNumber = Integer.parseInt(request.getParameter("trackNumber"));
        }
        Integer year = null;
        if (request.getParameter("year") != null) {
            year = Integer.parseInt(request.getParameter("year"));
        }
        Integer numberOfReviews = null;
        if (request.getParameter("numberOfReviews") != null) {
            numberOfReviews = Integer.parseInt(request.getParameter("numberOfReviews"));
        }
        Integer numofCopiesSold = null;
        if (request.getParameter("numofCopiesSold") != null) {
            numofCopiesSold = Integer.parseInt(request.getParameter("numofCopiesSold"));
        }

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (artistName == null && trackTitle == null && albumTitle == null && trackNumber == null && year == null && numberOfReviews == null && numofCopiesSold == null) {
            // if no parameter is provided, return all records in audioDB
            Gson gson = new Gson();
            JsonElement element = gson.toJsonTree(audioDB.values());
            out.println("GET RESPONSE IN JSON - all elements: " + element.toString());
        } else {
            // search for the record with any matching parameter
            Audio matchingAudio = null;
            for (Audio audio : audioDB.values()) {
                if ((artistName != null && audio.getArtistName().equals(artistName)) ||
                    (trackTitle != null && audio.getTrackTitle().equals(trackTitle)) ||
                    (albumTitle != null && audio.getAlbumTitle().equals(albumTitle)) ||
                    (trackNumber != null && audio.getTrackNumber()==trackNumber) ||
                    (year != null && audio.getYear()==year) ||
                    (numberOfReviews != null && audio.getNumberOfReviews()==numberOfReviews) ||
                    (numofCopiesSold != null && audio.getNumofCopiesSold()==(numofCopiesSold))) {
                    matchingAudio = audio;
                    break;
                }
            }
            if (matchingAudio != null) {
                Gson gson = new Gson();
                out.println("GET RESPONSE IN JSON - single element: " + gson.toJson(matchingAudio));
            } else {
            	response.setStatus(404);
                out.println("ERROR: Audio not found." + "artist" + artistName);
            }
        }
        out.println("GET RESPONSE: Total number of copies sold is "+ totalCopiesSold +".");
        out.flush();
    }

    
//    
//    @ApiOperation(httpMethod = "POST", value = "Resource create Audio", nickname = "createAudio")
//    @ApiResponses({@ApiResponse(code = 500, message = "Invalid input")})
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "artistName", value = "Artist Name", required = false, dataType = "string", paramType =
//                    "query"),
//            @ApiImplicitParam(name = "trackTitle", value = "Track Title", required = false, dataType = "string", paramType =
//                    "query"),
//            @ApiImplicitParam(name = "albumTitle", value = "Album Title", required = false, dataType = "string", paramType
//                    = "query"),
//            @ApiImplicitParam(name = "trackNumber", value = "Track Number", required = false, dataType = "string", paramType
//            = "query"),
//            @ApiImplicitParam(name = "year", value = "Year", required = false, dataType = "string", paramType
//            = "query"),
//            @ApiImplicitParam(name = "numberOfReviews", value = "Number of Reviews", required = false, dataType = "string", paramType
//            = "query"),
//            @ApiImplicitParam(name = "numofCopiesSold", value = "Number of copies solved", required = false, dataType = "string", paramType
//            = "query")})
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String artistName = request.getParameter("artistName");
    String trackTitle = request.getParameter("trackTitle");
    String albumTitle = request.getParameter("albumTitle");
    String trackNumberStr = request.getParameter("trackNumber");
    String yearStr = request.getParameter("year");
    String numberOfReviewsStr = request.getParameter("numberOfReviews");
    String numofCopiesSoldStr = request.getParameter("numofCopiesSold");
    
    int trackNumber = Integer.parseInt(trackNumberStr);
    int year = Integer.parseInt(yearStr);
    int numberOfReviews = Integer.parseInt(numberOfReviewsStr);
    int numofCopiesSold = Integer.parseInt(numofCopiesSoldStr);

    String id = UUID.randomUUID().toString();
    Audio art = new Audio();
    art.setId(id);
    art.setArtistName(artistName);
    art.setTrackTitle(trackTitle);
    art.setAlbumTitle(albumTitle);
    art.setTrackNumber(trackNumber);
    art.setYear(year);
    art.setNumberOfReviews(numberOfReviews);
    art.setNumofCopiesSold(numofCopiesSold);

    audioDB.put(id, art);
    totalCopiesSold += numofCopiesSold; // Update the total number of copies sold
    
    	response.setStatus(200);
	
    	response.getOutputStream().println("POST RESPONSE: Artist_Name with " + artistName + " and number of copies sold "+ numofCopiesSold +" is added to the database.");
    	//out.println("POST RESPONSE: Audio item with " + artistName + " and number of copies sold "+ numofCopiesSold +" is added to the database.");
    	response.getOutputStream().println("POST RESPONSE: Total number of copies sold is updated to "+ totalCopiesSold +".");

    }
 }
