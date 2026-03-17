package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    public Button B;
    public EditText H;
    public EditText M;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        B = findViewById(R.id.button);
        H = findViewById(R.id.entrada);
        M = findViewById(R.id.entrada2);
        createNotificationChannel();
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int h = Integer.parseInt(H.getText().toString());
                int m = Integer.parseInt(M.getText().toString());
                if(m > 60 || m < 0){
                    m = 0;
                }
                if(h > 24 || h < 0){
                    h = 0;
                }
                onTimeset(h,m);
            }
        });


    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = channelName ;
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(channelID, name, importance);

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    public void onTimeset(int hour , int minute){
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY,hour );
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);
            startAlarm(c);
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmManagerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,intent,0);
        alarmManager.setExact(alarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
}