package com.example.fit_routine.data;

import com.example.fit_routine.models.Exercise;
import com.example.fit_routine.models.UserProfile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Centraliza el acceso a Firestore para que las Activities no repitan
 * rutas de colecciones ni el manejo del usuario logueado.
 */
public class UserRepository {

    private static final String USERS_COLLECTION = "users";
    private static final String ROUTINE_COLLECTION = "routine";

    private static final String FIELD_NAME = "name";
    private static final String FIELD_GOAL = "goal";
    private static final String FIELD_LEVEL = "level";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_WORKOUT_COUNT = "workoutCount";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_MUSCLE_GROUP = "muscleGroup";
    private static final String FIELD_IMAGE_URL = "imageUrl";
    private static final String FIELD_SETS = "sets";
    private static final String FIELD_REPS = "reps";
    private static final String FIELD_COMPLETED = "completed";
    private static final String FIELD_POSITION = "position";

    private final FirebaseFirestore database;
    private final FirebaseUser user;

    public UserRepository() {
        database = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public boolean hasSession() {
        return user != null;
    }

    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }

    public String getDisplayName() {
        return user != null ? user.getDisplayName() : null;
    }

    private DocumentReference profileDocument() {
        return database.collection(USERS_COLLECTION).document(user.getUid());
    }

    private CollectionReference routineCollection() {
        return profileDocument().collection(ROUTINE_COLLECTION);
    }

    public Task<DocumentSnapshot> loadProfile() {
        return profileDocument().get();
    }

    public Task<Void> saveProfile(String name, String goal, String level) {
        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_NAME, name);
        data.put(FIELD_GOAL, goal);
        data.put(FIELD_LEVEL, level);
        data.put(FIELD_EMAIL, user.getEmail());
        // merge para no pisar el contador de entrenamientos que vive en el mismo documento
        return profileDocument().set(data, SetOptions.merge());
    }

    public Task<Void> saveWorkoutCount(int workoutCount) {
        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_WORKOUT_COUNT, workoutCount);
        return profileDocument().set(data, SetOptions.merge());
    }

    public Task<QuerySnapshot> loadRoutine() {
        return routineCollection().orderBy(FIELD_POSITION).get();
    }

    public Task<Void> saveExercise(Exercise exercise, int position) {
        DocumentReference document = exercise.getId() == null
                ? routineCollection().document()
                : routineCollection().document(exercise.getId());

        exercise.setId(document.getId());
        return document.set(toMap(exercise, position));
    }

    public Task<Void> deleteExercise(String exerciseId) {
        return routineCollection().document(exerciseId).delete();
    }

    /**
     * Borra la rutina guardada y escribe la nueva en una sola operación atómica,
     * para que no queden ejercicios sueltos si algo falla a la mitad.
     */
    public Task<Void> replaceRoutine(List<Exercise> exercises) {
        return routineCollection().get().continueWithTask(task -> {
            WriteBatch batch = database.batch();

            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                batch.delete(document.getReference());
            }

            for (int i = 0; i < exercises.size(); i++) {
                Exercise exercise = exercises.get(i);
                DocumentReference document = routineCollection().document();
                exercise.setId(document.getId());
                batch.set(document, toMap(exercise, i));
            }

            return batch.commit();
        });
    }

    public Task<Void> clearRoutine() {
        return routineCollection().get().continueWithTask(task -> {
            WriteBatch batch = database.batch();
            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                batch.delete(document.getReference());
            }
            return batch.commit();
        });
    }

    public static UserProfile toProfile(DocumentSnapshot document) {
        Long workoutCount = document.getLong(FIELD_WORKOUT_COUNT);

        return new UserProfile(
                document.getString(FIELD_NAME),
                document.getString(FIELD_GOAL),
                document.getString(FIELD_LEVEL),
                workoutCount != null ? workoutCount.intValue() : 0
        );
    }

    public static Exercise toExercise(DocumentSnapshot document) {
        Long sets = document.getLong(FIELD_SETS);
        Long reps = document.getLong(FIELD_REPS);

        Exercise exercise = new Exercise(
                document.getString(FIELD_NAME),
                document.getString(FIELD_DESCRIPTION),
                document.getString(FIELD_MUSCLE_GROUP),
                document.getString(FIELD_IMAGE_URL),
                sets != null ? sets.intValue() : null,
                reps != null ? reps.intValue() : null
        );

        exercise.setId(document.getId());
        exercise.setCompleted(Boolean.TRUE.equals(document.getBoolean(FIELD_COMPLETED)));
        return exercise;
    }

    private Map<String, Object> toMap(Exercise exercise, int position) {
        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_NAME, exercise.getName());
        data.put(FIELD_DESCRIPTION, exercise.getDescription());
        data.put(FIELD_MUSCLE_GROUP, exercise.getMuscleGroup());
        data.put(FIELD_IMAGE_URL, exercise.getImageUrl());
        data.put(FIELD_SETS, exercise.getSets());
        data.put(FIELD_REPS, exercise.getReps());
        data.put(FIELD_COMPLETED, exercise.isCompleted());
        data.put(FIELD_POSITION, position);
        return data;
    }
}
