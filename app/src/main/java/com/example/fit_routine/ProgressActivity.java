package com.example.fit_routine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressActivity extends AppCompatActivity {

    private static final int WEEKLY_GOAL = 4;

    private TextView tvTotalExercises;
    private TextView tvCompletedExercises;
    private TextView tvPendingExercises;
    private TextView tvCompletionPercentage;
    private TextView tvWeeklyGoal;
    private Button btnResetProgress;
    private Button btnBack;

    private int workoutCount = 0;
    private int total = 0;
    private Intent resultIntent = new Intent();

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

        total = getIntent().getIntExtra("total_exercises", 0);
        int completed = getIntent().getIntExtra("completed_exercises", 0);
        int pending = total - completed;
        workoutCount = getIntent().getIntExtra("workout_count", 0);

        updateCounters(total, completed, pending);

        if (total == 0) {
            tvCompletionPercentage.setText(R.string.progress_percentage_default);
        } else if (completed == total) {
            tvCompletionPercentage.setText(R.string.msg_routine_completed);
            Toast.makeText(this, R.string.msg_routine_completed, Toast.LENGTH_SHORT).show();
        } else {
            int percentage = (completed * 100) / total;
            tvCompletionPercentage.setText(getString(R.string.label_progress_percentage, percentage));
        }

        updateWeeklyGoalText();

        if (workoutCount >= WEEKLY_GOAL) {
            showWeeklyGoalCompletionDialog();
        }

        btnResetProgress.setOnClickListener(v -> {
            total = 0;
            updateCounters(0, 0, 0);
            tvCompletionPercentage.setText(R.string.progress_percentage_default);

            resultIntent.putExtra("clear_exercises", true);
            setResult(RESULT_OK, resultIntent);

            Toast.makeText(this, R.string.msg_routine_cleared, Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void updateCounters(int total, int completed, int pending) {
        tvTotalExercises.setText(getString(R.string.label_total_exercises, total));
        tvCompletedExercises.setText(getString(R.string.label_completed_exercises, completed));
        tvPendingExercises.setText(getString(R.string.label_pending_exercises, pending));
    }

    private void updateWeeklyGoalText() {
        tvWeeklyGoal.setText(getString(R.string.label_weekly_goal, WEEKLY_GOAL, workoutCount));
    }

    private void showWeeklyGoalCompletionDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.dialog_weekly_goal_title)
                .setMessage(R.string.dialog_weekly_goal_message)
                .setPositiveButton(R.string.dialog_accept, (dialog, which) -> {
                    workoutCount = 0;
                    updateWeeklyGoalText();
                    resultIntent.putExtra("reset_workouts", true);
                    setResult(RESULT_OK, resultIntent);
                })
                .setCancelable(false)
                .show();
    }
}
