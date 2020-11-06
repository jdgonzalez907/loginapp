package com.jdgonzalez907.loginapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jdgonzalez907.loginapp.models.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User user = (User) getIntent().getSerializableExtra("user");

        TextView textName = (TextView) findViewById(R.id.txtNames);
        textName.setText(user.getFullName());
    }
}