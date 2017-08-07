package com.systemteam.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.FragmentWelcomePage;
import com.stephentuso.welcome.ParallaxPage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;
import com.systemteam.R;
import com.systemteam.util.LogTool;
import com.systemteam.welcome.fragment.outlayer.LoginAnimFragment;

public class MyWelcomeActivity extends WelcomeActivity {
    LoginAnimFragment mFragment = new LoginAnimFragment();
    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultTitleTypefacePath("Montserrat-Bold.ttf")
                .defaultHeaderTypefacePath("Montserrat-Bold.ttf")

                .page(new BasicPage(R.drawable.ic_front_desk_white,
                        "Welcome",
                        "An Android library for onboarding, instructional screens, and more")
                        .background(R.color.orange_background)
                )

                .page(new BasicPage(R.drawable.ic_thumb_up_white,
                        "Simple to use",
                        "Add a welcome screen to your app with only a few lines of code.")
                        .background(R.color.red_background)
                )

                .page(new ParallaxPage(R.layout.parallax_example,
                        "Easy parallax",
                        "Supply a layout and parallax effects will automatically be applied")
                        .lastParallaxFactor(2f)
                        .background(R.color.purple_background)
                )

                /*.page(new BasicPage(R.drawable.ic_edit_white,
                        "Customizable",
                        "All elements of the welcome screen can be customized easily.")
                        .background(R.color.blue_background)
                )*/
                .page(new FragmentWelcomePage() {
                    @Override
                    protected Fragment fragment() {
                        return mFragment;
                    }
                }.background(R.color.red_background))

                .swipeToDismiss(false)
                .backButtonNavigatesPages(false)
                .exitAnimation(android.R.anim.fade_out)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogTool.d("onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        LogTool.d("onDestroy");
        super.onDestroy();
    }

    @Override
    protected void completeWelcomeScreen() {
        LogTool.d("completeWelcomeScreen");
        if(mFragment == null){
            mFragment = new LoginAnimFragment();
        }
        return;
//        super.completeWelcomeScreen();
    }

    @Override
    protected void cancelWelcomeScreen() {
        LogTool.d("cancelWelcomeScreen");
        super.cancelWelcomeScreen();
    }

    @Override
    public void onBackPressed() {
        LogTool.d("onBackPressed");
        finish();
        super.onBackPressed();
    }



    public static String welcomeKey() {
        return "WelcomeScreen";
    }

}
