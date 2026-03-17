package ufsc.monitoramentodetemperatura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmManagerActivity extends BroadcastReceiver {
    public GoogleAccountCredential mCredential;
    public static final String channelID = "channelID";
    String temperatura;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;

    public static int lim_sup_temp = 7;
    public static int lim_inf_temp = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        mCredential = GetSheetsDataActivity.getmCredential();
        try {
            new MakeRequestTask(mCredential).execute();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        builder = new NotificationCompat.Builder(context, channelID);
        notificationManager = NotificationManagerCompat.from(context);
    }

    public void Notify(){
        builder .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Monitoramento da Temperatura")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if(Double.parseDouble(temperatura) > lim_sup_temp){
            builder .setContentText("Temperatura acima: "+temperatura);
        }
        if(Double.parseDouble(temperatura) > lim_sup_temp - 2 && Double.parseDouble(temperatura) < lim_sup_temp){
            builder .setContentText("Temperatura perto do limite superior: "+temperatura);
        }
        if(Double.parseDouble(temperatura) < lim_inf_temp){
            builder .setContentText("Temperatura abaixo: "+temperatura);
        }
        if(Double.parseDouble(temperatura) < lim_inf_temp + 2 && Double.parseDouble(temperatura) > lim_inf_temp){
            builder .setContentText("Temperatura perto do limite inferior: "+temperatura);
        }
        if(Double.parseDouble(temperatura) > lim_inf_temp + 2 && Double.parseDouble(temperatura) < lim_sup_temp - 2 ){
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) throws GeneralSecurityException, IOException {
            HttpTransport transport = new com.google.api.client.http.javanet.NetHttpTransport();// GoogleNetHttpTransport.newTrustedTransport();//AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         *
         * Nosso link
         * https://docs.google.com/spreadsheets/d/1JhHi4aaZXbRxZk_1zXU_wcRTwDIYo2JYjGYDhnjcetg/edit#gid=0
         */
        private List<String> getDataFromApi() throws IOException {
            String spreadsheetId = "1hdCeBqLnC8L138ypRposgZX0T-os5HdMSmpvm4Bi33g";
            String range = "pg1!A:H";
            List<String> results = new ArrayList<String>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();

            temperatura = values.get(values.size()-1).get(2).toString();
            Notify();
            return results;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(List<String> output) {
        }

        @Override
        protected void onCancelled() {
        }
    }

}