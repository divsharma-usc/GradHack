package com.example.frontend;

import com.example.frontend.ViewPageAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class SliderActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private ViewPageAdapter myViewPagerAdapter;
    private int[] layouts;
    private String[] speaks = {"Volume up provides navigation, press volume up for next", "Press volume up and down to select an option, press them to move ahead"};
    private Button btnSkip, btnNext;
    private TextView[] dots;
    private TextToSpeech tts;
    private boolean volume_up_pressed, volume_down_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        layouts = new int[]{
                R.layout.slide1,
                R.layout.slide2
        };

        addBottomDots(0);
        changeStatusBarColor();
        removeNavigation();
        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        System.out.println(dotsLayout);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);
        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        myViewPagerAdapter = new ViewPageAdapter(SliderActivity.this, layouts);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
                if (position == layouts.length - 1) {
                    btnNext.setText(getString(R.string.start));
                    btnSkip.setVisibility(View.GONE);
                } else {
                    btnNext.setText(getString(R.string.next));
                    btnSkip.setVisibility(View.VISIBLE);
                }
                speakOut(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page if true launch MainActivity
                int current = getItem(+1);

                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                    speakOut(current);
                } else {
                      launchHomeScreen();
                }
            }
        });

        new Timer().schedule(
                new TimerTask(){
                    @Override
                    public void run(){
                        tts.speak("You can press volume down to skip the tips",TextToSpeech.QUEUE_FLUSH,null,null);
                    }}, 400);

        new Timer().schedule(
                new TimerTask(){
                    @Override
                    public void run(){
                        tts.speak("Volume up provides navigation, press volume up for next tip",TextToSpeech.QUEUE_FLUSH,null,null);
                    }}, 400);
    }


    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;", 0));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }


    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void speakOut(int pos){
        viewPager.setCurrentItem(pos);
        viewPager.setClickable(false);
        viewPager.setEnabled(false);
        btnNext.setClickable(false);
        tts.speak(speaks[pos],TextToSpeech.QUEUE_FLUSH,null,null);
        viewPager.setClickable(true);
        viewPager.setEnabled(true);
        btnNext.setClickable(true);
    }


    //Using Volumes buttons to navigate and select
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volume_down_pressed = false;
            btnSkip.performClick();
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            volume_up_pressed = false;
            if(viewPager.getCurrentItem() < layouts.length - 1 )
                btnNext.performClick();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Boolean response = false;
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volume_down_pressed = true;
            response = true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            volume_up_pressed = true;
            response = true;
        }
        if (volume_down_pressed && volume_up_pressed) {
            btnNext.performClick();
        }
        return response;
    }

    @Override
    protected void onResume() {
        super.onResume();
        removeNavigation();
    }

    @Override
    protected void onStart(){
        super.onStart();
        removeNavigation();
    }

    private void launchHomeScreen(){
        Intent myIntent = new Intent(SliderActivity.this,Home.class);
        SliderActivity.this.startActivity(myIntent);
    }

    private void removeNavigation(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void onPause(){
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }
}
