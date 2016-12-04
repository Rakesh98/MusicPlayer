package com.rakesh.mobile.musicmasti.utils;

import com.rakesh.mobile.musicmasti.model.Song;

import java.util.Comparator;

/**
 * Created by rakesh.jnanagari on 19/11/16.
 */

public class RecentlyPlayedSongListComparator implements Comparator<Song> {
    @Override
    public int compare(Song lhsSong, Song rhsSong) {
        if (Configuration.sortType == Constants.RECENT_SORT) {
            try {
                int lhs = lhsSong.getId();
                int rhs = rhsSong.getId();
                if (StaticData.getRecentlyPlayed().contains(lhs) && StaticData.getRecentlyPlayed().contains(rhs)) {
                    if (StaticData.getRecentlyPlayed().indexOf(lhs) < StaticData.getRecentlyPlayed().indexOf(rhs)) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                if (StaticData.getRecentlyPlayed().contains(lhs) && !StaticData.getRecentlyPlayed().contains(rhs)) {
                    return -1;
                }
                if (!StaticData.getRecentlyPlayed().contains(lhs) && StaticData.getRecentlyPlayed().contains(rhs)) {
                    return 1;
                }
            } catch (Exception e) {

            }
        } else if (Configuration.sortType == Constants.TITLE_SORT) {
            if (lhsSong.getTitle() != null && rhsSong.getTitle()!= null) {
                return lhsSong.getTitle().compareTo(rhsSong.getTitle());
            }
            if(lhsSong.getTitle() == null) {
                return -1;
            } else {
                return 1;
            }
        } else if (Configuration.sortType == Constants.ARTISTS_SORT) {
            if (lhsSong.getComposer() != null && rhsSong.getComposer()!= null) {
                return lhsSong.getComposer().compareTo(rhsSong.getComposer());
            }
            if(lhsSong.getComposer() == null) {
                return -1;
            } else {
                return 1;
            }
        } else if (Configuration.sortType == Constants.DATE_SORT) {
            if (lhsSong.getDateAdded() != null && rhsSong.getDateAdded()!= null) {
                try {
                    long lhsDate = Long.parseLong(lhsSong.getDateAdded());
                    long rhsDate = Long.parseLong(rhsSong.getDateAdded());
                    if (lhsDate > rhsDate) {
                        return -1;
                    } else {
                        return 1;
                    }
                }catch (NumberFormatException e) {
                    return 0;
                }
            }
            if(lhsSong.getDateAdded() == null) {
                return -1;
            } else {
                return 1;
            }
        }
        return 0;
    }
}
