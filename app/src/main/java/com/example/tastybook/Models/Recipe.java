package com.example.tastybook.Models;

import java.util.List;

public class Recipe {
    private String id;
    private String title;
    private String category;
    private List<String> ingredients;
    private String instructions;
    private String imageUrl;
    private boolean isFavorite = false;



    public Recipe() {}

    public Recipe(String id, String title, String category, List<String> ingredients,String instructions, String imageUrl, boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.isFavorite = isFavorite;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
    public String getInstructions() {return instructions;}

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean getisFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}

