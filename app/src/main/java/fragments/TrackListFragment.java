package fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.colinforzeal.blury.R;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import java.util.ArrayList;

import activities.TrackActivity;
import activities.TrackListActivity;
import adapters.TrackListWithHeaderAdapter;
import decorations.DividerItemDecoration;
import objects.Track;
import utils.Constants;
import utils.Utils;

public class TrackListFragment extends Fragment implements ObservableScrollViewCallbacks{
    private static Track mTrack;
    private static ArrayList<Track> mTracks;

    private static ImageView sImage;
    private static View sOverlayView;
    private static FloatingActionButton sPlayFab;
    private static View sRecyclerViewBackground;
    private static ObservableRecyclerView sRecyclerView;

    private static int sImageHeight;
    private static int sActionBarSize;

    public static TrackListFragment newInstance(Track track, ArrayList<Track> tracks) {
        TrackListFragment fragment = new TrackListFragment();
        mTrack = track;
        mTracks = tracks;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);

        sImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        sActionBarSize = Utils.getStatusBarHeight(getResources());

        sImage = (ImageView) view.findViewById(R.id.track_image_tracklist_fragment);
        sOverlayView = view.findViewById(R.id.overlay_tracklist_fragment);

        sRecyclerViewBackground = view.findViewById(R.id.list_background_tracklist_fragment);

        /*
         * RecyclerView should have a big padding at the top
         * of the it to make ImageView visible.
         * We achieve this by adding a transparent header view to the RecyclerView
         * We send recyclerHeaderView to adapter.
         */
        final View recyclerHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_header, null);
        recyclerHeaderView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , sImageHeight));
        recyclerHeaderView.setClickable(true);

        sRecyclerView = (ObservableRecyclerView) view.findViewById(R.id.recyclerview_tracklist_fragment);
        sRecyclerView.setScrollViewCallbacks(this);
        sRecyclerView.setHasFixedSize(false);

        TrackListWithHeaderAdapter listAdapter = new TrackListWithHeaderAdapter(getActivity(), mTracks, recyclerHeaderView);
        sRecyclerView.setAdapter(listAdapter);

        sPlayFab = (FloatingActionButton) view.findViewById(R.id.fab_tracklist_fragment);

        setViewsOperations();

        return view;
    }

    //Sets view transitions
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        float flexibleRange = sImageHeight - sActionBarSize;
        int minOverlayTransitionY = sActionBarSize - sOverlayView.getHeight();

        sOverlayView.setTranslationY(ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        sImage.setTranslationY(ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        sRecyclerViewBackground.setTranslationY(Math.max(0, -scrollY + sImageHeight));

        sOverlayView.setAlpha(ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        float fabScale = ScrollUtils.getFloat((float) (sImageHeight - scrollY) / sImageHeight, 0, 1);
        sPlayFab.setScaleX(fabScale);
        sPlayFab.setScaleY(fabScale);

        sPlayFab.setTranslationY(-scrollY - sPlayFab.getHeight()/2);
    }

    @Override
    public void onDownMotionEvent() {}

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {}

    private void setViewsOperations(){
        sImage.setImageBitmap(mTrack.getImage());

        sOverlayView.post(new Runnable() {
            @Override
            public void run() {
                sOverlayView.setTranslationY(sImageHeight);
            }
        });

        sRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        sPlayFab.post(new Runnable() {
            @Override
            public void run() {
                sPlayFab.setTranslationY(-sPlayFab.getHeight() / 2);
            }
        });

        sPlayFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTrack.hasDefaultImage()) {
                    Toast.makeText(getActivity(), "Can't play this track", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    startActivity(TrackActivity.getStartIntent(getActivity(), mTrack));
                }
            }

        });

        final int fabColor = mTrack.getColor();
        sPlayFab.setBackgroundTintList(ColorStateList.valueOf(fabColor));

        sRecyclerViewBackground.post(new Runnable() {
            @Override
            public void run() {
                sRecyclerViewBackground.setTranslationY(sImageHeight);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
