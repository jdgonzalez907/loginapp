package com.jdgonzalez907.loginapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jdgonzalez907.loginapp.models.LoginUser;
import com.jdgonzalez907.loginapp.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    String url = "https://testandroidservice.000webhostapp.com/login.php";
    RequestQueue rq;
    StringRequest sr;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rq = Volley.newRequestQueue(this);

        final EditText editUsername = (EditText) findViewById(R.id.editUsername);
        final EditText editPassword = (EditText) findViewById(R.id.editPassword);
        final Button btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser loginUser = new LoginUser(
                        editUsername.getText().toString(),
                        editPassword.getText().toString()
                );

                tryLogin(loginUser);
            }
        });
    }

    void tryLogin(final LoginUser loginUser) {
        if(loginUser.getUsername().isEmpty() || loginUser.getUsername().equals("") ||
                        (loginUser.getPassword().isEmpty() || loginUser.getPassword().equals(""))) {
            showInvalidLogin("Asegurese de llenar todos los campos");
        } else {
            sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response).getJSONObject("response");
                        user = new User();
                        user.setFullName(jsonResponse.getString("names"));
                        user.setUsername(jsonResponse.getString("user"));
                        user.setPassword(jsonResponse.getString("pwd"));

                        goToMain();
                    } catch (JSONException e) {
                        Log.e("LOGIN", "Json error in login service", e);
                    }
                }
            }, new Response.ErrorListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onErrorResponse(VolleyError e) {
                    user = null;
                    try {
                        String stringError = new String(e.networkResponse.data, StandardCharsets.UTF_8);
                        JSONObject jsonError = new JSONObject(stringError);
                        showInvalidLogin(jsonError.getString("message"));
                    } catch (JSONException ex) {
                        Log.e("LOGIN", "Error in login service", e);
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", loginUser.getUsername());
                    params.put("password", loginUser.getPassword());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                    return headers;
                }
            };

            rq.add(sr);
        }
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void showInvalidLogin(String message) {
        Toast.makeText(this, message.toUpperCase(), Toast.LENGTH_SHORT).show();
    }
}