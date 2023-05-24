package com.bbudzowski.homeautoandroid;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import com.bbudzowski.homeautoandroid.api.BaseApi;
import com.bbudzowski.homeautoandroid.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;

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
        findViewById(R.id.login_button).setOnClickListener(view ->
                onLoginClick(binding.getRoot()));
    }

    private void onLoginClick(View root) {
        //String username = ((TextView) findViewById(R.id.inputUsername)).getText().toString();
        //String password = ((TextView) findViewById(R.id.inputPassword)).getText().toString();
        String username = "bbudzowski";
        String password = "tial2o3";
        InputStream keyFile = getResources().openRawResource(R.raw.server_ts);
        if(!BaseApi.createClient(keyFile, username, password)){
            Snackbar.make(root, "Błąd tworzenia klienta http", Snackbar.LENGTH_SHORT).show();
            return;
        }
        try (Response resp = BaseApi.dummyRequest()) {
            if(resp == null) {
                Snackbar.make(root, "Brak połączenia z serwerem", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if(resp.code() != 200) {
                if(resp.code() == 401)
                    Snackbar.make(root, "Niepoprawne dane logowania", Snackbar.LENGTH_SHORT).show();
                else
                    Snackbar.make(root, "Nieznany błąd", Snackbar.LENGTH_SHORT).show();
                return;
            }
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
