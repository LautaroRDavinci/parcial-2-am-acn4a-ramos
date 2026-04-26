package com.example.fit_routine;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;
import android.graphics.Color;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

        btnAddExercise.setOnClickListener(v -> {
            String exercise = etExercise.getText().toString().trim();
            if (exercise.isEmpty()) {
                Toast.makeText(this, "Ingrese un ejercicio", Toast.LENGTH_SHORT).show();
            } else {
                TextView newExercise = new TextView(this);
                newExercise.setText(exercise);
                newExercise.setTextSize(18);
                newExercise.setPadding(0, 16, 0, 16);

                newExercise.setOnClickListener(view -> {
                    TextView tv = (TextView) view;
                    String currentText = tv.getText().toString();

                    if (currentText.startsWith("✔ ")) {
                        tv.setText(currentText.substring(2));
                        tv.setTextColor(Color.BLACK);
                    } else {
                        tv.setText("✔ " + currentText);
                        tv.setTextColor(Color.GRAY);
                    }
                });

                exerciseContainer.addView(newExercise);

                etExercise.setText("");
            }
        });
    }
}