package com.example.fit_routine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fit_routine.data.UserRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName;
    private TextView tvGoal;
    private TextView tvLevel;
    
    private LinearLayout layoutDisplay;
    private LinearLayout layoutEdit;
    
    private EditText etEditName;
    private EditText etEditGoal;
    private EditText etEditLevel;
    
    private Button btnEditProfile;
    private Button btnSaveProfile;
    private Button btnCancelProfile;
    private Button btnLogout;
    private Button btnBack;

    private UserRepository repository;

    private String currentName;
    private String currentGoal;
    private String currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        repository = new UserRepository();

        tvUserName = findViewById(R.id.tvUserName);
        tvGoal = findViewById(R.id.tvGoal);
        tvLevel = findViewById(R.id.tvLevel);
        
        layoutDisplay = findViewById(R.id.layoutDisplay);
        layoutEdit = findViewById(R.id.layoutEdit);
        
        etEditName = findViewById(R.id.etEditName);
        etEditGoal = findViewById(R.id.etEditGoal);
        etEditLevel = findViewById(R.id.etEditLevel);
        
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnCancelProfile = findViewById(R.id.btnCancelProfile);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);

        currentName = getIntent().getStringExtra("user_name");
        currentGoal = getIntent().getStringExtra("user_goal");
        currentLevel = getIntent().getStringExtra("user_level");

        showProfile();

        btnEditProfile.setOnClickListener(v -> {
            etEditName.setText(currentName);
            etEditGoal.setText(currentGoal);
            etEditLevel.setText(currentLevel);
            
            layoutDisplay.setVisibility(View.GONE);
            layoutEdit.setVisibility(View.VISIBLE);
        });

        btnCancelProfile.setOnClickListener(v -> {
            layoutEdit.setVisibility(View.GONE);
            layoutDisplay.setVisibility(View.VISIBLE);
        });

        btnSaveProfile.setOnClickListener(v -> {
            String newName = etEditName.getText().toString().trim();
            String newGoal = etEditGoal.getText().toString().trim();
            String newLevel = etEditLevel.getText().toString().trim();

            if (newName.isEmpty() || newGoal.isEmpty() || newLevel.isEmpty()) {
                Toast.makeText(this, R.string.msg_empty_fields, Toast.LENGTH_SHORT).show();
            } else {
                currentName = newName;
                currentGoal = newGoal;
                currentLevel = newLevel;

                showProfile();

                layoutEdit.setVisibility(View.GONE);
                layoutDisplay.setVisibility(View.VISIBLE);

                Intent data = new Intent();
                data.putExtra("user_name", currentName);
                data.putExtra("user_goal", currentGoal);
                data.putExtra("user_level", currentLevel);
                setResult(RESULT_OK, data);

                saveProfileToDatabase();
            }
        });

        btnLogout.setOnClickListener(v -> logout());

        btnBack.setOnClickListener(v -> finish());
    }

    private void showProfile() {
        tvUserName.setText(getString(R.string.label_profile_username, currentName != null ? currentName : ""));
        tvGoal.setText(getString(R.string.label_profile_goal, currentGoal != null ? currentGoal : ""));
        tvLevel.setText(getString(R.string.label_profile_level, currentLevel != null ? currentLevel : ""));
    }

    private void saveProfileToDatabase() {
        if (!repository.hasSession()) {
            return;
        }

        repository.saveProfile(currentName, currentGoal, currentLevel)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, R.string.msg_profile_updated, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, R.string.msg_sync_error, Toast.LENGTH_SHORT).show());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        // sin esto la cuenta de Google queda cacheada y el próximo login saltea el selector
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
        Toast.makeText(this, R.string.msg_logout_ok, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        // limpia la pila para que el botón atrás no pueda volver a la rutina
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
