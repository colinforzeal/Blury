package fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.colinforzeal.blury.R;

import java.net.UnknownHostException;
import java.util.ArrayList;

import network.LastFmCaller;
import utils.NetworkConnector;
import objects.Track;

public class LoadingFragment extends Fragment{
    private static final String BUNDLE_TRACK_NAME = "BUNDLE_TRACK_NAME";
    private static final String BUNDLE_ARTIST_NAME = "BUNDLE_ARTIST_NAME";

    private static TextView sErrorMessage;

    private String mTrackName;
    private String mArtistName;

    private ArrayList<Track> mTracks;
    private Track mTrack;

    private OnFragmentInteractionListener mListener;

    public static LoadingFragment newInstance(String trackName, String artistName) {
        LoadingFragment fragment = new LoadingFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_TRACK_NAME, trackName);
        args.putString(BUNDLE_ARTIST_NAME, artistName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            mTrackName = getArguments().getString(BUNDLE_TRACK_NAME);
            mArtistName = getArguments().getString(BUNDLE_ARTIST_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        sErrorMessage = (TextView) view.findViewById(R.id.error_message_loading_activity);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (NetworkConnector.isConnected()){
            new MyAsyncTask(getActivity()).execute();
        }
        else{
            sErrorMessage.setText(R.string.error_no_connection);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Track track, ArrayList<Track> tracks);
    }

    /* Asynchronious operations:
       Network connecting, ProgressDialog handling
     */
    private class MyAsyncTask extends AsyncTask<String,Integer,String> {
        private final static String TAG_NO_CONNECTION = "TAG_NO_CONNECTION";
        private final static String TAG_LIST_IS_EMPTY = "TAG_LIST_IS_EMPTY";

        private Context context;

        private ProgressDialog progress;
        private int progressStatus;

        public MyAsyncTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = new ProgressDialog(context);

            progress.setIndeterminate(true);
            progress.setMessage("Searching for similar tracks..");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setProgress(0);
            progress.setMax(50);
            progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });

            progress.show();

            progressStatus = 0;
        }

        @Override
        protected String doInBackground(String... params) {
            /*
                Recieves data from lastfm database
             */
            LastFmCaller lastFmCaller = new LastFmCaller(mTrackName, mArtistName);
            lastFmCaller.createListOfSimilarTracks();

            if (!lastFmCaller.isListEmpty()){
                //Getting track info
                mTrack = lastFmCaller.getTrackInfo();

                //Filling list of similar tracks
                mTracks = new ArrayList<>();
                for (int i = 0; i < 50; i++){
                    try{
                        mTracks.add(lastFmCaller.getCurrentSimilarTrack(i));
                    }
                    catch (UnknownHostException e){
                        Log.e(e.getClass().getName(), "while getting list of similar tracks");

                        mTracks = null;
                        cancel(true);
                        return TAG_NO_CONNECTION;
                    }

                    progressStatus = i;
                    publishProgress(progressStatus);

                    try{
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

                return "OK";
            }
            else{
                cancel(true);
                return TAG_LIST_IS_EMPTY;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progress.setIndeterminate(false);
            progress.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            if (mTracks == null || mTracks.isEmpty()){
                sErrorMessage.setText(R.string.error_empty_list);
            }
            else{
                //Pass arguments to fragment, that show tracklist
                mListener.onFragmentInteraction(mTrack, mTracks);
            }

            progress.dismiss();
        }

        @Override
        protected void onCancelled(String s){
            //Check for errors and show them
            switch (s){
                case TAG_NO_CONNECTION:{
                    sErrorMessage.setText(R.string.error_no_connection);
                    break;
                }
                case TAG_LIST_IS_EMPTY:{
                    sErrorMessage.setText(R.string.error_empty_list);
                    break;
                }
                default:{
                    break;
                }
            }

            progress.dismiss();
        }
    }

}
