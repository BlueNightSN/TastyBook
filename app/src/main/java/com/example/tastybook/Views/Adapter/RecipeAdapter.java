package com.example.tastybook.Views.Adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tastybook.R;
import com.example.tastybook.Models.Recipe;
import com.example.tastybook.Views.Activities.RecipeDetailActivity;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipeList;
    private List<Recipe> filteredList;
    private OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public RecipeAdapter(List<Recipe> recipeList, OnRecipeClickListener listener) {
        this.recipeList = recipeList;
        this.filteredList = new ArrayList<>(recipeList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = filteredList.get(position);
        holder.recipeTitle.setText(recipe.getTitle());

        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            Bitmap bitmap = decodeBase64ToBitmap(recipe.getImageUrl());
            if (bitmap != null) {
                holder.recipeImage.setImageBitmap(bitmap);
            } else {
                holder.recipeImage.setImageResource(R.drawable.placeholder_image);
            }
        } else {
            holder.recipeImage.setImageResource(R.drawable.placeholder_image);
        }

        updateFavoriteIcon(holder.favoriteButton, recipe.getisFavorite());
        
        holder.favoriteButton.setOnClickListener(v ->{
            boolean newFavoriteStatus = !recipe.getisFavorite();
            recipe.setFavorite(newFavoriteStatus);
            updateFavoriteIcon(holder.favoriteButton, newFavoriteStatus);

            FirebaseDatabase.getInstance().getReference("recipes")
                    .child(recipe.getId())
                    .child("isFavorite")
                    .setValue(newFavoriteStatus);
        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecipeDetailActivity.class);
            intent.putExtra("title", recipe.getTitle());

            if (recipe.getIngredients() != null) {
                intent.putExtra("ingredients", TextUtils.join("\n", recipe.getIngredients()));
            } else {
                intent.putExtra("ingredients", "No ingredients available");
            }

            if (recipe.getInstructions() != null) {
                intent.putExtra("instructions", recipe.getInstructions());
            } else {
                intent.putExtra("instructions", "No instructions available");
            }

            intent.putExtra("imageUrl", recipe.getImageUrl());
            view.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return filteredList.size();
    }
    public void updateList(List<Recipe> newRecipes) {
        this.filteredList.clear();
        this.filteredList.addAll(newRecipes);
        notifyDataSetChanged();
        Log.d("RecyclerViewUpdate", "Updated list with " + newRecipes.size() + " recipes.");
    }


    private void updateFavoriteIcon(ImageView favoriteButton, boolean isFavorite){
        if(isFavorite){
            favoriteButton.setImageResource(R.drawable.ic_favorite);
        }else {
            favoriteButton.setImageResource(R.drawable.ic_unfavorite);
        }
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView recipeTitle;
        ImageView recipeImage, favoriteButton;


        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeTitle = itemView.findViewById(R.id.recipeTitle);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
        }
    }

    private Bitmap decodeBase64ToBitmap(String base64Image) {
        try {
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            return null;
        }
    }

}

