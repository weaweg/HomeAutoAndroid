package com.bbudzowski.homeautoandroid.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.api.BaseApi;
import com.bbudzowski.homeautoandroid.databinding.ActivityLoginBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import okhttp3.Response;

public class LoginActivity extends Activity {
    String username = null;
    String password = null;
    private ActivityLoginBinding binding;

    public static void saveToFile(Context context, String text) throws IOException {
        OutputStreamWriter outputStreamWriter =
                new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
        outputStreamWriter.write(text);
        outputStreamWriter.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MaterialButton button = findViewById(R.id.login_button);
        ((TextInputEditText) findViewById(R.id.inputPassword)).
                setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE)
                        button.performClick();
                    return true;
                });
        button.setOnClickListener(this::onLoginClick);
        String pass = readFromFile(getApplicationContext());
        if (pass != null) {
            String[] credentials = pass.split(":");
            username = credentials[0];
            password = credentials[1];
            button.performClick();
        }
    }

    private void onLoginClick(View button) {
        button.setOnClickListener(null);
        TextInputEditText inputUsername = findViewById(R.id.inputUsername);
        TextInputEditText inputPassword = findViewById(R.id.inputPassword);
        inputUsername.setEnabled(false);
        inputPassword.setEnabled(false);
        if (username == null || password == null) {
            username = inputUsername.getText().toString();
            password = inputPassword.getText().toString();
        }
        if (username.equals("") || password.equals("")) {
            Snackbar.make(binding.getRoot(), "Wpisz dane logowania!", Snackbar.LENGTH_SHORT).show();
            button.setOnClickListener(this::onLoginClick);
            return;
        }
        InputStream keyFile = getResources().openRawResource(R.raw.server_ts);
        if (!BaseApi.createClient(keyFile, username, password)) {
            Snackbar.make(binding.getRoot(), "Błąd tworzenia klienta http", Snackbar.LENGTH_SHORT).show();
            button.setOnClickListener(this::onLoginClick);
            return;
        }
        try (Response resp = BaseApi.dummyRequest()) {
            if (resp == null) {
                Snackbar.make(binding.getRoot(), "Brak połączenia z serwerem", Snackbar.LENGTH_SHORT).show();
                button.setOnClickListener(this::onLoginClick);
                return;
            }
            if (resp.code() != 200) {
                if (resp.code() == 401) {
                    Snackbar.make(binding.getRoot(), "Niepoprawne dane logowania", Snackbar.LENGTH_SHORT).show();
                    getApplicationContext().deleteFile("config.txt");
                } else
                    Snackbar.make(binding.getRoot(), "Wewnętrzny błąd serwera", Snackbar.LENGTH_SHORT).show();
                button.setOnClickListener(this::onLoginClick);
                return;
            }
        }
        try {
            saveToFile(getApplicationContext(), username + ":" + password);
        } catch (IOException ignored) {
        }
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    private String readFromFile(Context context) {
        String ret = null;

        try {
            InputStream inputStream = context.openFileInput("config.txt");
            if (inputStream != null) {
                ret = new BufferedReader(
                        new InputStreamReader(inputStream)).readLine();
                inputStream.close();
            }
        } catch (IOException ignored) {
        }
        return ret;
    }
}
