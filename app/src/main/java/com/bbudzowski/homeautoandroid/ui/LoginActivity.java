package com.bbudzowski.homeautoandroid.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.api.BaseApi;
import com.bbudzowski.homeautoandroid.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.InputStream;

import okhttp3.Response;

public class LoginActivity extends Activity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        findViewById(R.id.login_button).setOnClickListener(this::onLoginClick);
        findViewById(R.id.login_button).performClick();
    }

    private void onLoginClick(View button) {
        button.setOnClickListener(null);
        TextInputEditText textUsername = (TextInputEditText) findViewById(R.id.inputUsername);
        TextInputEditText textPassword = (TextInputEditText) findViewById(R.id.inputPassword);
        String username = "bbudzowski";// textUsername.getText().toString();
        String password = "tial2o3"; //textPassword.getText().toString();
        if(username.equals("") || password.equals("")) {
            Snackbar.make(binding.getRoot(), "Wpisz dane logowania!", Snackbar.LENGTH_SHORT).show();
            button.setOnClickListener(this::onLoginClick);
            return;
        }
        InputStream keyFile = getResources().openRawResource(R.raw.server_ts);
        if(!BaseApi.createClient(keyFile, username, password)){
            Snackbar.make(binding.getRoot(), "Błąd tworzenia klienta http", Snackbar.LENGTH_SHORT).show();
            button.setOnClickListener(this::onLoginClick);
            return;
        }
        try (Response resp = BaseApi.dummyRequest()) {
            if(resp == null) {
                Snackbar.make(binding.getRoot(), "Brak połączenia z serwerem", Snackbar.LENGTH_SHORT).show();
                button.setOnClickListener(this::onLoginClick);
                return;
            }
            if(resp.code() != 200) {
                if(resp.code() == 401)
                    Snackbar.make(binding.getRoot(), "Niepoprawne dane logowania", Snackbar.LENGTH_SHORT).show();
                else
                    Snackbar.make(binding.getRoot(), "Wewnętrzny błąd serwera", Snackbar.LENGTH_SHORT).show();
                button.setOnClickListener(this::onLoginClick);
                return;
            }
        }
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }



}
