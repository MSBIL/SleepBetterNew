/*
 * Copyright (C) 2016 SMedic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bulut.sleepbetter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bulut.sleepbetter.MainActivity;
import com.bulut.sleepbetter.R;
import com.bulut.sleepbetter.adapters.PlaylistsAdapter;
import com.bulut.sleepbetter.database.YouTubeSqlDb;
import com.bulut.sleepbetter.interfaces.ItemEventsListener;
import com.bulut.sleepbetter.interfaces.OnItemSelected;
import com.bulut.sleepbetter.model.YouTubePlaylist;
import com.bulut.sleepbetter.model.YouTubeVideo;
import com.bulut.sleepbetter.utils.Config;
import com.bulut.sleepbetter.youtube.YouTubePlaylistVideosLoader;
import com.bulut.sleepbetter.youtube.YouTubePlaylistsLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that handles list of the playlists acquired from YouTube
 * Created by smedic on 7.3.16..
 */
public class PlaylistsFragment extends BaseFragment implements
        ItemEventsListener<YouTubePlaylist> {

    private static final String TAG = "SMEDIC PlaylistsFrag";

    private ArrayList<YouTubePlaylist> playlists;
    private RecyclerView playlistsListView;
    private PlaylistsAdapter playlistsAdapter;
    //private SwipeRefreshLayout swipeToRefresh;
    private Context context;
    private OnItemSelected itemSelected;
    private RelativeLayout nothingFoundMessageHolder;

    public PlaylistsFragment() {
        // Required empty public constructor
    }

    public static PlaylistsFragment newInstance() {
        return new PlaylistsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playlists = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        /* Setup the ListView */
        playlistsListView = (RecyclerView) v.findViewById(R.id.fragment_list_items);
        playlistsListView.setLayoutManager(new LinearLayoutManager(context));
        nothingFoundMessageHolder = (RelativeLayout) v.findViewById(R.id.nothing_found_holder);

        playlistsAdapter = new PlaylistsAdapter(context, playlists);
        playlistsAdapter.setOnItemEventsListener(this);
        playlistsListView.setAdapter(playlistsAdapter);

//        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                searchPlaylists();
//            }
//        });
        return v;
    }

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.context = context;
            itemSelected = (MainActivity) context;
        }
    }
    */

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
        this.itemSelected = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        playlists.clear();
        playlists.addAll(YouTubeSqlDb.getInstance().playlists().readAll());
        updateList();
    }

    public void searchPlaylists() {
        getLoaderManager().restartLoader(2, null, new LoaderManager.LoaderCallbacks<List<YouTubePlaylist>>() {
            @Override
            public Loader<List<YouTubePlaylist>> onCreateLoader(final int id, final Bundle args) {
                return new YouTubePlaylistsLoader(context);
            }

            @Override
            public void onLoadFinished(Loader<List<YouTubePlaylist>> loader, List<YouTubePlaylist> data) {
                if (data == null) {
                    //swipeToRefresh.setRefreshing(false);
                    return;
                }
                YouTubeSqlDb.getInstance().playlists().deleteAll();
                for (YouTubePlaylist playlist : data) {
                    YouTubeSqlDb.getInstance().playlists().create(playlist);
                }

                Log.d(TAG, "onLoadFinished: data size: " + data.size());

                playlists.clear();
                playlists.addAll(data);
                //swipeToRefresh.setRefreshing(false);
                updateList();
            }

            @Override
            public void onLoaderReset(Loader<List<YouTubePlaylist>> loader) {
                playlists.clear();
                playlists.addAll(Collections.<YouTubePlaylist>emptyList());
                updateList();
            }
        }).forceLoad();
    }

    private void acquirePlaylistVideos(final String playlistId) {
        getLoaderManager().restartLoader(3, null, new LoaderManager.LoaderCallbacks<List<YouTubeVideo>>() {
            @Override
            public Loader<List<YouTubeVideo>> onCreateLoader(final int id, final Bundle args) {
                return new YouTubePlaylistVideosLoader(context, playlistId);
            }

            @Override
            public void onLoadFinished(Loader<List<YouTubeVideo>> loader, List<YouTubeVideo> data) {
                if (data == null || data.isEmpty()) {
                    return;
                }
                itemSelected.onPlaylistSelected(data, 0);
            }

            @Override
            public void onLoaderReset(Loader<List<YouTubeVideo>> loader) {
                playlists.clear();
                playlists.addAll(Collections.<YouTubePlaylist>emptyList());
            }
        }).forceLoad();
    }

    /**
     * Remove playlist with specific ID from DB and list TODO
     *
     * @param playlistId
     */
    private void removePlaylist(final String playlistId) {
        YouTubeSqlDb.getInstance().playlists().delete(playlistId);

        for (YouTubePlaylist playlist : playlists) {
            if (playlist.getId().equals(playlistId)) {
                playlists.remove(playlist);
                break;
            }
        }
        updateList();
    }

    /**
     * Extracts user name from email address
     *
     * @param emailAddress
     * @return
     */
    private String extractUserName(String emailAddress) {
        if (emailAddress != null) {
            String[] parts = emailAddress.split("@");
            if (parts.length > 0) {
                if (parts[0] != null) {
                    return parts[0];
                }
            }
        }
        return "";
    }

    @Override
    public void onShareClicked(String itemId) {
        share(Config.SHARE_PLAYLIST_URL + itemId);
    }

    @Override
    public void onFavoriteClicked(YouTubeVideo video, boolean isChecked) {
        //do nothing
    }

    @Override
    public void onItemClick(YouTubePlaylist youTubePlaylist) {
        //results are in onVideosReceived callback method
        acquirePlaylistVideos(youTubePlaylist.getId());
    }

    @Override
    public void updateList() {
        playlistsAdapter.notifyDataSetChanged();
        if (playlistsAdapter.getItemCount() > 0) {
            nothingFoundMessageHolder.setVisibility(View.GONE);
            playlistsListView.setVisibility(View.VISIBLE);
        } else {
            nothingFoundMessageHolder.setVisibility(View.VISIBLE);
            playlistsListView.setVisibility(View.GONE);
        }
    }
}