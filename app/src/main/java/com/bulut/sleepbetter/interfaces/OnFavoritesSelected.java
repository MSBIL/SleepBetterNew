package com.bulut.sleepbetter.interfaces;

import com.bulut.sleepbetter.model.YouTubeVideo;

/**
 * Created by smedic on 5.3.17..
 */

public interface OnFavoritesSelected {
    void onFavoritesSelected(YouTubeVideo video, boolean isChecked);
}
