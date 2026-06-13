package com.example.fit_routine;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ExerciseDetailActivity extends AppCompatActivity {

    private ImageView ivExerciseImage;
    private TextView tvExerciseName;
    private TextView tvMuscleGroup;
    private TextView tvDescription;
    private TextView tvSetsReps;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        ivExerciseImage = findViewById(R.id.ivExerciseImage);
        tvExerciseName = findViewById(R.id.tvExerciseName);
        tvMuscleGroup = findViewById(R.id.tvMuscleGroup);
        tvDescription = findViewById(R.id.tvDescription);
        tvSetsReps = findViewById(R.id.tvSetsReps);
        btnBack = findViewById(R.id.btnBack);

        // Get extras from Intent
        String name = getIntent().getStringExtra("exercise_name");
        String description = getIntent().getStringExtra("exercise_description");
        String muscleGroup = getIntent().getStringExtra("exercise_muscle_group");

        tvExerciseName.setText(name != null ? name : "");
        tvDescription.setText(description != null ? description : "");
        tvMuscleGroup.setText(muscleGroup != null ? muscleGroup : "");

        if (getIntent().hasExtra("exercise_sets") && getIntent().hasExtra("exercise_reps")) {
            int sets = getIntent().getIntExtra("exercise_sets", 0);
            int reps = getIntent().getIntExtra("exercise_reps", 0);
            tvSetsReps.setText("Series: " + sets + " - Repeticiones: " + reps);
            tvSetsReps.setVisibility(android.view.View.VISIBLE);
        } else {
            tvSetsReps.setVisibility(android.view.View.GONE);
        }

        btnBack.setOnClickListener(v -> finish());
    }
}
