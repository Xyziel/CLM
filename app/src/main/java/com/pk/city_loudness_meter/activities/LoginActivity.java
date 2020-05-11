package com.pk.city_loudness_meter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.pk.city_loudness_meter.R; //cannot resolve symbol R

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences loginData;
    private String email, password;
    private TextView emailField, passwordField;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;
    private GoogleSignInClient googleSignInClient;
    private final OkHttpClient client = new OkHttpClient();
    private int RC_SIGN_IN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.buttonLogin);
        SignInButton loginButtonGoogle = findViewById(R.id.loginButtonGoogle);
        TextView createNewAccount = findViewById(R.id.createNewAccount);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        rememberMeCheckBox = findViewById(R.id.checkBoxRemember);

        loginButton.setOnClickListener(v -> loginSuccessful());

        loginButtonGoogle.setOnClickListener(v -> loginGoogle());

        createNewAccount.setOnClickListener(v -> register());

        loginData = getSharedPreferences("loginData", MODE_PRIVATE);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void login() {
        loginButton.setEnabled(false);
        if (validate()) {
            sendPostRequest();
        }
        loginButton.setEnabled(true);
    }

    private boolean validate() {
        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        if (email.isEmpty()) {
            emailField.setError("Enter email");
            emailField.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Email is not valid");
            emailField.requestFocus();
            return false;
        } else if (password.isEmpty()) {
            passwordField.setError("Enter password");
            passwordField.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private Request prepareRequest() {
        final JSONObject data = new JSONObject() {{
            try {
                put("username", email);
                put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }};

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), data.toString());

        return new Request.Builder()
                .url("http://192.168.0.105:8080/login")
                .post(body)
                .build();
    }

    private void sendPostRequest() {

        Call call = client.newCall(prepareRequest());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                sendPostFailure();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                System.out.println(response.header("Authorization"));
                if(response.isSuccessful()) {
                    loginSuccessful();

                } else {
                    loginUnsuccessful();
                }
            }
        });
    }

    private void sendPostFailure() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginSuccessful() {

        if (rememberMeCheckBox.isChecked()) {
            SharedPreferences.Editor loginDataEditor = loginData.edit();
            loginDataEditor.putBoolean("loginDataSaved", true);
            loginDataEditor.putString("email", email);
            loginDataEditor.putString("password", encodePassword(password));
            loginDataEditor.apply();
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = new Intent(this, DataActivity.class);
        startActivity(intent);
        finish();

    }

    private void loginUnsuccessful() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Intent intent = new Intent(this, DataActivity.class);
            startActivity(intent);
            finish();
        }

        boolean loginDataSaved = loginData.getBoolean("loginDataSaved", false);
        if (loginDataSaved) {
            Intent intent = new Intent(this, DataActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void loginGoogle() {
        Intent googleSignInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(googleSignInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            startActivity(new Intent(this, DataActivity.class));
        } catch (ApiException e) {
            System.out.println(e.getMessage() + e);
            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private String encodePassword(String pass) {
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pass.getBytes(StandardCharsets.UTF_8));
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hexString.toString();
    }

}
