package activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.colinforzeal.blury.R;

import java.util.ArrayList;

import fragments.LoadingFragment;
import fragments.TrackListFragment;

import objects.Track;

public class TrackListActivity extends AppCompatActivity implements LoadingFragment.OnFragmentInteractionListener {
    private final static String EXTRA_TRACK_NAME = "EXTRA_TRACK_NAME";
    private final static String EXTRA_ARTIST_NAME = "EXTRA_ARTIST_NAME";
    private final static String ARGUMENT_TRACKLIST = "ARGUMENT_TRACKLIST";

    private static TrackListFragment sTrackListFragment;

    public static Intent getStartIntent(Context context, String trackName, String artistName){
        Intent intent = new Intent(context, TrackListActivity.class);
        intent.putExtra(EXTRA_TRACK_NAME, trackName);
        intent.putExtra(EXTRA_ARTIST_NAME, artistName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracklist);

        if (savedInstanceState != null){
            sTrackListFragment = (TrackListFragment) getSupportFragmentManager()
                                    .getFragment(savedInstanceState, ARGUMENT_TRACKLIST);
        }
        else{
            Intent intent = getIntent();
            String trackName = intent.getStringExtra(EXTRA_TRACK_NAME);
            String artistName = intent.getStringExtra(EXTRA_ARTIST_NAME);

            LoadingFragment loadingFragment = LoadingFragment.newInstance(trackName, artistName);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container_tracklist_activity, loadingFragment);
            transaction.commit();
        }

    }


    /* After finishing of loading content
       We display it.
     */
    @Override
    public void onFragmentInteraction(Track track, ArrayList<Track> tracks) {
        sTrackListFragment = TrackListFragment.newInstance(track, tracks);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.container_tracklist_activity, sTrackListFragment);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (sTrackListFragment != null){
            getSupportFragmentManager().putFragment(outState, ARGUMENT_TRACKLIST, sTrackListFragment);
        }
    }
}
