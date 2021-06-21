package com.example.footballapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.ResourceBundle;

public class MainActivity extends AppCompatActivity implements MainMenu.OnFragmentInteractionListener,
ChooseTeam.OnFragmentInteractionListener

{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
