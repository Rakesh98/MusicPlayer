package com.rakesh.mobile.musicmasti.utils;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.rakesh.mobile.musicmasti.R;
import com.rakesh.mobile.musicmasti.model.Song;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by rakesh.jnanagari on 06/07/16.
 */
public class Utils {

  public static final Uri ART_WORK_URI = Uri.parse("content://media/external/audio/albumart");
  public static int mToolBarHeight = 100;


  public static void setBitMap(Context context, ImageView imageView, long albumId) {
    Uri uriPath = ContentUris.withAppendedId(ART_WORK_URI, albumId);
    Glide.with(context).load(uriPath).placeholder(R.drawable.music_icon).crossFade().centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
  }

  public static void setCircularImage(final Context context, final ImageView imageView, long albumId) {
    Uri uriPath = ContentUris.withAppendedId(ART_WORK_URI, albumId);
    Glide.with(context).load(uriPath).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new BitmapImageViewTarget(imageView) {
      @Override
      protected void setResource(Bitmap resource) {
        RoundedBitmapDrawable circularBitmapDrawable =
                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
        circularBitmapDrawable.setCircular(true);
        imageView.setImageDrawable(circularBitmapDrawable);
      }
    });
  }

  public static void setBlurImage(final Activity context, final ImageView imageView, final ImageView sourceImage, final View imageLayout, final long albumId) throws ExecutionException, InterruptedException {
    final Uri uriPath = ContentUris.withAppendedId(ART_WORK_URI, albumId);
    Display display = context.getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    final int width = size.x;
    final int height = size.y;
    new AsyncTask<Void, Void, Bitmap>() {

      @Override
      protected Bitmap doInBackground(Void... params) {
        try {
          Bitmap mBitMap = Glide.
                  with(context).
                  load(uriPath).
                  asBitmap()
                  .diskCacheStrategy(DiskCacheStrategy.ALL).
                  into(100, 100). // Width and height
                  get();
          return mBitMap;
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }
        return null;
      }

      @Override
      protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null) {
          Bitmap mBitMap = BlurBuilder.blurController(context, bitmap, 0.6f, 12, width, height);
          imageView.setImageBitmap(mBitMap);
          imageLayout.setVisibility(View.VISIBLE);

//          setCircularImage(context, sourceImage, albumId);
          if (android.os.Build.VERSION.SDK_INT >= 16){
            imageView.setBackground(new BitmapDrawable(bitmap));
          }
          else{
            imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
          }
        } else {
          sourceImage.setVisibility(View.GONE);
        }
      }
    }.execute();

  }

  public static void setBitMap(Context context, SimpleTarget simpleTarget, long albumId) {
    Uri uriPath = ContentUris.withAppendedId(ART_WORK_URI, albumId);
    Glide.with(context).load(uriPath).placeholder(R.drawable.music_icon)
        .override(Configuration.musicContainerListThumbnailDimen,
            Configuration.musicContainerListThumbnailDimen)
        .crossFade().centerCrop().into(simpleTarget);
  }

  public static void initGridItemSize(Activity activity) {
    Display display = activity.getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    if (isPotraitMode(activity)) {
      if (Configuration.gridView) {
        Configuration.gridCount = Configuration.potrateGridCount;
      } else {
        Configuration.gridCount = Configuration.potrateListCount;
      }
    } else {
      if (Configuration.gridView) {
        Configuration.gridCount = Configuration.landscapeGridCount;
      } else {
        Configuration.gridCount = Configuration.landscapeListCount;
      }
    }

    Configuration.musicContainerListThumbnailDimen = size.x / Configuration.gridCount;
  }

  private static boolean isPotraitMode(Activity activity) {
    Display getOrient = activity.getWindowManager().getDefaultDisplay();
    if (getOrient.getWidth() < getOrient.getHeight()) {
      return true;
    } else {
      return false;
    }

  }

  /**
   * Function to convert milliseconds time to
   * Timer Format
   * Hours:Minutes:Seconds
   * */
  public static String milliSecondsToTimer(long milliseconds){
    String finalTimerString = "";
    String secondsString = "";

    // Convert total duration into time
    int hours = (int)( milliseconds / (1000*60*60));
    int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
    int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
    // Add hours if there
    if(hours > 0){
      finalTimerString = hours + ":";
    }

    // Prepending 0 to seconds if it is one digit
    if(seconds < 10){
      secondsString = "0" + seconds;
    }else{
      secondsString = "" + seconds;}

    finalTimerString = finalTimerString + minutes + ":" + secondsString;

    // return timer string
    return finalTimerString;
  }

  /**
   * Function to get Progress percentage
   * @param currentDuration
   * @param totalDuration
   * */
  public static int getProgressPercentage(long currentDuration, long totalDuration){
    Double percentage = (double) 0;

    long currentSeconds = (int) (currentDuration / 1000);
    long totalSeconds = (int) (totalDuration / 1000);

    // calculating percentage
    percentage =(((double)currentSeconds)/totalSeconds)*100;

    // return percentage
    return percentage.intValue();
  }

  /**
   * Function to change progress to timer
   * @param progress -
   * @param totalDuration
   * returns current duration in milliseconds
   * */
  public static int progressToTimer(int progress, int totalDuration) {
    int currentDuration = 0;
    totalDuration = (int) (totalDuration / 1000);
    currentDuration = (int) ((((double)progress) / 100) * totalDuration);

    // return current duration in milliseconds
    return currentDuration * 1000;
  }

  public static String convertBytesToMb(long bytes) {
    double returnValue = bytes / (1024.0 * 1024);
    return String.format("%.2f MB", returnValue);
  }

  public static List<Integer> jsonToList(String jsonObject) {
    ArrayList<Integer> returnList = new ArrayList<>();
    try {
      if (jsonObject != null) {
        String[] split = jsonObject.split(":");
        for (int i = 0; i < split.length; i++) {
          returnList.add(Integer.parseInt(split[i]));
        }
      }
    } catch (Exception e) {

    }
    return returnList;
  }

  public static String listToString(List<Integer> list) {
    StringBuffer returnString = new StringBuffer().append("");
    if(null != list) {
      for (int i = 0; i < list.size(); i++) {
        if (i == 0) {
          returnString.append(list.get(i));
        } else {
          returnString.append(new StringBuffer().append(":").append(list.get(i)));
        }
      }
    }
    return returnString.toString();
  }

  public static void addToPlayList(Song song, Context context) {
    if (!StaticData.getPlayListSelected().contains(song.getId())) {
      StaticData.getPlayListSelected().add(song.getId());
    }
    SharedPreference.getInstance(context).putSharedPref(Constants.PLAY_LIST_SELECTED, Utils.listToString(StaticData.getPlayListSelected()));
  }

  public static void addToPlayList(List<Song> songList, Context context) {
    for (int i = 0; i < songList.size(); i++) {
      if(!StaticData.getPlayListSelected().contains(songList.get(i).getId())) {
        StaticData.getPlayListSelected().add(songList.get(i).getId());
      }
    }
    SharedPreference.getInstance(context).putSharedPref(Constants.PLAY_LIST_SELECTED, Utils.listToString(StaticData.getPlayListSelected()));
  }

  public static void setStatusBarTranslucent(Activity activity, boolean makeTranslucent) {
    if (makeTranslucent) {
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    } else {
      activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
  }
}
