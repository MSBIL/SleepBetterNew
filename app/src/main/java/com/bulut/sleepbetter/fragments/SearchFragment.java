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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bulut.sleepbetter.MainActivity;
import com.bulut.sleepbetter.R;
import com.bulut.sleepbetter.adapters.VideosAdapter;
import com.bulut.sleepbetter.database.YouTubeSqlDb;
import com.bulut.sleepbetter.interfaces.ItemEventsListener;
import com.bulut.sleepbetter.interfaces.OnFavoritesSelected;
import com.bulut.sleepbetter.interfaces.OnItemSelected;
import com.bulut.sleepbetter.model.YouTubeVideo;
import com.bulut.sleepbetter.utils.Config;
import com.bulut.sleepbetter.utils.NetworkConf;
import com.bulut.sleepbetter.youtube.YouTubeVideosLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that handles list of the videos searched on YouTube
 * Created by smedic on 7.3.16..
 */
public class SearchFragment extends BaseFragment implements ItemEventsListener<YouTubeVideo> {

    private static final String TAG = "SMEDIC search frag";
    private RecyclerView videosFoundListView;
    private List<YouTubeVideo> searchResultsList;
    private VideosAdapter videoListAdapter;
    private ProgressBar loadingProgressBar;
    private NetworkConf networkConf;
    private Context context;
    private OnItemSelected itemSelected;
    private OnFavoritesSelected onFavoritesSelected;
    private RelativeLayout nothingFoundMessageHolder;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchResultsList = new ArrayList<>();
        networkConf = new NetworkConf(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        videosFoundListView = (RecyclerView) v.findViewById(R.id.fragment_list_items);
        videosFoundListView.setLayoutManager(new LinearLayoutManager(context));
        loadingProgressBar = (ProgressBar) v.findViewById(R.id.fragment_progress_bar);
        videoListAdapter = new VideosAdapter(context, searchResultsList);
        videoListAdapter.setOnItemEventsListener(this);
        videosFoundListView.setAdapter(videoListAdapter);
        nothingFoundMessageHolder = (RelativeLayout) v.findViewById(R.id.nothing_found_holder);
        //disable swipe to refresh for this tab
        return v;
    }

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            this.context = context;
            itemSelected = (MainActivity) context;
            onFavoritesSelected = (MainActivity) context;
        }
    }
    */

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
        this.itemSelected = null;
        this.onFavoritesSelected = null;
    }

    /**
     * Search for query on youTube by using YouTube Data API V3
     *
     * @param query
     */
    public void searchQuery(final String query) {
        //check network connectivity
        if (!networkConf.isNetworkAvailable()) {
            networkConf.createNetErrorDialog();
            return;
        }

        loadingProgressBar.setVisibility(View.VISIBLE);

        getLoaderManager().restartLoader(1, null, new LoaderManager.LoaderCallbacks<List<YouTubeVideo>>() {
            @Override
            public Loader<List<YouTubeVideo>> onCreateLoader(final int id, final Bundle args) {
                return new YouTubeVideosLoader(context, query);
            }

            @Override
            public void onLoadFinished(Loader<List<YouTubeVideo>> loader, List<YouTubeVideo> data) {
                if (data == null)
                    return;
                videosFoundListView.smoothScrollToPosition(0);
                searchResultsList.clear();
                searchResultsList.addAll(data);
                loadingProgressBar.setVisibility(View.INVISIBLE);
                updateList();
            }

            @Override
            public void onLoaderReset(Loader<List<YouTubeVideo>> loader) {
                searchResultsList.clear();
                searchResultsList.addAll(Collections.<YouTubeVideo>emptyList());
                updateList();
            }
        }).forceLoad();
    }

    @Override
    public void onShareClicked(String itemId) {
        share(Config.SHARE_VIDEO_URL + itemId);
    }

    @Override
    public void onFavoriteClicked(YouTubeVideo video, boolean isChecked) {
        onFavoritesSelected.onFavoritesSelected(video, isChecked); // pass event to MainActivity
    }

    @Override
    public void onItemClick(YouTubeVideo video) {
        YouTubeSqlDb.getInstance().videos(YouTubeSqlDb.VIDEOS_TYPE.RECENTLY_WATCHED).create(video);
        //itemSelected.onVideoSelected(video);
        itemSelected.onPlaylistSelected(searchResultsList, searchResultsList.indexOf(video));
    }

    @Override
    public void updateList() {
        videoListAdapter.notifyDataSetChanged();
        if (videoListAdapter.getItemCount() > 0) {
            nothingFoundMessageHolder.setVisibility(View.GONE);
            videosFoundListView.setVisibility(View.VISIBLE);
        } else {
            nothingFoundMessageHolder.setVisibility(View.VISIBLE);
            videosFoundListView.setVisibility(View.GONE);
        }
    }
}
