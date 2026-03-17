package com.example.sheetsandjava;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;

import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Graph graph;
    private List<List<Object>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graph = new Graph()

    }


}