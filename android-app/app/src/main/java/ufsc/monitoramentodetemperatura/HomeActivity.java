package ufsc.monitoramentodetemperatura;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;


public class HomeActivity extends AppCompatActivity{

    private String SHEETS_ID = "1sILuxZUnyl_7-MlNThjt765oWshN3Xs-PPLfqYe4DhI";
    private String TEMP_FILE = "temperatura";
    private String HORA_FILE = "hora";
    private String TEMPMAX_FILE = "temp_max";
    private String TEMPMIN_FILE = "temp_min";
    private String TEMPEXT_FILE = "temp_ext";
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    
    private TextView temperatura;
    private TextView horario;
    private TextView temperaturaMax;
    private TextView temperaturaMin;
    private TextView temperaturaExt;

    private Button profileButton;
    private Button mButton;

    private static int SHEET_DATA_OK;
    private static int ERRO = 666;
    private static int LIST_SIZE;
    private static int TAdados;
    
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LIST_SIZE = 1;
        setContentView(R.layout.activity_home);
        // Find view by id
        profileButton = findViewById(R.id.profile_button);
        mButton = findViewById(R.id.sheetsbutton);
        temperatura = findViewById(R.id.temperatura);
        horario = findViewById(R.id.horario);
        temperaturaMax = findViewById(R.id.temperaturaMax);
        temperaturaMin = findViewById(R.id.temperaturaMin);
        temperaturaExt = findViewById(R.id.temperaturaExt);
        // Setting initial values to the views
        temperatura.setText(R.string.temperatura_zero);
        temperaturaMax.setText(R.string.temperatura_zero);
        temperaturaMin.setText(R.string.temperatura_zero);
        temperaturaExt.setText(R.string.temperatura_zero);
        horario.setText(R.string.horario_zero);
        TAdados = 15;
        // Getting the ID written on "GetSheetsId"
        SHEETS_ID = getIntent().getStringExtra("SHEETS_ID");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        // On Click Listeners
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoProfileActivity();
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SHEET_DATA_OK = 1;
                gotoGetSheetsDataActivity();
            }
        } );


    }

    @Override
    protected void onStart() {
        super.onStart();
        /*Check if it's the fist time starting*/
        if(SHEET_DATA_OK == 1){
            /* Check if there is data on the Intent */
            if((getIntent().getIntExtra("ERRO",ERRO)) == ERRO) {
                /* Getting the data from Intent */
                temperatura.setText((getIntent().getStringExtra("TEMPERATURA")).concat("°C"));
                horario.setText(getIntent().getStringExtra("HORA"));
                temperaturaMax.setText(getIntent().getStringExtra("TEMP_MAX").concat("°C"));
                temperaturaMin.setText(getIntent().getStringExtra("TEMP_MIN").concat("°C"));
                temperaturaExt.setText(getIntent().getStringExtra("TEMP_EXT").concat("°C"));
                LIST_SIZE = getIntent().getIntExtra("LIST_SIZE", 1);
                SHEET_DATA_OK = 0;
                writingAll();
                onTimeset(horario.getText().toString());
            }
        }else{
            /* Reading the data from Cellphone Memory <Files> */
            readingAll();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        /* Checking if there is data on the views */
        if(LIST_SIZE != 1) {
            writingAll();
        }
    }

    private void readingAll(){
        readFile(TEMP_FILE, temperatura);
        readFile(HORA_FILE, horario);
        readFile(TEMPMAX_FILE, temperaturaMax);
        readFile(TEMPMIN_FILE, temperaturaMin);
        readFile(TEMPEXT_FILE, temperaturaExt);
    }

    private void writingAll(){
        writeFile(TEMP_FILE, temperatura.getText().toString());
        writeFile(HORA_FILE, horario.getText().toString());
        writeFile(TEMPMAX_FILE, temperaturaMax.getText().toString());
        writeFile(TEMPMIN_FILE, temperaturaMin.getText().toString());
        writeFile(TEMPEXT_FILE, temperaturaExt.getText().toString());
//            writeFile(LIST_SIZES, id.getText().toString());
    }

    private void gotoProfileActivity() {
        /* Changing to a different Activity */
        startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        finish();
    }

    private void gotoGetSheetsDataActivity(){
        /* Changing to a different Activity */
        Intent intent = new Intent(HomeActivity.this, GetSheetsDataActivity.class);
        /* Putting data on the Intent */
        intent.putExtra("SHEETS_ID",SHEETS_ID);
        intent.putExtra("LIST_SIZE1",LIST_SIZE);
        startActivity(intent);
    }

    void writeFile(String filename, String fileContents) {
        /* Opening and writing on a File*/
        FileOutputStream fos = null;
        try {
            fos = getApplicationContext().openFileOutput(filename,MODE_PRIVATE);
            fos.write(fileContents.getBytes());
//            Toast.makeText(this,"Saved to" + getFilesDir() + "/" + filename, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null){
                try {
                    /* Closing the File */
                    fos.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    void readFile(String filename, TextView set) {
        /* Opening and reading a File */
        FileInputStream fis = null;
        try {
            fis = getApplicationContext().openFileInput(filename);
            InputStreamReader irs = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(irs);
            StringBuilder sb = new StringBuilder();
            String text = "";
            while ((text = br.readLine()) != null){
                sb.append(text).append("\n");
            }
            set.setText(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fis != null){
                try {
                    /* Closing the File */
                    fis.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.O)
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

    public void onTimeset(String horario){
        String[] separated = horario.split(":");
        int hour = Integer.parseInt(separated[0]);
        int minute = Integer.parseInt(separated[1]);
        minute = minute + TAdados + 1;
        if(minute > 60 ){
            hour++;
            minute = minute - 60;
        }
        if(hour > 24){
            hour = hour - 24;
        }
        if(minute < 0 ){
            /*------------------------------------------------------------------- ERRO */
            minute = 0;
        }
        if(hour < 0 ){
            /*------------------------------------------------------------------- ERRO */
            hour = 0;
        }
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY,hour );
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 30);

        startAlarm(c);
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmManagerActivity.class);
        pendingIntent = PendingIntent.getBroadcast(this,1,intent,pendingIntent.FLAG_CANCEL_CURRENT);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(), pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+(60*1000*TAdados),60*1000*TAdados, pendingIntent);
    }

    private void cancelAlarm() {
        Intent intent = new Intent(this, AlarmManagerActivity.class);
        pendingIntent = PendingIntent.getBroadcast(this,1,intent,0);
        pendingIntent.cancel();
    }
}