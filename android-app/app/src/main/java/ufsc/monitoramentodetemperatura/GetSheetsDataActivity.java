package ufsc.monitoramentodetemperatura;

        import com.github.mikephil.charting.charts.LineChart;
        import com.github.mikephil.charting.data.Entry;
        import com.github.mikephil.charting.data.LineData;
        import com.github.mikephil.charting.data.LineDataSet;
        import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.GoogleApiAvailability;
        import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
        import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
        import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
        import com.google.api.client.http.HttpTransport;
        import com.google.api.client.json.JsonFactory;
        import com.google.api.client.json.jackson2.JacksonFactory;
        import com.google.api.client.util.ExponentialBackOff;
        import com.google.api.services.sheets.v4.SheetsScopes;
        import com.google.api.services.sheets.v4.model.*;
        import android.Manifest;
        import android.accounts.AccountManager;
        import android.app.Activity;
        import android.app.Dialog;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.text.method.ScrollingMovementMethod;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import java.io.IOException;
        import java.security.GeneralSecurityException;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.List;
        import pub.devrel.easypermissions.AfterPermissionGranted;
        import pub.devrel.easypermissions.EasyPermissions;


public class GetSheetsDataActivity extends Activity
        implements EasyPermissions.PermissionCallbacks {

    public static GoogleAccountCredential mCredential2;
    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private Button mCallApiButton;
    private Button mGoToHomeActivity;
    ProgressDialog mProgress;

    private static double TEMP_MAX;
    private static double TEMP_MIN;
    private static int LIST_SIZE;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int RESULT_OK = 1005;

    private static final String BUTTON_TEXT = "Gerar Grafico";
    private static final String BUTTON_TEXT_2 = "Ir para Pagina Principal";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };
    private List<List<Object>> tempList;
    private String sheets_id;

    private LineChart chart;

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TEMP_MAX = 666;
        TEMP_MIN = 666;
        LIST_SIZE = 1;
        LinearLayout activityLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);
        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mCallApiButton = new Button(this);
        mCallApiButton.setText(BUTTON_TEXT);
        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallApiButton.setEnabled(false);
                mOutputText.setText("");
                try {
                    getResultsFromApi();
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
                mCallApiButton.setEnabled(true);
            }
        });
        mGoToHomeActivity = new Button(this);
        mGoToHomeActivity.setText(BUTTON_TEXT_2);
        mGoToHomeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHomeActivity();
            }
         }
        );
        activityLayout.addView(mCallApiButton);
        activityLayout.addView(mGoToHomeActivity);
        mOutputText = new TextView(this);
        mOutputText.setLayoutParams(tlp);
        mOutputText.setPadding(16, 16, 16, 16);
        mOutputText.setVerticalScrollBarEnabled(true);
        mOutputText.setMovementMethod(new ScrollingMovementMethod());
        mOutputText.setText(
                "Click the \'" + BUTTON_TEXT +"\' button to test the API.");
        activityLayout.addView(mOutputText);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Sheets API ...");

        setContentView(activityLayout);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        chart = new LineChart(this);
        // enable touch gestures
        chart.setTouchEnabled(true);
        chart.setDragDecelerationFrictionCoef(0.9f);
        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);
        activityLayout.addView(chart,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LIST_SIZE = getIntent().getIntExtra("LIST_SIZE1",1);
        sheets_id = getIntent().getStringExtra("SHEETS_ID");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToHomeActivity();
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() throws GeneralSecurityException, IOException {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            mOutputText.setText(R.string.no_ntwk_connection);
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    private void goToHomeActivity()     {
        mProgress.dismiss();
        Intent intent = new Intent(GetSheetsDataActivity.this, HomeActivity.class);
        mCredential2 = mCredential;
        if(tempList != null) {
            if (TEMP_MAX == 666 || tempList.size() <= LIST_SIZE) {
                LIST_SIZE = 1;
                TEMP_MAX = Double.parseDouble(tempList.get(LIST_SIZE).get(2).toString());
                TEMP_MIN = Double.parseDouble(tempList.get(LIST_SIZE).get(2).toString());
            }
            for (int i = LIST_SIZE; i < tempList.size(); i++) {
                if (Double.parseDouble(tempList.get(i).get(2).toString()) > TEMP_MAX)
                    TEMP_MAX = Double.parseDouble(tempList.get(i).get(2).toString());
                if (Double.parseDouble(tempList.get(i).get(2).toString()) < TEMP_MIN)
                    TEMP_MIN = Double.parseDouble(tempList.get(i).get(2).toString());
            }

            LIST_SIZE = tempList.size();

            intent.putExtra("TEMPERATURA", tempList.get(tempList.size() - 1).get(2).toString());
            intent.putExtra("HORA", tempList.get(tempList.size() - 1).get(1).toString());

            if(TEMP_MAX > 100){
                intent.putExtra("TEMP_MAX",getString(R.string.erro));
            }else {
                intent.putExtra("TEMP_MAX", Double.toString(TEMP_MAX));
            }
            if(TEMP_MIN < -100){
                intent.putExtra("TEMP_MIN",getString(R.string.erro));
            }else {
                intent.putExtra("TEMP_MIN", Double.toString(TEMP_MIN));
            }

            intent.putExtra("TEMP_MIN", Double.toString(TEMP_MIN));
            intent.putExtra("TEMP_EXT", tempList.get(tempList.size()-1).get(3).toString());
            intent.putExtra("LIST_SIZE", LIST_SIZE);
            intent.putExtra("ERRO", 666);
        }else {
            intent.putExtra("ERRO", 667);
        }
        startActivity(intent);
        finish();
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() throws GeneralSecurityException, IOException {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(mCredential.newChooseAccountIntent(),REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText((String)("This app requires Google Play Services. Please install Google Play Services on your device and relaunch this app."));
                } else {
                    try {
                        getResultsFromApi();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (/*resultCode == RESULT_OK &&*/ data != null && data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        try {
                            getResultsFromApi();
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    try {
                        getResultsFromApi();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                GetSheetsDataActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService;
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
            if (values != null) {
                for (List<Object> row : values) {
                    if(values.indexOf(0) == 0) results.add(row.get(1) +" "+ row.get(2));
                    results.add(" " + row.get(1) +",                 " + row.get(2));
                }
            }
            tempList = values;
            return results;
        }



        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
            } else {
                LineDataSet lineDataSet = new LineDataSet(LineChartDataSet(), "Temperatura");
                ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
                lineDataSets.add(lineDataSet);

                LineData lineData = new LineData(lineDataSets);
                chart.setData(lineData);
                //chart.invalidate();
                /*
                output.add(0, "Data retrieved using the Google Sheets API:");
                mOutputText.setText(TextUtils.join("\n", output));
                 */
            }
        }

        private List<Entry> LineChartDataSet() {
            List<Entry> list = new ArrayList<Entry>();

            for(int i = 0 ;i< tempList.size()-1; i++){
                list.add(new Entry(i,Float.parseFloat(tempList.get(i+1).get(2).toString())));
            }

            return list;
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GetSheetsDataActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }

    public static GoogleAccountCredential getmCredential(){
        return mCredential2;
    }

}