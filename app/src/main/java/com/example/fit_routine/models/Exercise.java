package com.example.fit_routine.models;

public class Exercise {
    private String name;
    private String description;
    private String muscleGroup;
    private String imageUrl;

    public Exercise(String name, String description, String muscleGroup, String imageUrl) {
        this.name = name;
        this.description = description;
        this.muscleGroup = muscleGroup;
        this.imageUrl = imageUrl;
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
}
