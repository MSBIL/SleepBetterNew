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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bulut.sleepbetter.MainPageActivity;
import com.bulut.sleepbetter.R;
import com.bulut.sleepbetter.adapters.VideosAdapter;
import com.bulut.sleepbetter.database.YouTubeSqlDb;
import com.bulut.sleepbetter.interfaces.ItemEventsListener;
import com.bulut.sleepbetter.interfaces.OnItemSelected;
import com.bulut.sleepbetter.model.YouTubeVideo;
import com.bulut.sleepbetter.utils.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stevan Medic on 21.3.16..
 */
public class FavoritesFragment extends BaseFragment implements ItemEventsListener<YouTubeVideo> {

    private static final String TAG = "SMEDIC Favorites";
    private List<YouTubeVideo> favoriteVideos;

    private RecyclerView favoritesListView;
    private VideosAdapter videoListAdapter;
    private OnItemSelected itemSelected;
    private Context context;
    private RelativeLayout nothingFoundMessageHolder;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoriteVideos = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        favoritesListView = (RecyclerView) v.findViewById(R.id.fragment_list_items);
        favoritesListView.setLayoutManager(new LinearLayoutManager(context));

        videoListAdapter = new VideosAdapter(context, favoriteVideos);
        videoListAdapter.setOnItemEventsListener(this);
        favoritesListView.setAdapter(videoListAdapter);

        nothingFoundMessageHolder = (RelativeLayout) v.findViewById(R.id.nothing_found_holder);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        favoriteVideos.clear();
        favoriteVideos.addAll(YouTubeSqlDb.getInstance().videos(YouTubeSqlDb.VIDEOS_TYPE.FAVORITE).readAll());
        updateList();
    }

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.itemSelected = (MainActivity) context;
            this.context = context;
        }
    }
    */

    @Override
    public void onDetach() {
        super.onDetach();
        this.itemSelected = null;
        this.context = null;
    }

    /**
     * Clears recently played list items
     */
    public void clearFavoritesList() {
        favoriteVideos.clear();
        updateList();
    }

    public void addToFavoritesList(YouTubeVideo video) {
        YouTubeSqlDb.getInstance().videos(YouTubeSqlDb.VIDEOS_TYPE.FAVORITE).create(video);
    }

    public void removeFromFavorites(YouTubeVideo video) {
        YouTubeSqlDb.getInstance().videos(YouTubeSqlDb.VIDEOS_TYPE.FAVORITE).delete(video.getId());
        favoriteVideos.remove(video);
        updateList();
    }

    @Override
    public void onShareClicked(String itemId) {
        share(Config.SHARE_VIDEO_URL + itemId);
    }

    @Override
    public void onFavoriteClicked(YouTubeVideo video, boolean isChecked) {
        if (isChecked) {
            addToFavoritesList(video);
        } else {
            removeFromFavorites(video);
        }
    }

    @Override
    public void onItemClick(YouTubeVideo video) {
        YouTubeSqlDb.getInstance().videos(YouTubeSqlDb.VIDEOS_TYPE.RECENTLY_WATCHED).create(video);
        itemSelected.onPlaylistSelected(favoriteVideos, favoriteVideos.indexOf(video));
    }

    @Override
    public void updateList() {
        Log.d(TAG, "updateList: ");
        videoListAdapter.notifyDataSetChanged();
        if (videoListAdapter.getItemCount() > 0) {
            Log.d(TAG, "updateList: show");
            nothingFoundMessageHolder.setVisibility(View.GONE);
            favoritesListView.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "updateList: hide");
            nothingFoundMessageHolder.setVisibility(View.VISIBLE);
            favoritesListView.setVisibility(View.GONE);
        }
    }
}
