package com.bulut.sleepbetter.interfaces;

import com.bulut.sleepbetter.model.YouTubeVideo;

/**
 * Created by smedic on 9.2.17..
 */

public interface ItemEventsListener<Model> {
    void onShareClicked(String itemId);

    void onFavoriteClicked(YouTubeVideo video, boolean isChecked);

    void onItemClick(Model model); //handle click on a row (video or playlist)
}
