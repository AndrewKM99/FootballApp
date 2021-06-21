package com.example.footballapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final String Registration_URL = "http://192.168.0.11/register.php";
    ProgressBar progressBar;

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMPTY = "";

    private String username;
    private String password;
    private String confirmPassword;
    private String email;


    private EditText signupInputName;
    private EditText signupInputEmail;
            private EditText signupInputPassword;
    private Button signUpButton;
    private Button linkLoginButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        signupInputName = (EditText) findViewById(R.id.signup_input_layout_name);
        signupInputEmail = (EditText) findViewById(R.id.signup_input_email);
        signupInputPassword = (EditText) findViewById(R.id.signup_input_password);

        signUpButton = (Button) findViewById(R.id.btn_signup);
        linkLoginButton = (Button) findViewById(R.id.btn_link_login);

     //   progressBar = (ProgressBar) findViewById(R.id.progressBar) ;


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });


        linkLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        username = signupInputName.getText().toString().trim();
        password = signupInputPassword.getText().toString().trim();
        email = signupInputEmail.getText().toString().trim();
        if (validateInputs()) {
            submitForm();
        }

    }
    private void submitForm()
        {



                        registerUser(signupInputName.getText().toString(), signupInputEmail.getText().toString(), signupInputPassword.getText().toString());


        }

        private void registerUser(final String name, final String email, final String password)
        {
            String cancel_req_tag = "register";

        //   progressBar.setVisibility(View.VISIBLE);
            StringRequest strReq = new StringRequest(Request.Method.POST, Registration_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

Log.d(TAG, "Register Response: " + response);

                    try {


                        JSONObject obj = new JSONObject(response);
                        boolean error = obj.getBoolean("error");

                        if (!error) {
                            String user = obj.getJSONObject("user").getString("name");
                            Toast.makeText(getApplicationContext(), "Hello " + user + ", You are successfully Added!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMessage = obj.getString("ERROR");
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();

                        }

                    } catch (
                            JSONException e) {
                        e.printStackTrace();
                    }


                }

            },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    })
            {

                        protected Map<String, String> getParams() {

                            Map<String, String> details = new HashMap<String, String>();

                            details.put(KEY_NAME, name);
                            details.put(KEY_EMAIL, email);
                            details.put(KEY_PASSWORD, password);
                            return details;

                        }

            };

            AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

        }

    private boolean validateInputs() {

        if (KEY_EMPTY.equals(username)) {
            signupInputName.setError("Username cannot be empty");
            signupInputName.requestFocus();
            return false;
        }

        if (KEY_EMPTY.equals(email)) {
            signupInputEmail.setError("Email cannot be empty");
            signupInputEmail.requestFocus();
            return false;

        }

        if (KEY_EMPTY.equals(password)) {
            signupInputPassword.setError("Password cannot be empty");
            signupInputPassword.requestFocus();
            return false;
        }

        return true;
    }



}
