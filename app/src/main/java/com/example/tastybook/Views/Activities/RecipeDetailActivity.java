package com.example.tastybook.Views.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import com.example.tastybook.R;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView recipeImage;
    private TextView recipeTitle, recipeIngredients, recipeInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);


        recipeImage = findViewById(R.id.recipeImage);
        recipeTitle = findViewById(R.id.recipeTitle);
        recipeIngredients = findViewById(R.id.recipeIngredients);
        recipeInstructions = findViewById(R.id.recipeInstructions);


        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String ingredients = intent.getStringExtra("ingredients");
        String instructions = intent.getStringExtra("instructions");
        String imageBase64 = intent.getStringExtra("imageUrl");


        recipeTitle.setText(title);
        recipeIngredients.setText(ingredients);
        recipeInstructions.setText(instructions);


        if (imageBase64 != null && !imageBase64.isEmpty()) {
            if (imageBase64.startsWith("/9j/")) {
                Bitmap bitmap = decodeBase64ToBitmap(imageBase64);
                if (bitmap != null) {
                    recipeImage.setImageBitmap(bitmap);
                } else {
                    recipeImage.setImageResource(R.drawable.placeholder_image);
                }
            } else {
                Picasso.get()
                        .load(imageBase64)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(recipeImage);
            }
        } else {
            recipeImage.setImageResource(R.drawable.placeholder_image);
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
