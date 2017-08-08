package com.systemteam.activity;

import android.content.Intent;
import android.os.Bundle;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.ParallaxPage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;
import com.systemteam.R;
import com.systemteam.util.LogTool;

public class MyWelcomeActivity extends WelcomeActivity {
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

                .page(new BasicPage(R.drawable.ic_edit_white,
                        "Customizable",
                        "All elements of the welcome screen can be customized easily.")
                        .background(R.color.blue_background)
                )

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
        startActivity(new Intent(MyWelcomeActivity.this, com.systemteam.welcome.WelcomeActivity.class));
        super.completeWelcomeScreen();
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
