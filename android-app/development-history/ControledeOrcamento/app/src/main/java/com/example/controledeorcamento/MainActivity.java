package com.example.controledeorcamento;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button addoneB;
    private Button addtwoB;
    private Button addthreeB;

    int  valone;
    int  valtwo;
    int  valthree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addoneB = findViewById(R.id.addone);
        addtwoB = findViewById(R.id.addtwo);
        addthreeB = findViewById(R.id.addthree);

        addoneB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addone();
            }
        });
        addtwoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addtwo();
            }
        } );
        addthreeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addthree();
            }
        } );
    }

    void addone(){
        valone++;
    }

    void addtwo(){
        valtwo++;
    }

    void addthree(){
        valthree++;
    }
}