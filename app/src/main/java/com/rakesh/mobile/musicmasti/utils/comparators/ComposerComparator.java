package com.rakesh.mobile.musicmasti.utils.comparators;

import com.rakesh.mobile.musicmasti.model.Album;
import com.rakesh.mobile.musicmasti.utils.Configuration;
import com.rakesh.mobile.musicmasti.utils.Constants;

import java.util.Comparator;

/**
 * Created by rakesh.jnanagari on 25/11/16.
 */

public class ComposerComparator implements Comparator<Album> {
    @Override
    public int compare(Album lhs, Album rhs) {
        if (Configuration.sortType == Constants.RECENT_SORT) {
            try {
                int lhsIndex = lhs.getRecentlyPlayedPosition();
                int rhsIndex = rhs.getRecentlyPlayedPosition();
                if (-1 != lhsIndex && -1 != rhsIndex) {
                    if (lhsIndex < rhsIndex) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                if (-1 != lhsIndex) {
                    return -1;
                }
                if (-1 != rhsIndex) {
                    return 1;
                }
            } catch (Exception e) {

            }
            return 0;
        } else if (Configuration.sortType == Constants.ARTISTS_SORT) {
            if (lhs.getTitle() != null && rhs.getTitle()!= null) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
            if(lhs.getTitle() == null) {
                return -1;
            } else {
                return 1;
            }
        } else if (Configuration.sortType == Constants.TITLE_SORT) {
            if (lhs.getSubTitle() != null && rhs.getSubTitle()!= null) {
                return lhs.getSubTitle().compareTo(rhs.getSubTitle());
            }
            if(lhs.getSubTitle() == null) {
                return -1;
            } else {
                return 1;
            }
        } else if (Configuration.sortType == Constants.DATE_SORT) {
            if (lhs.getDateAdded() != null && rhs.getDateAdded()!= null) {
                try {
                    long lhsDate = Long.parseLong(lhs.getDateAdded());
                    long rhsDate = Long.parseLong(rhs.getDateAdded());
                    if (lhsDate > rhsDate) {
                        return -1;
                    } else {
                        return 1;
                    }
                }catch (NumberFormatException e) {
                    return 0;
                }
            }
            if(lhs.getDateAdded() == null) {
                return -1;
            } else {
                return 1;
            }
        }
        return 0;
    }
}
