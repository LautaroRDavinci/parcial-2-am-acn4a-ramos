package com.example.fit_routine.models;

public class UserProfile {
    private String name;
    private String goal;
    private String level;
    private int workoutCount;

    public UserProfile(String name, String goal, String level, int workoutCount) {
        this.name = name;
        this.goal = goal;
        this.level = level;
        this.workoutCount = workoutCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getWorkoutCount() {
        return workoutCount;
    }

    public void setWorkoutCount(int workoutCount) {
        this.workoutCount = workoutCount;
    }
}
