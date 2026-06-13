package com.example.fit_routine.models;

public class Exercise {
    private String name;
    private String description;
    private String muscleGroup;
    private String imageUrl;
    private Integer sets;
    private Integer reps;

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
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getSets() {
        return sets;
    }

    public Integer getReps() {
        return reps;
    }
}
