package ufsc.monitoramentodetemperatura;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.sheets.v4.SheetsScopes;

public class GetSheetsActivity extends AppCompatActivity {
    private Button home;
    private EditText codigosheets;
    public String idSheets;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_sheets2);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(SheetsScopes.SPREADSHEETS_READONLY))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        home = findViewById(R.id.buttonSI);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codigosheets = findViewById(R.id.sheetsId);
                idSheets = codigosheets.getText().toString();
                gotoHomeActivity();
            }
        });
    }

    private void gotoHomeActivity() {
        Intent intent = new Intent(GetSheetsActivity.this, HomeActivity.class);
        intent.putExtra("SHEETS_ID",idSheets);
        startActivity(intent);
        finish();
    }

}