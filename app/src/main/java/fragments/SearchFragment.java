package fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.colinforzeal.blury.R;

public class SearchFragment extends Fragment {
    private static final SearchFragment INSTANCE = new SearchFragment();

    private EditText mArtistView;
    private EditText mTrackView;

    public static SearchFragment getInstance(){
        return INSTANCE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null);

        mArtistView = (EditText) view.findViewById(R.id.artist);
        mTrackView = (EditText) view.findViewById(R.id.track);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public String getArtistName(){return mArtistView.getText().toString();}

    public String getTrackName(){return mTrackView.getText().toString();}
}
