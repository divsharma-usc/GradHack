package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.lex.interactionkit.Response;
import com.amazonaws.mobileconnectors.lex.interactionkit.config.InteractionConfig;
import com.amazonaws.mobileconnectors.lex.interactionkit.ui.InteractiveVoiceView;

import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Home extends AppCompatActivity {

    TextToSpeech t1;

    private int[] menuItemsArray = {R.id.button1, R.id.button2,R.id.button3,R.id.button4};
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private String[] speekText = {"Account details","Voice assistant","transfer funds", "my deposits"};
    private java.lang.Class[] classArray = {MainActivity.class,ChatBotActivity.class,MainActivity.class,MainActivity.class};
    private int[] buttonArrayMain = {R.drawable.homeicon1,R.drawable.homeicon2,R.drawable.homeicon3,R.drawable.homeicon4};
    private int[] buttonArraySelected = {R.drawable.homeicona,R.drawable.homeiconb,R.drawable.homeiconc,R.drawable.homeicond};
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

        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(Home.this,ChatBotActivity.class);
                Home.this.startActivity(myIntent);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(Home.this,ChatBotActivity.class);
                Home.this.startActivity(myIntent);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(Home.this,classArray[selectedIndex]);
                Home.this.startActivity(myIntent);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(Home.this,classArray[selectedIndex]);
                Home.this.startActivity(myIntent);
            }
        });

        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d("MainActivity", "AWSMobileClient is instantiated and you are connected to AWS!");
            }
        }).execute();

        init();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        for(int index = 0 ; index < menuItemsArray.length;index++){
            Button myButton = (Button)findViewById(menuItemsArray[index]);
            myButton.setBackgroundColor(Color.parseColor("#c7000f"));
            myButton.setTextColor(Color.parseColor("#ffffff"));
            Drawable top = getResources().getDrawable(buttonArrayMain[index],null);
            myButton.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volume_down_pressed = false;
            if (selectedIndex != -1) {
                Button myButton = findViewById(menuItemsArray[selectedIndex]);
                myButton.setBackgroundColor(Color.parseColor("#ffffff"));
                myButton.setTextColor(Color.parseColor("#c7000f"));
                Drawable top = getResources().getDrawable(buttonArraySelected[selectedIndex],null);
                myButton.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
                t1.speak(speekText[selectedIndex],TextToSpeech.QUEUE_FLUSH, null,null);
            }
            selectedIndex = (selectedIndex + 1) % listViewLength;
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {

            volume_up_pressed = false;
            if (selectedIndex != -1) {
                Button myButton = findViewById(menuItemsArray[selectedIndex]);
                myButton.setBackgroundColor(Color.parseColor("#ffffff"));
                myButton.setTextColor(Color.parseColor("#c7000f"));
                Drawable top = getResources().getDrawable(buttonArraySelected[selectedIndex],null);
                myButton.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
                t1.speak(speekText[selectedIndex],TextToSpeech.QUEUE_FLUSH, null,null);
            }
            selectedIndex = (selectedIndex - 1) % listViewLength;
            if (selectedIndex < 0) selectedIndex += listViewLength;
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        boolean response = false;
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volume_down_pressed = true;
            response = true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            volume_up_pressed = true;
            response = true;
        }

        if (volume_down_pressed && volume_up_pressed) {
            Intent myIntent = new Intent(Home.this,classArray[selectedIndex]);
            Home.this.startActivity(myIntent);
        }
        return response;
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            //t1.shutdown();
        }
        super.onPause();
    }

    public void onResume(){
        super.onResume();
        new Timer().schedule(
                new TimerTask(){
                    @Override
                    public void run(){
                        t1.speak("You are on home menu",TextToSpeech.QUEUE_FLUSH,null,null);
                    }}, 200);
    }

    public void init(){
        InteractiveVoiceView voiceView = findViewById(R.id.voiceInterface);

        voiceView.setInteractiveVoiceListener(new InteractiveVoiceView.InteractiveVoiceListener() {
            @Override
            public void dialogReadyForFulfillment(Map<String, String> slots, String intent) {

            }

            @Override
            public void onResponse(Response response) {

            }

            @Override
            public void onError(String responseText, Exception e) {

            }
        });


        voiceView.getViewAdapter().setCredentialProvider(AWSMobileClient.getInstance().getCredentialsProvider());

        //replace parameters with your botname, bot-alias
        voiceView.getViewAdapter()
                .setInteractionConfig(
                        new InteractionConfig("GradHack","newAlias"));

        voiceView.getViewAdapter()
                .setAwsRegion(getApplicationContext()
                        .getString(R.string.aws_region));

        voiceView.setCurrentRadius(20);
    }

}
