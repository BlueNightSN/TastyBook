package com.example.tastybook.Controllers;

import com.example.tastybook.Models.Recipe;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FirebaseManager {
    private DatabaseReference databaseReference;

    public FirebaseManager() {
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes");
    }

    public void addRecipe(Recipe recipe) {
        String id = databaseReference.push().getKey();
        recipe.setId(id);
        databaseReference.child(id).setValue(recipe);
    }

    public DatabaseReference getRecipesReference() {
        return databaseReference;
    }
}
