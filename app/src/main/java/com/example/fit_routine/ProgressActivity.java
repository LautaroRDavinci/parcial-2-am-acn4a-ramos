package com.example.fit_routine;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressActivity extends AppCompatActivity {

    private TextView tvTotalExercises;
    private TextView tvCompletedExercises;
    private TextView tvPendingExercises;
    private TextView tvCompletionPercentage;
    private TextView tvWeeklyGoal;
    private Button btnIncrementWorkout;
    private Button btnResetProgress;
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
        btnResetProgress = findViewById(R.id.btnResetProgress);
        btnBack = findViewById(R.id.btnBack);

        // Receive extras from Intent
        int total = getIntent().getIntExtra("total_exercises", 0);
        int completed = getIntent().getIntExtra("completed_exercises", 0);
        int pending = total - completed;

        tvTotalExercises.setText("Ejercicios totales: " + total);
        tvCompletedExercises.setText("Ejercicios completados: " + completed);
        tvPendingExercises.setText("Ejercicios pendientes: " + pending);

        if (total == 0) {
            tvCompletionPercentage.setText("Agregá ejercicios para ver tu progreso");
        } else if (completed == total) {
            tvCompletionPercentage.setText("Rutina completada");
            Toast.makeText(this, "Rutina completada", Toast.LENGTH_SHORT).show();
        } else {
            int percentage = (completed * 100) / total;
            tvCompletionPercentage.setText("Progreso actual: " + percentage + "%");
        }

        updateWeeklyGoalText();

        btnIncrementWorkout.setOnClickListener(v -> {
            workoutCount++;
            updateWeeklyGoalText();
        });

        btnResetProgress.setOnClickListener(v -> {
            workoutCount = 0;
            updateWeeklyGoalText();
            Toast.makeText(this, "Progreso reiniciado", Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void updateWeeklyGoalText() {
        tvWeeklyGoal.setText("Objetivo semanal: 4 entrenamientos\nEntrenamientos completados: " + workoutCount);
    }
}
