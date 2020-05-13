package com.pk.city_loudness_meter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pk.city_loudness_meter.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailField, passwordField, confirmPasswordField;
    private String email, password, confirmPassword;
    private Button registerButton;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = findViewById(R.id.buttonRegister);
        TextView signIn = findViewById(R.id.signIn);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void register() {
        registerButton.setEnabled(false);
        if(validate()) {
            sendPostRequest();
        }
        registerButton.setEnabled(true);
    }

    private boolean validate() {

        email = emailField.getText().toString();
        password = passwordField.getText().toString();
        confirmPassword = confirmPasswordField.getText().toString();

        if (email.isEmpty()) {
            emailField.setError("Enter email");
            emailField.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailField.setError("Email is not valid");
            emailField.requestFocus();
            return false;
        } else if (password.isEmpty() || password.length() < 8) {
            passwordField.setError("Password must be at least 8 characters");
            passwordField.requestFocus();
            return false;
        } else if (confirmPassword.isEmpty() || confirmPassword.length() < 8 || confirmPassword.length() > 25) {
            confirmPasswordField.setError("Password must be between 8 to 25 characters");
            confirmPasswordField.requestFocus();
            return false;
        } else if (!password.equals(confirmPassword)){
            confirmPasswordField.setError("Passwords do not match");
            confirmPasswordField.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private Request prepareRequest() {
        JSONObject data = new JSONObject() {{
            try {
                put("username", email);
                put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }};

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data.toString());
        return new Request.Builder()
                .url("http://192.168.0.105:8080/sign-up")
                .post(body)
                .build();
    }


    private void sendPostRequest() {
        Call call = client.newCall(prepareRequest());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                registerFailure();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println(response.toString());
                if(response.isSuccessful()) {
                    registerSuccessful();
                    login();
                } else {
                    registerUnsuccessful();
                }
            }
        });
    }

    private void registerFailure() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerSuccessful() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Registration successful.\n   Now you can sign in.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerUnsuccessful() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
