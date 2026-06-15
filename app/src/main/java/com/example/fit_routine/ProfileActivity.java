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
    private Button btnBack;

    private String currentName;
    private String currentGoal;
    private String currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
        btnBack = findViewById(R.id.btnBack);

        // Receive extras from Intent
        currentName = getIntent().getStringExtra("user_name");
        currentGoal = getIntent().getStringExtra("user_goal");
        currentLevel = getIntent().getStringExtra("user_level");

        tvUserName.setText("Usuario: " + (currentName != null ? currentName : ""));
        tvGoal.setText("Objetivo: " + (currentGoal != null ? currentGoal : ""));
        tvLevel.setText("Nivel: " + (currentLevel != null ? currentLevel : ""));

        // Setup Edit Profile Button
        btnEditProfile.setOnClickListener(v -> {
            etEditName.setText(currentName);
            etEditGoal.setText(currentGoal);
            etEditLevel.setText(currentLevel);
            
            layoutDisplay.setVisibility(View.GONE);
            layoutEdit.setVisibility(View.VISIBLE);
        });

        // Setup Cancel Button
        btnCancelProfile.setOnClickListener(v -> {
            layoutEdit.setVisibility(View.GONE);
            layoutDisplay.setVisibility(View.VISIBLE);
        });

        // Setup Save Button
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

                tvUserName.setText("Usuario: " + currentName);
                tvGoal.setText("Objetivo: " + currentGoal);
                tvLevel.setText("Nivel: " + currentLevel);

                // Toggle visibility
                layoutEdit.setVisibility(View.GONE);
                layoutDisplay.setVisibility(View.VISIBLE);

                // Return data to MainActivity
                Intent data = new Intent();
                data.putExtra("user_name", currentName);
                data.putExtra("user_goal", currentGoal);
                data.putExtra("user_level", currentLevel);
                setResult(RESULT_OK, data);

                Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
