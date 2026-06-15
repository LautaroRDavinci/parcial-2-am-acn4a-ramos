package com.example.fit_routine;

import android.content.Intent;
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
    private Button btnResetProgress;
    private Button btnBack;

    private int workoutCount = 0;
    private int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        tvTotalExercises = findViewById(R.id.tvTotalExercises);
        tvCompletedExercises = findViewById(R.id.tvCompletedExercises);
        tvPendingExercises = findViewById(R.id.tvPendingExercises);
        tvCompletionPercentage = findViewById(R.id.tvCompletionPercentage);
        tvWeeklyGoal = findViewById(R.id.tvWeeklyGoal);
        btnResetProgress = findViewById(R.id.btnResetProgress);
        btnBack = findViewById(R.id.btnBack);

        // Receive extras from Intent
        total = getIntent().getIntExtra("total_exercises", 0);
        int completed = getIntent().getIntExtra("completed_exercises", 0);
        int pending = total - completed;
        workoutCount = getIntent().getIntExtra("workout_count", 0);

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

        btnResetProgress.setOnClickListener(v -> {
            total = 0;
            tvTotalExercises.setText("Ejercicios totales: 0");
            tvCompletedExercises.setText("Ejercicios completados: 0");
            tvPendingExercises.setText("Ejercicios pendientes: 0");
            tvCompletionPercentage.setText("Agregá ejercicios para ver tu progreso");

            // Return clear signal to MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("clear_exercises", true);
            setResult(RESULT_OK, resultIntent);

            Toast.makeText(this, "Ejercicios de la rutina eliminados", Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void updateWeeklyGoalText() {
        tvWeeklyGoal.setText("Objetivo semanal: 4 entrenamientos\nEntrenamientos completados: " + workoutCount);
    }
}
