package com.example.footballapp;

import android.app.ProgressDialog;
// import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.StringRequest;
import com.example.footballapp.ui.login.LoginViewModel;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private static final String TAG = "LoginActivity";
    ProgressDialog progressDialog;
    private final String URL_FOR_LOGIN = "http://192.168.0.11/login.php";
    private EditText email;
    private EditText password;
    private Button loginButton;
    private Button signUpbutton;
    private SessionHandler session;

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMPTY = "";

    private String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionHandler(getApplicationContext());

       // if(session.isLoggedIn()){
         //   loadMenu();
       // }



        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginButton);
       signUpbutton = (Button) findViewById(R.id.signUpButton);
        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(email.getText().toString(), password.getText().toString());

            }
        });

        signUpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
    }

        private void loginUser(final String email, final String password) {

            String cancel_req_tag = "login";
            progressDialog.setMessage("Logging in");
            showDialog();
            StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_LOGIN, new Response.Listener<String>() {

                public void onResponse(String response) {
                    hideDialog();
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean error = obj.getBoolean("error");

                        if (!error) {
                            String user = obj.getJSONObject("user").getString("name");
                            session.loginUser(email, password);
                            // Launch User activity
                            Intent intent = new Intent(
                                    LoginActivity.this,
                                    MapsActivity.class);
                            intent.putExtra("username", user);
                            startActivity(intent);
                            finish();
                        } else {

                            String errorMsg = obj.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }








        },

                    new ErrorListener() {


                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Login Error: " + error.getMessage());
                            Toast.makeText(getApplicationContext(),
                                    error.getMessage(), Toast.LENGTH_LONG).show();
                            hideDialog();
                        }




                    })
            {
                @Override
                protected Map<String, String> getParams() {
                    // Posting params to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", email);
                    params.put("password", password);
                    return params;
                }
            };
            // Adding request to request queue
            AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq,cancel_req_tag);
        }

    private void loadMenu() {
        Intent i = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(i);
        finish();

    }


        private void showDialog() {
            if (!progressDialog.isShowing())
                progressDialog.show();
        }
        private void hideDialog() {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

