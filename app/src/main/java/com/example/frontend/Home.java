package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.widget.GridLayout;
import android.widget.ListView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Home extends AppCompatActivity {

    TextToSpeech t1;

    private int[] menuItemsArray = {R.id.button1, R.id.button2,R.id.button3,R.id.button4};
    private String[] speekText = {"Account details","Voice assistant","transfer funds", "my deposits"};
    private java.lang.Class[] classArray = {MainActivity.class,MainActivity.class,MainActivity.class,MainActivity.class};
    int listViewLength = menuItemsArray.length;
    int selectedIndex;
    private boolean volume_up_pressed, volume_down_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });


        new Timer().schedule(
                new TimerTask(){
                    @Override
                    public void run(){
                        t1.speak("You are on home menu",TextToSpeech.QUEUE_FLUSH,null,null);
                    }}, 400);


    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volume_down_pressed = false;
            if (selectedIndex != -1)
                findViewById(menuItemsArray[selectedIndex]).setBackgroundColor(Color.parseColor("#e6e4e4"));
            selectedIndex = (selectedIndex + 1) % listViewLength;
            t1.speak(speekText[selectedIndex],TextToSpeech.QUEUE_FLUSH, null,null);
            findViewById(menuItemsArray[selectedIndex]).setBackgroundColor(Color.parseColor("#d8d8d8"));
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            volume_up_pressed = false;
            if (selectedIndex != -1)
                findViewById(menuItemsArray[selectedIndex]).setBackgroundColor(Color.parseColor("#e6e4e4"));
            selectedIndex = (selectedIndex - 1) % listViewLength;
            if (selectedIndex < 0) selectedIndex += listViewLength;
            t1.speak(speekText[selectedIndex],TextToSpeech.QUEUE_FLUSH, null,null);
            findViewById(menuItemsArray[selectedIndex]).setBackgroundColor(Color.parseColor("#d8d8d8"));
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volume_down_pressed = true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            volume_up_pressed = true;
        }

        if (volume_down_pressed && volume_up_pressed) {
            Intent myIntent = new Intent(Home.this,classArray[selectedIndex]);
            Home.this.startActivity(myIntent);
        }
        return false;
    }

}
