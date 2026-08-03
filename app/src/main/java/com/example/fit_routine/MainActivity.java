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

import com.example.fit_routine.data.UserRepository;
import com.example.fit_routine.models.Exercise;
import com.example.fit_routine.models.UserProfile;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_EDIT = 1001;
    private static final int REQUEST_CODE_PROGRESS = 1002;
    private static final int REQUEST_CODE_PROFILE = 1003;

    private String userName = "";
    private String userGoal = "Ganar fuerza y constancia";
    private String userLevel = "Principiante";

    private UserRepository repository;

    private EditText etExercise;
    private Button btnAddExercise;
    private LinearLayout exerciseContainer;
    private Button btnSuggestedRoutine;
    private LinearLayout suggestedOptionsContainer;
    
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

        repository = new UserRepository();

        etExercise = findViewById(R.id.etExercise);
        btnAddExercise = findViewById(R.id.btnAddExercise);
        exerciseContainer = findViewById(R.id.exerciseContainer);
        btnSuggestedRoutine = findViewById(R.id.btnSuggestedRoutine);
        suggestedOptionsContainer = findViewById(R.id.suggestedOptionsContainer);
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
                repository.saveExercise(manualEx, currentExercises.size() - 1);
            }
        });

        btnSuggestedRoutine.setOnClickListener(v -> {
            if (suggestedOptionsContainer.getVisibility() == View.GONE) {
                suggestedOptionsContainer.setVisibility(View.VISIBLE);
                btnSuggestedRoutine.setText(R.string.btn_suggested_routine_expanded);
            } else {
                collapseSuggestedOptions();
            }
        });

        btnTrenSuperior.setOnClickListener(v -> loadSuggestedRoutine("tren_superior"));
        btnTrenInferior.setOnClickListener(v -> loadSuggestedRoutine("tren_inferior"));
        btnCore.setOnClickListener(v -> loadSuggestedRoutine("core"));

        renderExercises();
        loadUserData();
        loadSavedRoutine();
    }

    private void loadUserData() {
        if (!repository.hasSession()) {
            return;
        }

        // el nombre de la cuenta es el valor inicial hasta que llegue el documento del perfil
        userName = repository.getDisplayName() != null ? repository.getDisplayName() : repository.getEmail();

        repository.loadProfile()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        applyProfile(document);
                    } else {
                        repository.saveProfile(userName, userGoal, userLevel);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, R.string.msg_sync_error, Toast.LENGTH_SHORT).show());
    }

    private void applyProfile(DocumentSnapshot document) {
        UserProfile profile = UserRepository.toProfile(document);

        if (profile.getName() != null) {
            userName = profile.getName();
        }
        if (profile.getGoal() != null) {
            userGoal = profile.getGoal();
        }
        if (profile.getLevel() != null) {
            userLevel = profile.getLevel();
        }
        completedWorkouts = profile.getWorkoutCount();
    }

    private void loadSavedRoutine() {
        if (!repository.hasSession()) {
            return;
        }

        repository.loadRoutine()
                .addOnSuccessListener(snapshot -> {
                    currentExercises.clear();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        currentExercises.add(UserRepository.toExercise(document));
                    }
                    renderExercises();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, R.string.msg_sync_error, Toast.LENGTH_SHORT).show());
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
                .setTitle(R.string.dialog_daily_goal_title)
                .setMessage(R.string.dialog_daily_goal_message)
                .setPositiveButton(R.string.dialog_go_to_progress, (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, ProgressActivity.class);
                    intent.putExtra("total_exercises", totalExercises);
                    intent.putExtra("completed_exercises", completedExercises);
                    intent.putExtra("workout_count", completedWorkouts);
                    startActivityForResult(intent, REQUEST_CODE_PROGRESS);
                })
                .setNegativeButton(R.string.dialog_back, null)
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

        LinearLayout actionContainer = new LinearLayout(this);
        actionContainer.setOrientation(LinearLayout.HORIZONTAL);

        Button btnDetail = new Button(this);
        btnDetail.setText(R.string.btn_view_detail);
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
        btnCheck.setText(R.string.btn_check);
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
            repository.saveExercise(exercise, index);
            boolean isFullyCompleted = (totalExercises > 0 && completedExercises == totalExercises);

            if (!wasFullyCompleted && isFullyCompleted) {
                completedWorkouts++;
                repository.saveWorkoutCount(completedWorkouts);
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
            tvSetsReps.setText(getString(R.string.label_sets_reps, exercise.getSets(), exercise.getReps()));
            tvSetsReps.setTextSize(14);
            tvSetsReps.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            tvSetsReps.setPadding(0, 0, 0, 16);
            itemLayout.addView(tvSetsReps);
        }

        itemLayout.addView(actionContainer);

        exerciseContainer.addView(itemLayout);
    }

    private void collapseSuggestedOptions() {
        suggestedOptionsContainer.setVisibility(View.GONE);
        btnSuggestedRoutine.setText(R.string.btn_suggested_routine_collapsed);
    }

    private void loadSuggestedRoutine(String category) {
        // se cierra antes de leer el archivo para que el menú no quede abierto mientras carga
        collapseSuggestedOptions();

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
                    repository.replaceRoutine(currentExercises);
                    Toast.makeText(MainActivity.this, R.string.msg_suggested_routine_loaded, Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, R.string.msg_suggested_routine_error, Toast.LENGTH_SHORT).show()
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
                    Exercise removed = currentExercises.remove(index);
                    renderExercises();
                    if (removed.getId() != null) {
                        repository.deleteExercise(removed.getId());
                    }
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
                    repository.saveExercise(ex, index);
                }
            }
        } else if (requestCode == REQUEST_CODE_PROGRESS && resultCode == RESULT_OK && data != null) {
            boolean clearExercises = data.getBooleanExtra("clear_exercises", false);
            if (clearExercises) {
                currentExercises.clear();
                renderExercises();
                repository.clearRoutine();
            }
            boolean resetWorkouts = data.getBooleanExtra("reset_workouts", false);
            if (resetWorkouts) {
                completedWorkouts = 0;
                repository.saveWorkoutCount(completedWorkouts);
            }
        } else if (requestCode == REQUEST_CODE_PROFILE && resultCode == RESULT_OK && data != null) {
            userName = data.getStringExtra("user_name");
            userGoal = data.getStringExtra("user_goal");
            userLevel = data.getStringExtra("user_level");
        }
    }
}
