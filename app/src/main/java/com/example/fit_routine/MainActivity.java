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

public class MainActivity extends AppCompatActivity {

    private EditText etExercise;
    private Button btnAddExercise;
    private LinearLayout exerciseContainer;

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

        findViewById(R.id.btnNavProgreso).setOnClickListener(v ->
                Toast.makeText(this, R.string.msg_coming_soon, Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnNavPerfil).setOnClickListener(v ->
                Toast.makeText(this, R.string.msg_coming_soon, Toast.LENGTH_SHORT).show());

        btnAddExercise.setOnClickListener(v -> {
            String exercise = etExercise.getText().toString().trim();
            if (exercise.isEmpty()) {
                Toast.makeText(this, R.string.msg_enter_exercise, Toast.LENGTH_SHORT).show();
            } else {
                TextView newExercise = new TextView(this);
                newExercise.setText(exercise);
                newExercise.setTextSize(18);
                newExercise.setPadding(0, 16, 0, 16);
                newExercise.setTextColor(ContextCompat.getColor(this, R.color.text));

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
                etExercise.setText("");
            }
        });
    }
}
