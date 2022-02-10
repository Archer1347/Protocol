package com.protocol.core.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.modulea.AModuleActivity;
import com.example.modulea.TestProtocol;
import com.protocol.core.ProtocolFactory;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AModuleActivity.class)));
    }
}
