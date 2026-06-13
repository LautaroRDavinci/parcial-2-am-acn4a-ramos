package com.example.fit_routine;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressActivity extends AppCompatActivity {

    private TextView tvTotalExercises;
    private TextView tvCompletedExercises;
    private TextView tvPendingExercises;
    private TextView tvCompletionPercentage;
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
        tvCompletionPercentage = findViewById(R.id.tvCompletionPercentage);
        tvWeeklyGoal = findViewById(R.id.tvWeeklyGoal);
        btnIncrementWorkout = findViewById(R.id.btnIncrementWorkout);
        btnBack = findViewById(R.id.btnBack);

        // Receive extras from Intent
        int total = getIntent().getIntExtra("total_exercises", 0);
        int completed = getIntent().getIntExtra("completed_exercises", 0);
        int pending = total - completed;

        tvTotalExercises.setText("Ejercicios totales: " + total);
        tvCompletedExercises.setText("Ejercicios completados: " + completed);
        tvPendingExercises.setText("Ejercicios pendientes: " + pending);

        int percentage = 0;
        if (total > 0) {
            percentage = (completed * 100) / total;
        }
        tvCompletionPercentage.setText("Progreso actual: " + percentage + "%");

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
