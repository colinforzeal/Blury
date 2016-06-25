package activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.colinforzeal.blury.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import network.YouTubeCaller;
import objects.Track;
import utils.Constants;
import utils.Utils;

public class TrackActivity extends YouTubeBaseActivity{
    private static final String EXTRA_TRACK = "EXTRA_TRACK";

    private static ImageView sBackground;
    private static RelativeLayout sContainer;
    private static ImageView sIcon;
    private static TextView sTrackName;
    private static TextView sArtistName;
    private static YouTubePlayerView sYouTubeView;
    private static YouTubePlayer sPlayer;

    private Track mTrack;
    private int mContainerColor;

    public static Intent getStartIntent(Context context, Track track){
        Intent intent = new Intent(context, TrackActivity.class);
        intent.putExtra(EXTRA_TRACK, track);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        Intent intent = getIntent();
        mTrack = intent.getParcelableExtra(EXTRA_TRACK);
        mContainerColor = mTrack.getColor();

        sBackground = (ImageView) findViewById(R.id.background_track_activity);
        sContainer = (RelativeLayout) findViewById(R.id.info_container_track_activity);
        sIcon = (ImageView) findViewById(R.id.icon_track_activity);
        sTrackName = (TextView) findViewById(R.id.track_track_activity);
        sArtistName = (TextView) findViewById(R.id.artist_track_activity);
        sYouTubeView = (YouTubePlayerView) findViewById(R.id.youtube_track_activity);

        setViewsOperation();
    }


    private void setViewsOperation(){
        sBackground.post(new Runnable() {
            @Override
            public void run() {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                final int screenWidth = size.x;
                final int screenHeight = size.y;

                final Bitmap backgroundBitmap =
                        Utils.createBackgroundBitmap(mTrack.getImage(), screenWidth, screenHeight);
                sBackground.setImageBitmap(backgroundBitmap);
            }
        });

        sIcon.post(new Runnable() {
            @Override
            public void run() {
                sIcon.setImageBitmap(mTrack.getIcon());
            }
        });

        sContainer.setBackgroundColor(mContainerColor);

        sTrackName.setText(mTrack.getName());
        sArtistName.setText(mTrack.getArtist());

        sYouTubeView.initialize(Constants.YOUTUBE_API_TOKEN, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean restored) {
                sPlayer = youTubePlayer;

                sPlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
                sPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
                sPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);

                new MyAsyncTask().execute(mTrack.getName(), mTrack.getArtist());
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
            }
        });
    }

    private static class MyAsyncTask extends AsyncTask<String,Void,String>{
        private String videoId;

        @Override
        protected String doInBackground(String... params) {
            String trackName = params[0];
            String artistName = params[1];

            //Gets id of first video from search
            YouTubeCaller caller = new YouTubeCaller(trackName, artistName);
            videoId = caller.getVideoId();

            return "OK";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            sPlayer.cueVideo(videoId);
        }
    }



}
