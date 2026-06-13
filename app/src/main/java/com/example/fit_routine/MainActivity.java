package com.example.fit_routine;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

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

    private EditText etExercise;
    private Button btnAddExercise;
    private LinearLayout exerciseContainer;
    private List<Exercise> loadedRoutine = new ArrayList<>();

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

        findViewById(R.id.btnNavProgreso).setOnClickListener(v ->
                Toast.makeText(this, R.string.msg_coming_soon, Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnNavPerfil).setOnClickListener(v ->
                Toast.makeText(this, R.string.msg_coming_soon, Toast.LENGTH_SHORT).show());

        btnAddExercise.setOnClickListener(v -> {
            String exercise = etExercise.getText().toString().trim();
            if (exercise.isEmpty()) {
                Toast.makeText(this, R.string.msg_enter_exercise, Toast.LENGTH_SHORT).show();
            } else {
                addExerciseView(exercise);
                etExercise.setText("");
            }
        });

        btnSuggestedRoutine.setOnClickListener(v -> {
            if (suggestedOptionsContainer.getVisibility() == android.view.View.GONE) {
                suggestedOptionsContainer.setVisibility(android.view.View.VISIBLE);
            } else {
                suggestedOptionsContainer.setVisibility(android.view.View.GONE);
            }
        });

        btnTrenSuperior.setOnClickListener(v -> loadSuggestedRoutine("tren_superior"));
        btnTrenInferior.setOnClickListener(v -> loadSuggestedRoutine("tren_inferior"));
        btnCore.setOnClickListener(v -> loadSuggestedRoutine("core"));

        loadExercisesFromJson();
    }

    private void addExerciseView(String exerciseName) {
        TextView newExercise = new TextView(this);
        newExercise.setText(exerciseName);
        newExercise.setTextSize(18);
        newExercise.setPadding(0, 16, 0, 16);
        newExercise.setTextColor(ContextCompat.getColor(this, R.color.text));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 24);
        newExercise.setLayoutParams(params);

        newExercise.setOnClickListener(view -> {
            TextView tv = (TextView) view;
            String currentText = tv.getText().toString();
            String checkPrefix = getString(R.string.check_prefix);

            if (currentText.startsWith(checkPrefix)) {
                tv.setText(currentText.substring(checkPrefix.length()));
                tv.setTextColor(ContextCompat.getColor(this, R.color.text));
            } else {
                tv.setText(checkPrefix + currentText);
                tv.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            }
        });

        exerciseContainer.addView(newExercise);
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
                    for (Exercise ex : exercises) {
                        addExerciseViewFromJson(ex);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Error al cargar ejercicios", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void addExerciseViewFromJson(Exercise exercise) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 32);
        itemLayout.setLayoutParams(params);
        itemLayout.setPadding(32, 32, 32, 32);
        itemLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

        TextView tvName = new TextView(this);
        tvName.setText(exercise.getName());
        tvName.setTextSize(18);
        tvName.setTextColor(ContextCompat.getColor(this, R.color.text));
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvMuscle = new TextView(this);
        tvMuscle.setText(exercise.getMuscleGroup());
        tvMuscle.setTextSize(14);
        tvMuscle.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        tvMuscle.setPadding(0, 8, 0, 16);

        Button btnDetail = new Button(this);
        btnDetail.setText("Ver detalle");
        btnDetail.setOnClickListener(v -> {
            // Do nothing yet
        });

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

        itemLayout.addView(btnDetail);

        exerciseContainer.addView(itemLayout);
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

                String displayName;
                if ("tren_superior".equals(category)) {
                    displayName = "Tren superior";
                } else if ("tren_inferior".equals(category)) {
                    displayName = "Tren inferior";
                } else {
                    displayName = "Core";
                }

                final String finalDisplayName = displayName;
                runOnUiThread(() -> {
                    loadedRoutine.clear();
                    loadedRoutine.addAll(routine);
                    
                    exerciseContainer.removeAllViews();
                    for (Exercise ex : loadedRoutine) {
                        addExerciseViewFromJson(ex);
                    }
                    
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
}
