package com.example.fit_routine;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName;
    private TextView tvGoal;
    private TextView tvLevel;
    private EditText etNewGoal;
    private Button btnSaveGoal;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUserName = findViewById(R.id.tvUserName);
        tvGoal = findViewById(R.id.tvGoal);
        tvLevel = findViewById(R.id.tvLevel);
        etNewGoal = findViewById(R.id.etNewGoal);
        btnSaveGoal = findViewById(R.id.btnSaveGoal);
        btnBack = findViewById(R.id.btnBack);

        // Receive extras from Intent
        String name = getIntent().getStringExtra("user_name");
        String goal = getIntent().getStringExtra("user_goal");
        String level = getIntent().getStringExtra("user_level");

        tvUserName.setText("Usuario: " + (name != null ? name : ""));
        tvGoal.setText("Objetivo: " + (goal != null ? goal : ""));
        tvLevel.setText("Nivel: " + (level != null ? level : ""));

        btnSaveGoal.setOnClickListener(v -> {
            String newGoal = etNewGoal.getText().toString().trim();
            if (newGoal.isEmpty()) {
                Toast.makeText(this, "Ingrese un objetivo", Toast.LENGTH_SHORT).show();
            } else {
                tvGoal.setText("Objetivo: " + newGoal);
                etNewGoal.setText("");
                Toast.makeText(this, "Objetivo actualizado", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
