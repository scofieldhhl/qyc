package com.systemteam.view;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.systemteam.BikeApplication;
import com.systemteam.R;

/**
 * Created by chenjiang on 2017/1/6.
 */

public class PreferenceWithTip extends Preference {

    private PreferenceActivity parent;
    private ImageView preview_img;

    public PreferenceWithTip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PreferenceWithTip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreferenceWithTip(Context context) {
        super(context);
    }

    void setActivity(PreferenceActivity parent) {
        this.parent = parent;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        preview_img = (ImageView) view.findViewById(R.id.pref_current_img);
        if (BikeApplication.isHaveUpdate) {
            preview_img.setVisibility(View.VISIBLE);
        } else {
            preview_img.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
//        Bundle bundle = new Bundle();
//        bundle.putInt(GameGlobal.PREF_KEY_IMAGE, this.mImage);
//        Intent intent = new Intent(parent, ImageSelector.class);
//        intent.putExtras(bundle);
//        parent.startActivityForResult(intent, MagicSetting.REQUEST_CODE_GAME_IMAGE);
    }

    public void showUpdateTipImage() {
        if (preview_img != null) {
            preview_img.setVisibility(View.VISIBLE);
        }
    }

}
