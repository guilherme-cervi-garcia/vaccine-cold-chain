import android.widget.Button;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
import androidx.appcompat.app.AppCompatActivity;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
*/
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;



import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


import com.google.api.services.sheets.v4.model.*;
import com.squareup.picasso.Picasso;

import android.Manifest;
import android.accounts.AccountManager;

import java.util.List;

import com.google.android.gms.tasks.Task;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveRequest;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


public class LIXO {
}
    /* Variaveis diretamente ligadas a leitura da planilha */

    GoogleAccountCredential mCredential;
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private String spreadsheetId = "1JhHi4aaZXbRxZk_1zXU_wcRTwDIYo2JYjGYDhnjcetg";
    private Button mButton;
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };

    private static final String APLICATION_NAME = "Monitoramento de Temperatura" ;
    private static Sheets sheetsService;


    // NOTIFICACOES

//    private void checaDado(float dado) {
//        if((8 > dado && dado >= 7) || (3 >= dado && dado > 2)){
//            Notification noti = new Notification.Builder(this)
//                    .setContentTitle("ALERTA: Temperatura próxima do limite")
//                    .setContentText("A temperatura do seu refrigerador está em: "+ dado +"°C")
//                    .build();
//       }
//
//        if(dado == 8 || dado == 2){
//            Notification noti = new Notification.Builder(this)
//                    .setContentTitle("ALERTA: Temperatura próxima do limite")
//                    .setContentText("A temperatura do seu refrigerador está em: "+ dado +"°C")
//                    .build();
//            for(var i=0;i<j;i++) MailApp.sendEmail(v3[i].getValue(),"ALERTA: Temperatura no limite","A temperatura do seu refrigerador está em: "+v2.getValue()+"°C");
//        }
//
//        if(dado > 8 || dado < 2){
//            Notification noti = new Notification.Builder(this)
//                    .setContentTitle("ALERTA: Temperatura próxima do limite")
//                    .setContentText("A temperatura do seu refrigerador está em: "+ dado +"°C")
//                    .build();
//           for(var i=0;i<2;i++) MailApp.sendEmail(v3[i].getValue(),"ALERTA: Temperatura ultrapassou o limite","A temperatura do seu refrigerador está em: "+v2.getValue()+"°C");
//        }
//    }

    /* Metodos diretamente ligados a leitura da planilha */

    /*
    COLOCAR NA ONCREATE
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(HomeActivity.this);

        mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
            .setBackOff(new ExponentialBackOff());
        if(acct != null) mCredential.setSelectedAccount(acct.getAccount());
        try {
            getResultsFromApi();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(HomeActivity.this,"FALHA1",Toast.LENGTH_LONG).show();
        } catch (GeneralSecurityException e){
            e.printStackTrace();
            Toast.makeText(HomeActivity.this,"FALHA2",Toast.LENGTH_LONG).show();
        }


    private void getResultsFromApi() throws IOException, GeneralSecurityException {
        HttpTransport transport = new com.google.api.client.http.javanet.NetHttpTransport(); // GoogleNetHttpTransport.newTrustedTransport(); // AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("Google Sheets API Android Quickstart")
                .build();
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            Toast.makeText(this,"Sem Conta Selecionada",Toast.LENGTH_LONG).show();
        }else{
            getDataFromApi();
        }
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            Toast.makeText(this,"Erro ao adquirir o GooglePlayServices",Toast.LENGTH_LONG).show();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =  GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private List<String> getDataFromApi() throws IOException {
        String range = "Pg1!B:D";
        List<String> results = new ArrayList<String>();

        ValueRange response = this.mService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        if(response == null){
            Toast.makeText(this, "Response == null", Toast.LENGTH_LONG).show();
            return null;
        }else {
            List<List<Object>> values = response.getValues();
            if (values != null) {
                results.add("Name, Major");
                for (List row : values) {
                    results.add(row.get(2) + ", " + row.get(4));
                }
            }

            Toast.makeText(this, "resultados adquiridos", Toast.LENGTH_LONG).show();
            return results;
        }
    }
*/
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

//    private static Credential authorize () throws IOException , GeneralSecurityException {
//        InputStream in = HomeActivity.class.getResourceAsStream("/Credentials.json");
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));
//        List<String> scopes = Arrays.asList(SCOPES);
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), clientSecrets, scopes)
//                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
//                .setAccessType("offline")
//                .build();
//        Credential credential = new AuthorizationCodeInstalledApp(flow,new LocalServerReceiver()).authorize("user");
//        return credential;
//    }
//
//    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
//        Credential credential = authorize();
//        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),JacksonFactory.getDefaultInstance(),credential)
//                .setApplicationName(APLICATION_NAME)
//                .build();
//    }
//
//    private List<String> getDataFromApi() throws IOException, GeneralSecurityException {
//        sheetsService = getSheetsService();
//        String range = "Pg1!B:D";
//        List<String> results = new ArrayList<String>();
//
//        ValueRange response = sheetsService.spreadsheets().values()
//                .get(spreadsheetId, range)
//                .execute();
//        if (response == null) {
//            Toast.makeText(this, "Response == null", Toast.LENGTH_LONG).show();
//            return null;
//        } else {
//            List<List<Object>> values = response.getValues();
//            if (values != null) {
//                results.add("Name, Major");
//                for (List row : values) {
//                    results.add(row.get(2) + ", " + row.get(4));
//                }
//            }
//
//            Toast.makeText(this, "resultados adquiridos", Toast.LENGTH_LONG).show();
//            return results;
//        }
//    }





//    implementation 'com.google.oauth-client:google-oauth-client-jetty:1.23.0'
//    packagingOptions {
//        exclude 'META-INF/DEPENDENCIES'
//        exclude 'META-INF/LICENSE'
//        exclude 'META-INF/LICENSE.txt'
//        exclude 'META-INF/license.txt'
//        exclude 'META-INF/NOTICE'
//        exclude 'META-INF/NOTICE.txt'
//        exclude 'META-INF/notice.txt'
//        exclude 'META-INF/ASL2.0'
//    }