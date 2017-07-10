package com.systemteam.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.fragment.GuideFragment;
import com.systemteam.fragment.SettingsFragment;

import static com.systemteam.util.Constant.BUNDLE_TYPE_MENU;

/**
 * @author scofield.hhl@gmail.com
 * @Description 设置界面
 * @time 2016/11/7
 */
public class SettingActivity extends BaseActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private SettingsFragment settingsFragment;
    private String keyprefDownloadPath;
    private String keyprefDownloadTaskMax;
    private String keyprefWifiOnly;
    private String keyPrefVideoPlayer, keyPrefAudioPlayer;
    SettingsFragment mSettingFragment;
    private int mType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initToolBar(this, R.string.setting);
        mSettingFragment = new SettingsFragment();
        mType = getIntent().getIntExtra(BUNDLE_TYPE_MENU, -1);
        switch (mType){
            case 0:
                getFragmentManager().beginTransaction()
                        .replace(R.id.ll_content, mSettingFragment)
                        .commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ll_content, new GuideFragment())
                        .commit();
                break;
            default:
                break;
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        /*keyprefDownloadPath = getString(R.string.pref_download_path_key);
        keyprefDownloadTaskMax = getString(R.string.pref_max_download_task_key);
        keyprefWifiOnly = getString(R.string.pref_wifi_download_only_key);
        keyPrefVideoPlayer = getString(R.string.pref_player_video_key);
        keyPrefAudioPlayer = getString(R.string.pref_player_audio_key);*/

    }


    @Override
    protected void onResume() {
        super.onResume();
        /*if(mType == 0){
            SharedPreferences sharedPreferences =
                    settingsFragment.getPreferenceScreen().getSharedPreferences();
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
            updateSummary(sharedPreferences, keyprefDownloadTaskMax);
            updateSummary(sharedPreferences, keyprefDownloadPath);
            updateSummary(sharedPreferences, keyPrefVideoPlayer);
            updateSummary(sharedPreferences, keyPrefAudioPlayer);
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if(mType == 0) {
            SharedPreferences sharedPreferences =
                    settingsFragment.getPreferenceScreen().getSharedPreferences();
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }*/
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(keyprefDownloadPath)) {
            updateSummary(sharedPreferences, key);
        } else if (key.equals(keyprefWifiOnly)) {
        }

    }


    private void updateSummary(SharedPreferences sharedPreferences, String key) {
        Preference updatedPref = settingsFragment.findPreference(key);
        updatedPref.setSummary(sharedPreferences.getString(key, ""));
    }

    private void updateSummaryDefault(SharedPreferences sharedPreferences, String key, String defaultVlaue) {
        Preference updatedPref = settingsFragment.findPreference(key);
        updatedPref.setSummary(sharedPreferences.getString(key, defaultVlaue));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

    }
}
