package com.iesvegademijas.serverSideSocialFlavours.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecipeDTO {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private long id_recipe;
    private String name;
    private String description;
    private String imagePath;
    private int preparationTime;
    private String tag;
    private String rating;
    private List<String> ingredients;
    private List<String> steps;

    private Long userId;

    private String date;

    public RecipeDTO() {}

    public RecipeDTO(Long id_recipe, String name, String imagePath, String rating, String description,  String preparationTime, String tag,
                     List<String> ingredients, List<String> steps, String userId, String date) {
        this.id_recipe = id_recipe;
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.rating = rating;
        this.preparationTime = Integer.parseInt(preparationTime);
        this.tag = tag;
        this.ingredients = ingredients;
        this.steps = steps;
        this.userId = Long.parseLong(userId);
        this.date = date;
    }

    // Getters and Setters


    public long getId_recipe() {
        return id_recipe;
    }

    public void setId_recipe(long id_recipe) {
        this.id_recipe = id_recipe;
    }

    public Date getDate() throws ParseException {
        return dateFormat.parse(this.date);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }
}
