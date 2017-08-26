package com.systemteam.activity;

import android.content.Intent;
import android.os.Bundle;

import com.stephentuso.welcome.BasicPage;
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

//                .page(new BasicPage(R.drawable.ic_front_desk_white,
//                .page(new BasicPage(R.drawable.welcome1,
                .page(new BasicPage(R.drawable.welcome2,
                        getString(R.string.welcome_title1),
                        getString(R.string.welcome_decr1))
                        .background(R.color.orange_background)
                )

//                .page(new BasicPage(R.drawable.ic_thumb_up_white,
                .page(new BasicPage(R.drawable.welcome1,
                        getString(R.string.welcome_title2),
                        getString(R.string.welcome_decr2))
                        .background(R.color.red_background)
                )

                .page(new BasicPage(R.drawable.welcome3,
                        getString(R.string.welcome_title3),
                        getString(R.string.welcome_decr3))
                        .background(R.color.purple_background)
                )

                /*.page(new ParallaxPage(R.layout.parallax_example,
                        getString(R.string.welcome_title3),
                        getString(R.string.welcome_decr3))
                        .lastParallaxFactor(2f)
                        .background(R.color.purple_background)
                )*/

                /*.page(new BasicPage(R.drawable.ic_edit_white,
                        "Customizable",
                        "All elements of the welcome screen can be customized easily.")
                        .background(R.color.blue_background)
                )*/

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
