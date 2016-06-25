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
import java.net.UnknownHostException;
import java.util.Iterator;

import objects.Track;
import utils.Constants;
import utils.NetworkConnector;
import utils.Utils;

public class LastFmCaller {
    private final static JSONParser sParser;

    private final String mArtist;
    private final String mTrack;
    private JSONArray mTracksInJSON;
    private boolean mListIsEmpty;

    public LastFmCaller(String track, String artist){
        mTrack = track;
        mArtist = artist;
        mListIsEmpty = false;
    }

    static {
        sParser = new JSONParser();
    }

    public boolean isListEmpty(){return mListIsEmpty;}

    public void createListOfSimilarTracks(){
        InputStream input = null;

        try{
            String url = createGetSimilarTracksUrl();
            input = NetworkConnector.getInputStreamFromHttp(url);
        }
        catch (IOException e){
            Log.e(e.getClass().getName(), "Failed to set connection.");
        }

        if (input != null){
            String response = Utils.readInputStream(input);
            parseJsonToSimilarTracks(response);
        }

        if (mTracksInJSON == null || mTracksInJSON.isEmpty()){
            mListIsEmpty = true;
        }
    }

    public Track getCurrentSimilarTrack(int i) throws UnknownHostException{
        Track track = new Track();

        JSONObject currentTrack = (JSONObject) mTracksInJSON.get(i);

        //Setting mTrack's name
        String trackName = (String) currentTrack.get("name");
        track.setName(trackName);

        //Setting mTrack's mArtist
        String artistName = (String) ((JSONObject) currentTrack.get("artist")).get("name");
        track.setArtist(artistName);

        //Setting mTrack's cover
        JSONArray covers = (JSONArray) currentTrack.get("image");
        if (covers != null && !covers.isEmpty()){
            JSONObject cover;
            String coverUrl = null;
            Iterator iterator = covers.iterator();
            while (iterator.hasNext()){
                cover = (JSONObject) iterator.next();
                if (cover.get("size").equals("extralarge")){
                    coverUrl = (String) cover.get("#text");
                    cover = null;
                    break;
                }
            }

            //May contain empty cover url
            if (coverUrl != null && !coverUrl.equals("")){
                try{
                    track.setImage(coverUrl);
                }
                catch (IOException e){
                    Log.e(e.getClass().getName(), "In LastFmCaller");
                    throw new UnknownHostException();
                }
            }
            else{
                track.setDefaultImage();
            }
        }

        return track;
    }

    public Track getTrackInfo(){
        InputStream input = null;

        try{
            String url = createGetTrackInfoUrl();
            input = NetworkConnector.getInputStreamFromHttp(url);
        }
        catch (IOException e){
            Log.e(e.getClass().getName(), "Failed to set connection");
        }

        Track track = null;
        if (input != null){
            String response = Utils.readInputStream(input);

            try{
                track = parseJsonToTrackInfo(response);
            }
            catch (UnknownHostException e){
                Log.e(e.getClass().getName(), "In getTrackInfo");
            }
        }

        return track;
    }

    private String createGetSimilarTracksUrl(){
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://ws.audioscrobbler.com/2.0/");
        urlString.append("?method=");
        urlString.append("track.getSimilar");
        urlString.append("&");

        try{
            urlString.append("artist=");
            String artistEncoded = URLEncoder.encode(mArtist, "UTF-8");
            urlString.append(artistEncoded);

            urlString.append("&");

            urlString.append("track=");
            String trackEncoded = URLEncoder.encode(mTrack, "UTF-8");
            urlString.append(trackEncoded);
        }
        catch (UnsupportedEncodingException e){
            Log.e(e.getClass().getName(), "In LastFmCaller");
        }

        urlString.append("&");
        urlString.append("autocorrect=1");
        urlString.append("&");
        urlString.append("limit=50");
        urlString.append("&");
        urlString.append("api_key=");
        urlString.append(Constants.LASTFM_API_TOKEN);
        urlString.append("&");
        urlString.append("format=json");
        return urlString.toString();
    }

    private String createGetTrackInfoUrl(){
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://ws.audioscrobbler.com/2.0/");
        urlString.append("?method=");
        urlString.append("track.getInfo");
        urlString.append("&");

        try{
            urlString.append("artist=");
            String artistEncoded = URLEncoder.encode(mArtist, "UTF-8");
            urlString.append(artistEncoded);

            urlString.append("&");

            urlString.append("track=");
            String trackEncoded = URLEncoder.encode(mTrack, "UTF-8");
            urlString.append(trackEncoded);
        }
        catch (UnsupportedEncodingException e){
            Log.e(e.getClass().getName(), "In LastFmCaller");
        }

        urlString.append("&");
        urlString.append("autocorrect=1");
        urlString.append("&");
        urlString.append("api_key=");
        urlString.append(Constants.LASTFM_API_TOKEN);
        urlString.append("&");
        urlString.append("format=json");
        return urlString.toString();
    }

    private void parseJsonToSimilarTracks(String data){
        if (data == null){
            return;
        }

        try {
            JSONObject jsonObject = (JSONObject) sParser.parse(data);

            if (!jsonObject.toJSONString().contains("error")){
                //Fill list of similar tracks
                mTracksInJSON = (JSONArray) ((JSONObject) jsonObject.get("similartracks")).get("track");
            }
        }
        catch (ParseException e){
            Log.e(e.getClass().getName(), "Failed to parse data.");
        }
    }

    private Track parseJsonToTrackInfo(String data) throws UnknownHostException{
        if (data == null){
            return null;
        }

        Track track = null;
        try{
            JSONObject jsonObject = (JSONObject) sParser.parse(data);

            if (jsonObject.toJSONString().contains("error")){
                return null;
            }
            else{
                track = new Track();

                JSONObject rootObject = (JSONObject) jsonObject.get("track");

                //Getting name of mTrack
                String trackName = (String) rootObject.get("name");
                track.setName(trackName);

                //Getting name of mArtist
                String artistName = (String) ((JSONObject) rootObject.get("artist")).get("name");
                track.setArtist(artistName);

                //Getting list of album covers
                JSONArray covers = (JSONArray) ((JSONObject) rootObject.get("album")).get("image");

                /*
                 * If the're no album, we can't download cover
                 * So we set default icon
                 */
                if (covers != null && !covers.isEmpty()){
                    //Getting url of the largest cover
                    JSONObject cover;
                    String coverUrl = null;
                    Iterator iterator = covers.iterator();
                    while (iterator.hasNext()){
                        cover = (JSONObject) iterator.next();
                        if (cover.get("size").equals("extralarge")){
                            coverUrl = (String) cover.get("#text");
                            cover = null;
                            break;
                        }
                    }

                    //May contain empty cover url
                    if (coverUrl != null && !coverUrl.equals("")){
                        try{
                            track.setImage(coverUrl);
                        }
                        catch (IOException e){
                            Log.e(e.getClass().getName(), "In LastFmCaller");
                            throw new UnknownHostException();
                        }
                    }
                    else{
                        track.setDefaultImage();
                    }
                }
                else{
                    track.setDefaultImage();
                }

            }
        }
        catch (ParseException e){
            Log.e(e.getClass().getName(), "In LastFmCaller.");
        }

        return track;
    }
}
