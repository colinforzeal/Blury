package adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.colinforzeal.blury.R;

import activities.TrackActivity;
import activities.TrackListActivity;
import objects.Track;
import utils.Constants;

public class TrackListWithHeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Activity mActivity;
    private ArrayList<Track> mTracks;
    private View mHeader;

    public TrackListWithHeaderAdapter(Activity context, ArrayList<Track> tracks, View header)
    {
        mActivity = context;
        mTracks = tracks;
        mHeader = header;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER){
            return new ViewHeader(mHeader);
        }
        else {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.item_track,parent,false);

            return new ViewItem(view);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewItem){
            final Track track = mTracks.get(position-1);

            final ImageView icon = ((ViewItem) holder).icon;
            icon.setImageBitmap(track.getIcon());

            TextView trackName = ((ViewItem) holder).name;
            trackName.setText(track.getName());

            TextView artistName = ((ViewItem) holder).artist;
            artistName.setText(track.getArtist());

            //set OnClickListeners
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (track.hasDefaultImage()){
                        Toast.makeText(mActivity, "Can't play this track", Toast.LENGTH_SHORT)
                                .show();
                    }
                    else{

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(mActivity, icon, "transitionImage");
                        mActivity.startActivity(TrackActivity.getStartIntent(mActivity, track), options.toBundle());
                    }
                }
            };
            icon.setOnClickListener(clickListener);
            trackName.setOnClickListener(clickListener);
            artistName.setOnClickListener(clickListener);
        }
    }

    @Override
    public int getItemCount() {
        if (mHeader == null){
            return mTracks.size();
        }
        else{
            return mTracks.size()+1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? TYPE_HEADER : TYPE_ITEM;
    }

    //Represents items
    public static class ViewItem extends RecyclerView.ViewHolder{
        public ImageView icon;
        public TextView name;
        public TextView artist;

        public ViewItem(View itemView) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.icon_track);
            name = (TextView) itemView.findViewById(R.id.name_track);
            artist = (TextView) itemView.findViewById(R.id.artist_track);
        }
    }

    //Represents header
    public static class ViewHeader extends RecyclerView.ViewHolder{
        public ViewHeader(View itemView) {
            super(itemView);
        }
    }
}
