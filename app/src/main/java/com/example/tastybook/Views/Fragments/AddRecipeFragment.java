package com.example.tastybook.Views.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.tastybook.R;
import com.example.tastybook.Models.Recipe;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class AddRecipeFragment extends Fragment {

    private EditText recipeTitle, recipeIngredients, recipeInstructions;
    private ImageView recipeImage;
    private Button uploadImageButton, saveRecipeButton;
    private ProgressBar progressBar;
    private Uri imageUri;
    private CheckBox middleEasternCheckBox, italianCheckBox, asianCheckBox;
    private CheckBox americanCheckBox, mexicanCheckBox, indianCheckBox, frenchCheckBox;
    private CheckBox[] categoryCheckBoxes;
    private String selectedCategory = null;


    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public AddRecipeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);


        recipeTitle = view.findViewById(R.id.recipeTitle);
        recipeIngredients = view.findViewById(R.id.recipeIngredients);
        recipeInstructions = view.findViewById(R.id.recipeInstructions);
        recipeImage = view.findViewById(R.id.recipeImage);
        uploadImageButton = view.findViewById(R.id.uploadImageButton);
        saveRecipeButton = view.findViewById(R.id.saveRecipeButton);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        middleEasternCheckBox = view.findViewById(R.id.middle_eastern);
        italianCheckBox = view.findViewById(R.id.italian);
        asianCheckBox = view.findViewById(R.id.asian);
        americanCheckBox = view.findViewById(R.id.american);
        mexicanCheckBox = view.findViewById(R.id.mexican);
        indianCheckBox = view.findViewById(R.id.indian);
        frenchCheckBox = view.findViewById(R.id.french);

        categoryCheckBoxes = new CheckBox[]{middleEasternCheckBox, italianCheckBox, asianCheckBox,
                americanCheckBox, mexicanCheckBox, indianCheckBox, frenchCheckBox};

        setupCategorySelection();




        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        Picasso.get().load(imageUri).into(recipeImage);
                    }
                }
        );



        uploadImageButton.setOnClickListener(v -> openFileChooser());


        saveRecipeButton.setOnClickListener(v -> uploadRecipeToFirebase());

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(intent);
    }

    private void setupCategorySelection() {
        for (CheckBox checkBox : categoryCheckBoxes) {
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedCategory = buttonView.getText().toString();
                    uncheckOtherCheckBoxes(buttonView);
                }
            });
        }
    }
    private void uncheckOtherCheckBoxes(View selectedCheckBox) {
        for (CheckBox checkBox : categoryCheckBoxes) {
            if (checkBox != selectedCheckBox) {
                checkBox.setChecked(false);
            }
        }
    }

        private String getSelectedCategory() {
            return selectedCategory;
        }


    private void uploadRecipeToFirebase() {
        String title = recipeTitle.getText().toString().trim();
        String ingredients = recipeIngredients.getText().toString().trim();
        List<String> ingredientsList = Arrays.asList(ingredients.split(","));
        String instructions = recipeInstructions.getText().toString().trim();
        String selectedCategory = getSelectedCategory();

        if (title.isEmpty() || ingredients.isEmpty() || instructions.isEmpty() || selectedCategory == null) {
            Toast.makeText(getContext(), "נא לבחור קטגוריה ולמלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);


        String base64Image = null;
        if (imageUri != null) {
            try {
                base64Image = encodeImageToBase64(imageUri);
            } catch (Exception e) {
                Toast.makeText(getContext(), "לא מצליח לעולות את התמונה", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
        }

        saveRecipeToDatabase(title, ingredientsList, instructions, selectedCategory, base64Image);
    }

    private String encodeImageToBase64(Uri imageUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }




    private void saveRecipeToDatabase(String title, List<String> ingredients, String instructions, String category, String imageUrl) {
        String recipeId = FirebaseDatabase.getInstance().getReference("recipes").push().getKey();

        Recipe recipe = new Recipe(recipeId, title, category, ingredients, instructions, imageUrl, false);

        FirebaseDatabase.getInstance().getReference("recipes")
                .child(recipeId)
                .setValue(recipe)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "המתכון נוסף בהצלחה!!!", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "המתכון לא הצליח להתווסף בדוק את חיבור השרת שלך", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
