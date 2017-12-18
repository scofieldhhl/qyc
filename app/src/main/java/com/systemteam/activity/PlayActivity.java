package com.systemteam.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.adapter.VideoPlayerListAdapter;
import com.systemteam.util.LogTool;

import java.util.ArrayList;
import java.util.List;


/**
 * 单独的视频播放页面
 */
public class PlayActivity extends BaseActivity implements VideoAllCallBack, View.OnClickListener {

    public static final String KEY_PLAY_PATH_LIST = "play_path_list";

    public final static String KEY_PATH_VIDEO = "path_vido";
    public final static String IMG_TRANSITION = "IMG_TRANSITION";
    public final static String TRANSITION = "TRANSITION";
    public final static String FILTER_VIDEO_FILE_NAME = "-reformat-";
    public final static String SUBTITLE_SUFFIX = ".srt";

    SampleVideo videoPlayer;

    OrientationUtils orientationUtils;

    private boolean isTransition;

    private static final int PARSE_SRT = 0;
    private final int REFRESH_SUBTITLE = 100;

    private int mIndexFocus = -1;
    private List<SwitchVideoModel> mList;
    private TextView tvSrt;
    private Handler handler;
    private CheckBox mCcSwitch;
    private ImageButton mListImb;
    private boolean isHideSrt;

    private PopupWindow mPopupWindow;
    private VideoPlayerListAdapter mAdapter;
    private int mHeight;
    private int mWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        isTransition = getIntent().getBooleanExtra(TRANSITION, false);
        initializeView();
        initializeData();
    }

    protected void setContentView() {
        setContentView(R.layout.activity_play);
    }

    protected void initializeView() {
        videoPlayer = (SampleVideo) findViewById(R.id.video_player);
        ImageView ivPlayNext = (ImageView) findViewById(R.id.iv_playnext);
        ivPlayNext.setOnClickListener(this);

        mCcSwitch = (CheckBox) findViewById(R.id.cb_cc);
        mCcSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    LogTool.i("显示字幕");
                    isHideSrt = false;
                } else {
                    tvSrt.setVisibility(View.GONE);
                    LogTool.i("关闭字幕");
                    isHideSrt = true;
                }
            }
        });

        mListImb = (ImageButton) findViewById(R.id.video_list);

    }

    protected void initializeData() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;

        init();

        initPlaylistPop();

        mListImb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlaylistPop();
            }
        });
    }


    /**
     * 跳转到视频播放
     *
     * @param activity
     * @param view
     */
    public static void goToVideoPlayer(Activity activity, View view) {
        Intent intent = new Intent(activity, PlayActivity.class);
        intent.putExtra(PlayActivity.TRANSITION, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Pair pair = new Pair<>(view, PlayActivity.IMG_TRANSITION);
            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity, pair);
            ActivityCompat.startActivity(activity, intent, activityOptions.toBundle());
        } else {
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    private void init() {
//        String url = "http://baobab.wdjcdn.com/14564977406580.mp4";
        //需要路径的
        //videoPlayer.setUp(url, true, new File(FileUtils.getPath()), "");

        //借用了jjdxm_ijkplayer的URL
        /*String source1 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
        String name = "普通";
        SwitchVideoModel switchVideoModel = new SwitchVideoModel(name, source1);

        String source2 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4";
        String name2 = "清晰";
        SwitchVideoModel switchVideoModel2 = new SwitchVideoModel(name2, source2);*/
        String playPath = getIntent().getStringExtra(KEY_PATH_VIDEO);
        if (playPath == null) {
            return;
        }
        SwitchVideoModel mVideoModelFocus = initModel(playPath);
        mList = new ArrayList<>();
        ArrayList<String> pathList = getIntent().getStringArrayListExtra(KEY_PLAY_PATH_LIST);
        if (pathList != null && pathList.size() > 0) {
            for (int i = 0; i < pathList.size(); i++) {
                String str = pathList.get(i);
                if (playPath.equalsIgnoreCase(str)) {
                    mIndexFocus = i;
                }
                mList.add(initModel(str));
            }
        } else {
            mList.add(mVideoModelFocus);
        }
        LogTool.d("size:" + mList.size());
        videoPlayer.setUp(mList, mIndexFocus, true, "");
        videoPlayer.setKeepScreenOn(true);
        videoPlayer.setVideoAllCallBack(this);
        //增加封面
        /*ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.xxx1);
        videoPlayer.setThumbImageView(imageView);*/

        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        videoPlayer.getTitleTextView().setText(mVideoModelFocus.getName());

        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);

        //设置旋转
        orientationUtils = new OrientationUtils(this, videoPlayer);

        //设置全屏按键功能
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationUtils.resolveByClick();
            }
        });

        //videoPlayer.setBottomProgressBarDrawable(getResources().getDrawable(R.drawable.video_new_progress));
        //videoPlayer.setDialogVolumeProgressBar(getResources().getDrawable(R.drawable.video_new_volume_progress_bg));
        //videoPlayer.setDialogProgressBar(getResources().getDrawable(R.drawable.video_new_progress));
        //videoPlayer.setBottomShowProgressBarDrawable(getResources().getDrawable(R.drawable.video_new_seekbar_progress),
        //getResources().getDrawable(R.drawable.video_new_seekbar_thumb));
        //videoPlayer.setDialogProgressColor(getResources().getColor(R.color.colorAccent), -11);

        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);

        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //过渡动画
        initTransition();
    }

    private SwitchVideoModel initModel(String path) {
        SwitchVideoModel model = null;
        if (path != null) {
            String[] arrPath = path.split("\\/");
            model = new SwitchVideoModel(arrPath[arrPath.length - 1], path);
        }
        return model;
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orientationUtils != null)
            orientationUtils.releaseListener();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onBackPressed() {
        //先返回正常状态
//        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            videoPlayer.getFullscreenButton().performClick();
//            return;
//        }
        //释放所有
        videoPlayer.setStandardVideoAllCallBack(null);
        GSYVideoPlayer.releaseAllVideos();
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onBackPressed();
        } else {
            finish();
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }


    private void initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            ViewCompat.setTransitionName(videoPlayer, IMG_TRANSITION);
            addTransitionListener();
            startPostponedEnterTransition();
        } else {
            videoPlayer.startPlayLogic();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean addTransitionListener() {
        Transition transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            transition.addListener(new OnTransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    super.onTransitionEnd(transition);
                    videoPlayer.startPlayLogic();
                    transition.removeListener(this);
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                PlayActivity.this.finish();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPrepared(String url, Object... objects) {
        /*String srtPath = getSrtPath(url);
        if (TextUtils.isEmpty(srtPath)) {
            LogTool.w("srtPath is empty");
            tvSrt.setVisibility(View.GONE);
            return;
        }

        showSrt(srtPath);

        MaterialDesignUtils.setFullScreenAndHideNavigation(this);*/
    }

    @Override
    public void onClickStartIcon(String url, Object... objects) {

    }

    @Override
    public void onClickStartError(String url, Object... objects) {

    }

    @Override
    public void onClickStop(String url, Object... objects) {

    }

    @Override
    public void onClickStopFullscreen(String url, Object... objects) {

    }

    @Override
    public void onClickResume(String url, Object... objects) {

    }

    @Override
    public void onClickResumeFullscreen(String url, Object... objects) {

    }

    @Override
    public void onClickSeekbar(String url, Object... objects) {

    }

    @Override
    public void onClickSeekbarFullscreen(String url, Object... objects) {

    }

    @Override
    public void onAutoComplete(String url, Object... objects) {
        LogTool.d("onAutoComplete");
        /*if (++mIndexFocus < mList.size()) {

        } else {
            PlayActivity.this.finish();
        }*/
        playVideoByPosition(++mIndexFocus % mList.size(), 500);

    }

    @Override
    public void onEnterFullscreen(String url, Object... objects) {

    }

    @Override
    public void onQuitFullscreen(String url, Object... objects) {

    }

    @Override
    public void onQuitSmallWidget(String url, Object... objects) {

    }

    @Override
    public void onEnterSmallWidget(String url, Object... objects) {

    }

    @Override
    public void onTouchScreenSeekVolume(String url, Object... objects) {

    }

    @Override
    public void onTouchScreenSeekPosition(String url, Object... objects) {

    }

    @Override
    public void onTouchScreenSeekLight(String url, Object... objects) {

    }

    @Override
    public void onPlayError(String url, Object... objects) {

    }


    @Override
    public void onClick(View view) {
        playVideoByPosition(++mIndexFocus % mList.size(), 500);

    }

    private void playVideoByPosition(final int position, int delayTime) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                videoPlayer.setUp(mList, position, true, "");
                videoPlayer.startPlayLogic();
                if (mAdapter != null) {
                    mAdapter.updatePlayPosition(position);
                }

                //增加title
                videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
                if(mIndexFocus < mList.size()){
                    videoPlayer.getTitleTextView().setText(mList.get(mIndexFocus).getName());
                }
                //设置返回键
                videoPlayer.getBackButton().setVisibility(View.VISIBLE);
            }
        }, delayTime);
    }





    private void initPlaylistPop() {
        View popupView = getLayoutInflater().inflate(R.layout.pop_video_list, null);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);    //设置一个动画。

        RecyclerView recyclerView = (RecyclerView) popupView.findViewById(R.id.pop_video_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlayActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.addItemDecoration(new DividerItemDecoration(PlayActivity.this, R.drawable.mp_list_divider));
        mAdapter = new VideoPlayerListAdapter(PlayActivity.this, mList, mIndexFocus);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new VideoPlayerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                mIndexFocus = pos;
                playVideoByPosition(pos, 300);
            }
        });
    }

    private void showPlaylistPop() {
        if (mPopupWindow != null) {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                if (mHeight > 0) {
                    mPopupWindow.setWidth(mHeight / 2);
                }
            } else {
                if (mWidth > 0) {
                    mPopupWindow.setWidth(mWidth / 2);
                }
            }
            mPopupWindow.setFocusable(false);

            mPopupWindow.showAtLocation(videoPlayer, Gravity.RIGHT | Gravity.TOP,
                    0, 0);

        }
    }
}
