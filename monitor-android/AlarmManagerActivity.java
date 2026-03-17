package ufsc.monitoramentodetemperatura;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class AlarmManagerActivity extends BroadcastReceiver {
    GoogleAccountCredential mCredential;
    @Override
    public void onReceive(Context context, Intent intent) {
        GetSheetsDataActivity.getmCredential();
        try {
            new MakeRequestTask(mCredential).execute();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
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