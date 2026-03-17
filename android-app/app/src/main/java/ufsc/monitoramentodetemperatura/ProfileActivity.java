package ufsc.monitoramentodetemperatura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profile_image;
    private TextView name;
    private TextView email;
    private TextView id;
    private Button soBtn;
    private Button backHome;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile_image = findViewById(R.id.profile_image);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        id = findViewById(R.id.id);
        soBtn = findViewById(R.id.sign_out_button);
        backHome = findViewById(R.id.back_to_home);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(SheetsScopes.SPREADSHEETS_READONLY))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoHomeActivity();
            }
        });

        soBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    private void signOut (){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        gotoMainActivity();
                    }
                });
    }

    private void gotoMainActivity() {
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        finish();
    }

    private void gotoHomeActivity() {
        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
        finish();
    }

    private void handleSignInResult(GoogleSignInAccount account){
        if(account != null){
            name.setText(account.getDisplayName());
            email.setText(account.getEmail());
            id.setText(account.getId());
            Picasso.get().load(account.getPhotoUrl()).placeholder(R.mipmap.ic_launcher).into(profile_image);
        }else {
            gotoMainActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Task<GoogleSignInAccount> opr = mGoogleSignInClient.silentSignIn();
        if (opr.isSuccessful()){
            GoogleSignInAccount result = opr.getResult();
            handleSignInResult(result);
        }else {
            gotoMainActivity();
        }
    }
}