package com.example.fit_routine.models;

public class Exercise {
    private String name;
    private String description;
    private String muscleGroup;
    private String imageUrl;
    private Integer sets;
    private Integer reps;
    private boolean completed;

    public Exercise(String name, String description, String muscleGroup, String imageUrl) {
        this(name, description, muscleGroup, imageUrl, null, null);
    }

    public Exercise(String name, String description, String muscleGroup, String imageUrl, Integer sets, Integer reps) {
        this.name = name;
        this.description = description;
        this.muscleGroup = muscleGroup;
        this.imageUrl = imageUrl;
        this.sets = sets;
        this.reps = reps;
        this.completed = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
