package network;

import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import utils.Constants;
import utils.NetworkConnector;
import utils.Utils;

public class YouTubeCaller {
    private final static JSONParser sParser;

    private final String mTrackName;
    private final String mArtistName;

    public YouTubeCaller(String track, String artist){
        this.mTrackName = track;
        this.mArtistName = artist;
    }

    static {
        sParser = new JSONParser();
    }

    public String getVideoId(){
        String response = null;
        InputStream inputStream = null;
        try{
            String url = createVideoIdUrl();
            inputStream = NetworkConnector.getInputStreamFromHttps(url);
        }
        catch (IOException e){
            Log.e(e.getClass().getName(), "In YouTubeCaller");
        }

        if (inputStream != null){
            response = Utils.readInputStream(inputStream);
        }

        return parseJSONToString(response);
    }

    private String createVideoIdUrl(){
        StringBuilder builder = new StringBuilder();
        builder.append("https://www.googleapis.com/youtube/v3/search");
        builder.append("?");
        builder.append("part=");
        builder.append("snippet");
        builder.append("&");
        builder.append("q=");

        try{
            String trackNameEncoded = URLEncoder.encode(mTrackName, "UTF-8");
            builder.append(trackNameEncoded);

            builder.append("+");

            String artistNameEncoded = URLEncoder.encode(mArtistName, "UTF-8");
            builder.append(artistNameEncoded);
        }
        catch (UnsupportedEncodingException e){
            Log.e(e.getClass().getName(), "In YouTubeCaller");
        }
        builder.append("&");
        builder.append("type=");
        builder.append("video");
        builder.append("&");
        builder.append("maxResults=");
        builder.append("1");
        builder.append("&");
        builder.append("key=");
        builder.append(Constants.YOUTUBE_API_TOKEN);
        return builder.toString();
    }

    private String parseJSONToString(String response){
        if (response == null){
            return null;
        }

        String result = null;
        try{
            JSONObject jsonObject = (JSONObject) sParser.parse(response);

            if (jsonObject.toJSONString().contains("error")){
                return null;
            }
            else{
                //Getting id of first item
                JSONObject firstItem = (JSONObject) ((JSONArray) jsonObject.get("items")).get(0);
                result = (String) ((JSONObject) firstItem.get("id")).get("videoId");
            }
        }
        catch (ParseException e){
            Log.e(e.getClass().getName(), "In YouTubeCaller");
        }

        return result;
    }
}
