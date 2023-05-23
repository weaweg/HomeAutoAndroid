package com.bbudzowski.homeautoandroid;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;

import com.bbudzowski.homeautoandroid.api.BaseApi;
import com.bbudzowski.homeautoandroid.databinding.ActivityLoginBinding;

import java.io.InputStream;

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
                onLoginClick());
    }

    private void onLoginClick() {
        String username = ((TextView) findViewById(R.id.inputUsername)).getText().toString();
        String password = ((TextView) findViewById(R.id.inputPassword)).getText().toString();
        InputStream keyFile = getResources().openRawResource(R.raw.server_ts);
        if(!BaseApi.createClient(keyFile, username, password)){

        }
        //add dummy request to check if credentials are ok
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
