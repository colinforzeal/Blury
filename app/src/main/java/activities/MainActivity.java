package activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.colinforzeal.blury.R;

import fragments.SearchFragment;
import fragments.StorageFragment;
import utils.Constants;
import utils.ViewAnimation;

public class MainActivity extends AppCompatActivity{
    private static final int TABS_COUNT = 2;

    private static FloatingActionButton sSearchFab;
    private static FloatingActionButton sStorageFab;
    private static TabLayout sTabLayout;
    private static ViewPager sPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(toolbar);

        sSearchFab = (FloatingActionButton) findViewById(R.id.fab_search_main_activity);
        sStorageFab = (FloatingActionButton) findViewById(R.id.fab_storage_main_activity);

        sTabLayout = (TabLayout) findViewById(R.id.tab_layout_main_activity);
        sPager = (ViewPager) findViewById(R.id.pager_main_activity);

        setViewOperations();
        setPagerListener();
    }


    //Receives audio file from storage
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_SOUND && resultCode == Activity.RESULT_OK){
            if (data != null){
                Uri uri = data.getData();

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                try{
                    retriever.setDataSource(this, uri);

                    String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                    if (title == null || artist == null){
                        throw new IllegalArgumentException();
                    }

                    startActivity(TrackListActivity.getStartIntent(this, title, artist));
                }
                catch (IllegalArgumentException e){
                    Toast.makeText(this, "Invalid audio.", Toast.LENGTH_SHORT)
                            .show();

                    Log.e(e.getClass().getName(), "Wrong type of selected audio");
                }
                finally {
                    retriever.release();
                }
            }
        }
    }

    private void setViewOperations(){
        sTabLayout.addTab(sTabLayout.newTab().setText("Search"));
        sTabLayout.addTab(sTabLayout.newTab().setText("Storage"));
        sTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        sPager.setAdapter(pagerAdapter);

        //Start new activity, that searchs via Internet
        sSearchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();

                String artist = SearchFragment.getInstance().getArtistName();
                String title = SearchFragment.getInstance().getTrackName();

                if (artist.equals("") || title.equals("")) {
                    Toast.makeText(MainActivity.this, "Type artist and track", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    startActivity(TrackListActivity.getStartIntent(MainActivity.this, title, artist));
                }
            }
        });

        //Search for audio on device
        sStorageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");

                startActivityForResult(intent, Constants.REQUEST_SOUND);
            }
        });
    }

    private void setPagerListener(){
        sPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(sTabLayout));

        sTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                sPager.setCurrentItem(position);

                if (position == 0) {
                    ViewAnimation.startFabAnimation(sSearchFab);
                } else {
                    ViewAnimation.startFabAnimation(sStorageFab);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if (position == 0) {
                    ViewAnimation.startFabAnimation(sSearchFab);
                } else {
                    ViewAnimation.startFabAnimation(sStorageFab);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void hideSoftKeyboard(){
        if (getCurrentFocus() != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return SearchFragment.getInstance();
            }
            else{
                return StorageFragment.getInstance();
            }
        }

        @Override
        public int getCount() {
            return TABS_COUNT;
        }
    }
}
