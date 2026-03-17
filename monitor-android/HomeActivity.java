package ufsc.monitoramentodetemperatura;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class HomeActivity extends AppCompatActivity{

    private String SHEETS_ID = "1sILuxZUnyl_7-MlNThjt765oWshN3Xs-PPLfqYe4DhI";
    private String TEMP_FILE = "temperatura";
    private String HORA_FILE = "hora";
    private String TEMPMAX_FILE = "temp_max";
    private String TEMPMIN_FILE = "temp_min";
    private String TEMPEXT_FILE = "temp_ext";
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
//    private String LIST_SIZES = "list_size";

    private TextView temperatura;
    private TextView horario;
    private TextView temperaturaMax;
    private TextView temperaturaMin;
    private TextView temperaturaExt;
//    private TextView id;

    private Button profileButton;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private Button mButton;

    private static int SHEET_DATA_OK;
    private static int ERRO = 666;
    private static int LIST_SIZE;
    private int TAdados;


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
        TAdados = 10;
        // Getting the ID written on "GetSheetsId"
        SHEETS_ID = getIntent().getStringExtra("SHEETS_ID");
        // Connecting to a Google Account
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(SheetsScopes.SPREADSHEETS_READONLY))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

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
//                onTimeset(horario.toString());
//                id.setText(String.valueOf(LIST_SIZE));
            }
        }else{
            /* Reading the data from Cellphone Memory <Files> */
            readFile(TEMP_FILE, temperatura);
            readFile(HORA_FILE, horario);
            readFile(TEMPMAX_FILE, temperaturaMax);
            readFile(TEMPMIN_FILE, temperaturaMin);
            readFile(TEMPEXT_FILE, temperaturaExt);
//            readFile(LIST_SIZES, id);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        /* Checking if there is data on the views */
        if(LIST_SIZE != 1) {
            writeFile(TEMP_FILE, temperatura.getText().toString());
            writeFile(HORA_FILE, horario.getText().toString());
            writeFile(TEMPMAX_FILE, temperaturaMax.getText().toString());
            writeFile(TEMPMIN_FILE, temperaturaMin.getText().toString());
            writeFile(TEMPEXT_FILE, temperaturaExt.getText().toString());
//            writeFile(LIST_SIZES, id.getText().toString());
        }
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
        minute = minute + TAdados;
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