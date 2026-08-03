package com.example.fit_routine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

public class ExerciseDetailActivity extends AppCompatActivity {

    // varios sitios de imágenes rechazan los pedidos que no parecen venir de un navegador
    private static final String IMAGE_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                    + "(KHTML, like Gecko) Chrome/100.0.0.0 Safari/537.36";

    private ImageView ivExerciseImage;
    private TextView tvExerciseName;
    private TextView tvMuscleGroup;
    private TextView tvDescription;
    private TextView tvSetsReps;
    private Button btnBack;
    private Button btnEditExercise;
    private Button btnDeleteExercise;

    private LinearLayout displayContainer;
    private LinearLayout editContainer;

    private EditText etEditName;
    private EditText etEditDescription;
    private EditText etEditSets;
    private EditText etEditReps;
    private EditText etEditImageUrl;
    private Button btnSaveDetail;
    private Button btnCancelEdit;

    private int exerciseIndex = -1;
    private String muscleGroup = "";
    private String currentImageUrl = "";
    private Integer currentSets = null;
    private Integer currentReps = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        displayContainer = findViewById(R.id.displayContainer);
        ivExerciseImage = findViewById(R.id.ivExerciseImage);
        tvExerciseName = findViewById(R.id.tvExerciseName);
        tvMuscleGroup = findViewById(R.id.tvMuscleGroup);
        tvDescription = findViewById(R.id.tvDescription);
        tvSetsReps = findViewById(R.id.tvSetsReps);
        btnEditExercise = findViewById(R.id.btnEditExercise);
        btnBack = findViewById(R.id.btnBack);
        btnDeleteExercise = findViewById(R.id.btnDeleteExercise);

        editContainer = findViewById(R.id.editContainer);
        etEditName = findViewById(R.id.etEditName);
        etEditDescription = findViewById(R.id.etEditDescription);
        etEditSets = findViewById(R.id.etEditSets);
        etEditReps = findViewById(R.id.etEditReps);
        etEditImageUrl = findViewById(R.id.etEditImageUrl);
        btnSaveDetail = findViewById(R.id.btnSaveDetail);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);

        exerciseIndex = getIntent().getIntExtra("exercise_index", -1);
        String name = getIntent().getStringExtra("exercise_name");
        String description = getIntent().getStringExtra("exercise_description");
        muscleGroup = getIntent().getStringExtra("exercise_muscle_group");
        currentImageUrl = getIntent().getStringExtra("exercise_image_url");

        if (getIntent().hasExtra("exercise_sets")) {
            currentSets = getIntent().getIntExtra("exercise_sets", 0);
        }
        if (getIntent().hasExtra("exercise_reps")) {
            currentReps = getIntent().getIntExtra("exercise_reps", 0);
        }

        updateDisplayViews(name, description, currentSets, currentReps);

        btnBack.setOnClickListener(v -> finish());

        btnDeleteExercise.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("exercise_index", exerciseIndex);
            resultIntent.putExtra("delete_exercise", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        btnEditExercise.setOnClickListener(v -> {
            etEditName.setText(tvExerciseName.getText().toString());
            etEditDescription.setText(tvDescription.getText().toString());
            etEditSets.setText(currentSets != null ? String.valueOf(currentSets) : "");
            etEditReps.setText(currentReps != null ? String.valueOf(currentReps) : "");
            etEditImageUrl.setText(currentImageUrl != null ? currentImageUrl : "");

            displayContainer.setVisibility(View.GONE);
            editContainer.setVisibility(View.VISIBLE);
        });

        btnCancelEdit.setOnClickListener(v -> {
            editContainer.setVisibility(View.GONE);
            displayContainer.setVisibility(View.VISIBLE);
        });

        btnSaveDetail.setOnClickListener(v -> {
            String updatedName = etEditName.getText().toString().trim();
            String updatedDesc = etEditDescription.getText().toString().trim();
            String updatedSetsStr = etEditSets.getText().toString().trim();
            String updatedRepsStr = etEditReps.getText().toString().trim();
            String updatedImageUrl = etEditImageUrl.getText().toString().trim();

            if (updatedName.isEmpty()) {
                Toast.makeText(this, R.string.msg_enter_exercise_name, Toast.LENGTH_SHORT).show();
                return;
            }

            Integer updatedSets = null;
            Integer updatedReps = null;
            try {
                if (!updatedSetsStr.isEmpty()) {
                    updatedSets = Integer.parseInt(updatedSetsStr);
                }
                if (!updatedRepsStr.isEmpty()) {
                    updatedReps = Integer.parseInt(updatedRepsStr);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.msg_sets_reps_numeric, Toast.LENGTH_SHORT).show();
                return;
            }

            currentSets = updatedSets;
            currentReps = updatedReps;
            currentImageUrl = updatedImageUrl;

            updateDisplayViews(updatedName, updatedDesc, currentSets, currentReps);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("exercise_index", exerciseIndex);
            resultIntent.putExtra("exercise_name", updatedName);
            resultIntent.putExtra("exercise_description", updatedDesc);
            resultIntent.putExtra("exercise_muscle_group", muscleGroup);
            resultIntent.putExtra("exercise_image_url", currentImageUrl);
            if (currentSets != null) {
                resultIntent.putExtra("exercise_sets", currentSets.intValue());
            }
            if (currentReps != null) {
                resultIntent.putExtra("exercise_reps", currentReps.intValue());
            }
            setResult(RESULT_OK, resultIntent);

            editContainer.setVisibility(View.GONE);
            displayContainer.setVisibility(View.VISIBLE);

            Toast.makeText(this, R.string.msg_exercise_updated, Toast.LENGTH_SHORT).show();
        });
    }

    private void updateDisplayViews(String name, String description, Integer sets, Integer reps) {
        tvExerciseName.setText(name != null ? name : "");
        tvDescription.setText(description != null ? description : "");
        tvMuscleGroup.setText(muscleGroup != null ? muscleGroup : "");

        if (sets != null && reps != null) {
            tvSetsReps.setText(getString(R.string.label_sets_reps, sets, reps));
            tvSetsReps.setVisibility(View.VISIBLE);
        } else {
            tvSetsReps.setVisibility(View.GONE);
        }

        if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
            loadImageFromUrl(currentImageUrl);
        } else {
            ivExerciseImage.setImageResource(R.drawable.bg_exercise_placeholder);
        }
    }

    private void loadImageFromUrl(String urlString) {
        GlideUrl url = new GlideUrl(urlString, new LazyHeaders.Builder()
                .addHeader("User-Agent", IMAGE_USER_AGENT)
                .build());

        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.bg_exercise_placeholder)
                .error(R.drawable.bg_exercise_placeholder)
                .into(ivExerciseImage);
    }
}
