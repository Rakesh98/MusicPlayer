package com.rakesh.mobile.musicmasti.view;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.rakesh.mobile.musicmasti.R;
import com.rakesh.mobile.musicmasti.utils.Configuration;
import com.rakesh.mobile.musicmasti.utils.Constants;
import com.rakesh.mobile.musicmasti.utils.SharedPreference;
import com.rakesh.mobile.musicmasti.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rakesh.jnanagari on 12/07/16.
 */
public class Settings extends AppCompatActivity {

    private Toolbar toolbar;
    private SharedPreference mSharedPreference;
    private RadioButton gridViewRadioButton;
    private RadioButton listViewRadioButton;
    private Switch albumSwitch;
    private Switch allSongsSwitch;
    private Switch composersSwitch;
    private Switch playlistsSwitch;
    private Button saveButton;
    private Button cancelButton;
    private Spinner gridCountSpinner;
    private Spinner listCountSpinner;
    private CheckBox shakeSkipCheckBox;
    private CheckBox headSetControlCheckBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_dark);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        Utils.setStatusBarTranslucent(this, true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mSharedPreference = SharedPreference.getInstance(this);
        gridViewRadioButton = (RadioButton) findViewById(R.id.grid_view);
        listViewRadioButton = (RadioButton) findViewById(R.id.list_view);
        albumSwitch = (Switch) findViewById(R.id.albums);
        allSongsSwitch = (Switch) findViewById(R.id.all_songs);
        composersSwitch = (Switch) findViewById(R.id.composers);
        playlistsSwitch = (Switch) findViewById(R.id.playlists);
        saveButton = (Button) findViewById(R.id.save);
        cancelButton = (Button) findViewById(R.id.cancel);
        gridCountSpinner = (Spinner) findViewById(R.id.grid_count_spinner);
        listCountSpinner = (Spinner) findViewById(R.id.list_count_spinner);
        shakeSkipCheckBox = (CheckBox) findViewById(R.id.shake_skip);
        headSetControlCheckBox = (CheckBox) findViewById(R.id.headset_control);

        List<String> potraitGridCountList = new ArrayList<>();
        potraitGridCountList.add("1");
        potraitGridCountList.add("2");
        potraitGridCountList.add("3");
        potraitGridCountList.add("4");
        potraitGridCountList.add("5");
        List<String> potraitListCountList = new ArrayList<>();
        potraitListCountList.add("1");
        potraitListCountList.add("2");
        potraitListCountList.add("3");
        potraitListCountList.add("4");
        potraitListCountList.add("5");
        potraitListCountList.add("6");
        potraitListCountList.add("7");

        if(Configuration.isShakeSongSkipEnabled) {
            shakeSkipCheckBox.setChecked(true);
        }
        if(Configuration.isHeadSetControlEnabled) {
            headSetControlCheckBox.setChecked(true);
        }

        ArrayAdapter<String> gridDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, potraitGridCountList);
        ArrayAdapter<String> listDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, potraitGridCountList);
        gridDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gridCountSpinner.setAdapter(gridDataAdapter);
        listCountSpinner.setAdapter(listDataAdapter);
        if(Configuration.gridView) {
            gridCountSpinner.setSelection(Configuration.potrateGridCount - 1);
            listCountSpinner.setSelection(Configuration.landscapeGridCount - 1);
            showGridCount(true);

        } else {
            gridCountSpinner.setSelection(Configuration.landscapeGridCount - 1);
            listCountSpinner.setSelection(Configuration.landscapeListCount - 1);
            showGridCount(false);

        }
        gridViewRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gridViewRadioButton.isChecked()) {
                    gridCountSpinner.setSelection(Configuration.potrateGridCount - 1);
                    listCountSpinner.setSelection(Configuration.landscapeGridCount - 1);
                    showGridCount(true);
                } else {
                    gridCountSpinner.setSelection(Configuration.potrateListCount - 1);
                    listCountSpinner.setSelection(Configuration.landscapeListCount - 1);
                    showGridCount(false);
                }
            }
        });

        listViewRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listViewRadioButton.isChecked()) {
                    gridCountSpinner.setSelection(Configuration.potrateListCount - 1);
                    listCountSpinner.setSelection(Configuration.landscapeListCount - 1);
                    showGridCount(false);
                } else {
                    gridCountSpinner.setSelection(Configuration.potrateGridCount - 1);
                    listCountSpinner.setSelection(Configuration.landscapeGridCount - 1);
                    showGridCount(true);
                }
            }
        });

        if(Configuration.gridView) {
            gridViewRadioButton.setChecked(true);
        } else {
            listViewRadioButton.setChecked(true);
        }
        if(Configuration.albums) {
            albumSwitch.setChecked(true);
        } else {
            albumSwitch.setChecked(false);
        }
        if(Configuration.allSongs) {
            allSongsSwitch.setChecked(true);
        } else {
            allSongsSwitch.setChecked(false);
        }
        if(Configuration.composers) {
            composersSwitch.setChecked(true);
        } else {
            composersSwitch.setChecked(false);
        }
        if(Configuration.playlists) {
            playlistsSwitch.setChecked(true);
        } else {
            playlistsSwitch.setChecked(false);
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                if (accelerometerSensor == null)
                {
                    // Device has dosen't have Accelerometer
                    shakeSkipCheckBox.setChecked(false);
                    Toast.makeText(getApplicationContext(), getString(R.string.device_accelorometer_absent), Toast.LENGTH_LONG).show();
                    return;
                }

                initConfiguration();
                Utils.initGridItemSize(Settings.this);
                Intent intent = new Intent(Settings.this, MusicContainer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void initConfiguration() {
        if(gridViewRadioButton.isChecked()) {
            Configuration.gridView = true;
        } else {
            Configuration.gridView = false;
        }
        if(albumSwitch.isChecked()) {
            Configuration.albums = true;
        } else {
            Configuration.albums = false;
        }
        if(allSongsSwitch.isChecked()) {
            Configuration.allSongs = true;
        } else {
            Configuration.allSongs = false;
        }
        if(composersSwitch.isChecked()) {
            Configuration.composers = true;
        } else {
            Configuration.composers = false;
        }
        if(playlistsSwitch.isChecked()) {
            Configuration.playlists = true;
        } else {
            Configuration.playlists = false;
        }
        boolean refreshSensorRegisters = false;
        if(shakeSkipCheckBox.isChecked()) {
            if(!Configuration.isShakeSongSkipEnabled) {
                Configuration.isShakeSongSkipEnabled = true;
                refreshSensorRegisters = true;
            }
        } else {
            if(Configuration.isShakeSongSkipEnabled) {
                Configuration.isShakeSongSkipEnabled = false;
                refreshSensorRegisters = true;
            }

        }
        if(headSetControlCheckBox.isChecked()) {
            Configuration.isHeadSetControlEnabled = true;
            if (null != PlayerService.getInstance()) {
                PlayerService.getInstance().registerHeadSetController();
            }
        } else {
            Configuration.isHeadSetControlEnabled = false;
            if (null != PlayerService.getInstance())
            PlayerService.getInstance().unRegisterHeadSetController();
        }
        mSharedPreference.putSharedPrefBoolean(Constants.GRID_VIEW_KEY, Configuration.gridView);
        mSharedPreference.putSharedPrefBoolean(Constants.ALBUMS_KEY, Configuration.albums);
        mSharedPreference.putSharedPrefBoolean(Constants.ALL_SONGS_KEY, Configuration.allSongs);
        mSharedPreference.putSharedPrefBoolean(Constants.COMPOSERS_KEY, Configuration.composers);
        mSharedPreference.putSharedPrefBoolean(Constants.PLAYLIST_KEY, Configuration.playlists);
        mSharedPreference.putSharedPrefBoolean(Constants.IS_SHAKE_SKIP_SONG_KEY, Configuration.isShakeSongSkipEnabled);
        mSharedPreference.putSharedPrefBoolean(Constants.IS_HEAD_SET_CONTROL_KEY, Configuration.isHeadSetControlEnabled);
        if(refreshSensorRegisters) {
            if(Configuration.isShakeSongSkipEnabled) {
                if(null != PlayerService.getInstance()) {
                    PlayerService.getInstance().registerShakeLisitner();
                }
            } else {
                if(null != PlayerService.getInstance()) {
                    PlayerService.getInstance().unRegisterShakeLisitner();
                }
            }
        }
        if(gridViewRadioButton.isChecked()) {
            Configuration.potrateGridCount = Integer.parseInt((String)gridCountSpinner.getSelectedItem());
            Configuration.landscapeGridCount = Integer.parseInt((String)listCountSpinner.getSelectedItem());
            mSharedPreference.putSharedPrefInt(Constants.POTRAIT_GRID_COUNT_KEY, Configuration.potrateGridCount);
            mSharedPreference.putSharedPrefInt(Constants.LANDSCAPE_GRID_COUNT_KEY, Configuration.landscapeGridCount);
        } else {
            Configuration.potrateListCount = Integer.parseInt((String)listCountSpinner.getSelectedItem());
            Configuration.landscapeListCount = Integer.parseInt((String)listCountSpinner.getSelectedItem());
            mSharedPreference.putSharedPrefInt(Constants.POTRAIT_LIST_COUNT_KEY, Configuration.potrateListCount);
            mSharedPreference.putSharedPrefInt(Constants.LANDSCAPE_LIST_COUNT_KEY, Configuration.landscapeListCount);
        }

    }

    private void showGridCount(boolean isGridSelected) {
        if (isGridSelected) {
            findViewById(R.id.grid_count_text).setVisibility(View.VISIBLE);
            gridCountSpinner.setVisibility(View.VISIBLE);
            findViewById(R.id.list_count_text).setVisibility(View.GONE);
            listCountSpinner.setVisibility(View.GONE);
        } else {
            findViewById(R.id.grid_count_text).setVisibility(View.GONE);
            gridCountSpinner.setVisibility(View.GONE);
            findViewById(R.id.list_count_text).setVisibility(View.VISIBLE);
            listCountSpinner.setVisibility(View.VISIBLE);
        }
    }

}
