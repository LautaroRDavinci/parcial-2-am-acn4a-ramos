package com.example.fit_routine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fit_routine.models.Exercise;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_EDIT = 1001;
    private static final int REQUEST_CODE_PROGRESS = 1002;
    private static final int REQUEST_CODE_PROFILE = 1003;

    private String userName = "Lautaro";
    private String userGoal = "Ganar fuerza y constancia";
    private String userLevel = "Principiante";

    private EditText etExercise;
    private Button btnAddExercise;
    private LinearLayout exerciseContainer;
    
    private List<Exercise> currentExercises = new ArrayList<>();
    private int totalExercises = 0;
    private int completedExercises = 0;
    private int completedWorkouts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etExercise = findViewById(R.id.etExercise);
        btnAddExercise = findViewById(R.id.btnAddExercise);
        exerciseContainer = findViewById(R.id.exerciseContainer);
        Button btnSuggestedRoutine = findViewById(R.id.btnSuggestedRoutine);
        LinearLayout suggestedOptionsContainer = findViewById(R.id.suggestedOptionsContainer);
        Button btnTrenSuperior = findViewById(R.id.btnTrenSuperior);
        Button btnTrenInferior = findViewById(R.id.btnTrenInferior);
        Button btnCore = findViewById(R.id.btnCore);

        findViewById(R.id.btnNavProgreso).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProgressActivity.class);
            intent.putExtra("total_exercises", totalExercises);
            intent.putExtra("completed_exercises", completedExercises);
            intent.putExtra("workout_count", completedWorkouts);
            startActivityForResult(intent, REQUEST_CODE_PROGRESS);
        });

        findViewById(R.id.btnNavPerfil).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("user_name", userName);
            intent.putExtra("user_goal", userGoal);
            intent.putExtra("user_level", userLevel);
            startActivityForResult(intent, REQUEST_CODE_PROFILE);
        });

        btnAddExercise.setOnClickListener(v -> {
            String exerciseName = etExercise.getText().toString().trim();
            if (exerciseName.isEmpty()) {
                Toast.makeText(this, R.string.msg_enter_exercise, Toast.LENGTH_SHORT).show();
            } else {
                Exercise manualEx = new Exercise(exerciseName, "", "Personalizado", "", null, null);
                currentExercises.add(manualEx);
                renderExercises();
                etExercise.setText("");
            }
        });

        // suggested routine collapsible menu with visual indicator
        btnSuggestedRoutine.setOnClickListener(v -> {
            if (suggestedOptionsContainer.getVisibility() == View.GONE) {
                suggestedOptionsContainer.setVisibility(View.VISIBLE);
                btnSuggestedRoutine.setText("Rutina sugerida ▲");
            } else {
                suggestedOptionsContainer.setVisibility(View.GONE);
                btnSuggestedRoutine.setText("Rutina sugerida ▼");
            }
        });

        btnTrenSuperior.setOnClickListener(v -> loadSuggestedRoutine("tren_superior"));
        btnTrenInferior.setOnClickListener(v -> loadSuggestedRoutine("tren_inferior"));
        btnCore.setOnClickListener(v -> loadSuggestedRoutine("core"));

        renderExercises();
    }

    private void renderExercises() {
        exerciseContainer.removeAllViews();
        totalExercises = 0;
        completedExercises = 0;

        for (int i = 0; i < currentExercises.size(); i++) {
            Exercise ex = currentExercises.get(i);
            totalExercises++;
            if (ex.isCompleted()) {
                completedExercises++;
            }
            addExerciseViewFromJson(ex, i);
        }
    }

    private void showCompletionDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("¡Felicitaciones!")
                .setMessage("¡Has completado todos los ejercicios de tu rutina!")
                .setPositiveButton("Ir a progreso", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, ProgressActivity.class);
                    intent.putExtra("total_exercises", totalExercises);
                    intent.putExtra("completed_exercises", completedExercises);
                    intent.putExtra("workout_count", completedWorkouts);
                    startActivityForResult(intent, REQUEST_CODE_PROGRESS);
                })
                .setNegativeButton("Volver", null)
                .show();
    }

    private void addExerciseViewFromJson(Exercise exercise, int index) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 32);
        itemLayout.setLayoutParams(params);
        itemLayout.setPadding(32, 32, 32, 32);

        // Styling based on completed state
        if (exercise.isCompleted()) {
            itemLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.menu_highlight));
        } else {
            itemLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        }

        TextView tvName = new TextView(this);
        String nameText = exercise.getName();
        if (exercise.isCompleted()) {
            nameText = getString(R.string.check_prefix) + nameText;
            tvName.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            tvName.setTextColor(ContextCompat.getColor(this, R.color.text));
        }
        tvName.setText(nameText);
        tvName.setTextSize(18);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvMuscle = new TextView(this);
        tvMuscle.setText(exercise.getMuscleGroup());
        tvMuscle.setTextSize(14);
        tvMuscle.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        tvMuscle.setPadding(0, 8, 0, 16);

        // Action Buttons Row (Ver detalle & Check button)
        LinearLayout actionContainer = new LinearLayout(this);
        actionContainer.setOrientation(LinearLayout.HORIZONTAL);

        Button btnDetail = new Button(this);
        btnDetail.setText("Ver detalle");
        btnDetail.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExerciseDetailActivity.class);
            intent.putExtra("exercise_index", index);
            intent.putExtra("exercise_name", exercise.getName());
            intent.putExtra("exercise_description", exercise.getDescription());
            intent.putExtra("exercise_muscle_group", exercise.getMuscleGroup());
            intent.putExtra("exercise_image_url", exercise.getImageUrl());
            if (exercise.getSets() != null && exercise.getReps() != null) {
                intent.putExtra("exercise_sets", exercise.getSets().intValue());
                intent.putExtra("exercise_reps", exercise.getReps().intValue());
            }
            startActivityForResult(intent, REQUEST_CODE_EDIT);
        });

        Button btnCheck = new Button(this);
        btnCheck.setText("✔");
        if (exercise.isCompleted()) {
            btnCheck.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
            btnCheck.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            btnCheck.setBackgroundColor(ContextCompat.getColor(this, R.color.menu_highlight));
            btnCheck.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        }
        btnCheck.setOnClickListener(v -> {
            boolean wasFullyCompleted = (totalExercises > 0 && completedExercises == totalExercises);
            exercise.setCompleted(!exercise.isCompleted());
            renderExercises();
            boolean isFullyCompleted = (totalExercises > 0 && completedExercises == totalExercises);

            if (!wasFullyCompleted && isFullyCompleted) {
                completedWorkouts++;
                showCompletionDialog();
            }
        });

        LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2f
        );
        detailParams.setMargins(0, 0, 16, 0);
        btnDetail.setLayoutParams(detailParams);

        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        btnCheck.setLayoutParams(checkParams);

        actionContainer.addView(btnDetail);
        actionContainer.addView(btnCheck);

        itemLayout.addView(tvName);
        itemLayout.addView(tvMuscle);

        if (exercise.getSets() != null && exercise.getReps() != null) {
            tvMuscle.setPadding(0, 8, 0, 4);
            TextView tvSetsReps = new TextView(this);
            tvSetsReps.setText("Series: " + exercise.getSets() + " - Repeticiones: " + exercise.getReps());
            tvSetsReps.setTextSize(14);
            tvSetsReps.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            tvSetsReps.setPadding(0, 0, 0, 16);
            itemLayout.addView(tvSetsReps);
        }

        itemLayout.addView(actionContainer);

        exerciseContainer.addView(itemLayout);
    }

    private void loadExercisesFromJson() {
        new Thread(() -> {
            try {
                InputStream is = getAssets().open("exercises.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String json = new String(buffer, "UTF-8");

                JSONArray jsonArray = new JSONArray(json);
                List<Exercise> exercises = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String name = obj.getString("name");
                    String desc = obj.getString("description");
                    String muscle = obj.getString("muscleGroup");
                    String img = obj.getString("imageUrl");
                    Integer sets = obj.has("sets") ? obj.getInt("sets") : null;
                    Integer reps = obj.has("reps") ? obj.getInt("reps") : null;
                    exercises.add(new Exercise(name, desc, muscle, img, sets, reps));
                }

                runOnUiThread(() -> {
                    currentExercises.clear();
                    currentExercises.addAll(exercises);
                    renderExercises();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Error al cargar ejercicios", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void loadSuggestedRoutine(String category) {
        new Thread(() -> {
            try {
                InputStream is = getAssets().open("suggested_routines.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String json = new String(buffer, "UTF-8");

                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray(category);
                List<Exercise> routine = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String name = obj.getString("name");
                    String desc = obj.getString("description");
                    String muscle = obj.getString("muscleGroup");
                    String img = obj.getString("imageUrl");
                    Integer sets = obj.has("sets") ? obj.getInt("sets") : null;
                    Integer reps = obj.has("reps") ? obj.getInt("reps") : null;
                    routine.add(new Exercise(name, desc, muscle, img, sets, reps));
                }

                runOnUiThread(() -> {
                    currentExercises.clear();
                    currentExercises.addAll(routine);
                    renderExercises();
                    Toast.makeText(MainActivity.this, "Rutina sugerida cargada", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Error al cargar la rutina sugerida", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK && data != null) {
            int index = data.getIntExtra("exercise_index", -1);
            if (index >= 0 && index < currentExercises.size()) {
                boolean deleteExercise = data.getBooleanExtra("delete_exercise", false);
                if (deleteExercise) {
                    currentExercises.remove(index);
                    renderExercises();
                } else {
                    Exercise ex = currentExercises.get(index);
                    ex.setName(data.getStringExtra("exercise_name"));
                    ex.setDescription(data.getStringExtra("exercise_description"));
                    ex.setMuscleGroup(data.getStringExtra("exercise_muscle_group"));
                    ex.setImageUrl(data.getStringExtra("exercise_image_url"));
                    
                    if (data.hasExtra("exercise_sets")) {
                        ex.setSets(data.getIntExtra("exercise_sets", 0));
                    } else {
                        ex.setSets(null);
                    }
                    
                    if (data.hasExtra("exercise_reps")) {
                        ex.setReps(data.getIntExtra("exercise_reps", 0));
                    } else {
                        ex.setReps(null);
                    }

                    renderExercises();
                }
            }
        } else if (requestCode == REQUEST_CODE_PROGRESS && resultCode == RESULT_OK && data != null) {
            boolean clearExercises = data.getBooleanExtra("clear_exercises", false);
            if (clearExercises) {
                currentExercises.clear();
                renderExercises();
            }
            boolean resetWorkouts = data.getBooleanExtra("reset_workouts", false);
            if (resetWorkouts) {
                completedWorkouts = 0;
            }
        } else if (requestCode == REQUEST_CODE_PROFILE && resultCode == RESULT_OK && data != null) {
            userName = data.getStringExtra("user_name");
            userGoal = data.getStringExtra("user_goal");
            userLevel = data.getStringExtra("user_level");
        }
    }
}
