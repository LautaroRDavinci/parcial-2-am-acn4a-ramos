package com.example.fit_routine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private GoogleSignInClient googleClient;
    private ActivityResultLauncher<Intent> googleLauncher;

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnGoogleLogin;
    private TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        googleClient = buildGoogleClient();
        googleLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleGoogleResult(result.getData()));

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> login());

        btnGoogleLogin.setOnClickListener(v -> googleLauncher.launch(googleClient.getSignInIntent()));

        tvGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Firebase guarda la sesión en disco, así que un usuario ya logueado saltea esta pantalla
        if (auth.getCurrentUser() != null) {
            openRoutine();
        }
    }

    private GoogleSignInClient buildGoogleClient() {
        // default_web_client_id lo genera el plugin de google-services a partir del google-services.json
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(this, options);
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.msg_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    Toast.makeText(this, R.string.msg_login_ok, Toast.LENGTH_SHORT).show();
                    openRoutine();
                })
                .addOnFailureListener(e -> {
                    btnLogin.setEnabled(true);
                    Toast.makeText(this, R.string.msg_login_error, Toast.LENGTH_SHORT).show();
                });
    }

    private void handleGoogleResult(Intent data) {
        GoogleSignInAccount account;
        try {
            account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
        } catch (ApiException e) {
            Toast.makeText(this, R.string.msg_google_error, Toast.LENGTH_SHORT).show();
            return;
        }

        // la cuenta de Google solo prueba la identidad, Firebase todavía tiene que canjear el token por una sesión
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(result -> {
                    Toast.makeText(this, R.string.msg_login_ok, Toast.LENGTH_SHORT).show();
                    openRoutine();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, R.string.msg_google_error, Toast.LENGTH_SHORT).show());
    }

    private void openRoutine() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
