package com.example.footballapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class listItemDetails extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newcastle_page2);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);

      //  String[] myKeys = getResources().getStringArray(R.array.sections);
    //    myTextView.setText(mykeys[position]);


    }
}
