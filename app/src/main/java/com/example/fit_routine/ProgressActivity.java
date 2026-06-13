package com.example.fit_routine;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressActivity extends AppCompatActivity {

    private TextView tvTotalExercises;
    private TextView tvCompletedExercises;
    private TextView tvPendingExercises;
    private TextView tvWeeklyGoal;
    private Button btnIncrementWorkout;
    private Button btnBack;

    private int workoutCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        tvTotalExercises = findViewById(R.id.tvTotalExercises);
        tvCompletedExercises = findViewById(R.id.tvCompletedExercises);
        tvPendingExercises = findViewById(R.id.tvPendingExercises);
        tvWeeklyGoal = findViewById(R.id.tvWeeklyGoal);
        btnIncrementWorkout = findViewById(R.id.btnIncrementWorkout);
        btnBack = findViewById(R.id.btnBack);

        // Receive extras from Intent
        int total = getIntent().getIntExtra("total_exercises", 0);
        int completed = getIntent().getIntExtra("completed_exercises", 0);
        int pending = getIntent().getIntExtra("pending_exercises", 0);

        tvTotalExercises.setText("Ejercicios totales: " + total);
        tvCompletedExercises.setText("Ejercicios completados: " + completed);
        tvPendingExercises.setText("Ejercicios pendientes: " + pending);

        updateWeeklyGoalText();

        btnIncrementWorkout.setOnClickListener(v -> {
            workoutCount++;
            updateWeeklyGoalText();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void updateWeeklyGoalText() {
        tvWeeklyGoal.setText("Objetivo semanal: 4 entrenamientos\nEntrenamientos completados: " + workoutCount);
    }
}
