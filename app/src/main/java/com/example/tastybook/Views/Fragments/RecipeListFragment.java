package com.example.tastybook.Views.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tastybook.R;
import com.example.tastybook.Controllers.FirebaseManager;
import com.example.tastybook.Models.Recipe;
import com.example.tastybook.Views.Adapter.RecipeAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class RecipeListFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList, filteredList,favlist;
    private FirebaseManager firebaseManager;
    private SearchView searchView;
    private Button addRecipeButton;
    private Spinner categorySpinner;
    private Button favoriteFilterButton;
    private boolean showFavorites = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterRecipes(query, categorySpinner.getSelectedItem().toString(),showFavorites);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterRecipes(newText, categorySpinner.getSelectedItem().toString(),showFavorites);
                return false;
            }
        });
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        favlist = new ArrayList<>();
        recipeList = new ArrayList<>();
        filteredList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipeList, recipe -> {
        });

        recyclerView.setAdapter(recipeAdapter);
        recyclerView.setVisibility(View.VISIBLE);
        firebaseManager = new FirebaseManager();
        fetchRecipesFromFirebase();

        addRecipeButton = view.findViewById(R.id.addRecipeButton);
        addRecipeButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_recipeListFragment_to_addRecipeFragment);
        });

        categorySpinner = view.findViewById(R.id.categorySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.recipe_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        favoriteFilterButton = view.findViewById(R.id.favoriteFilterButton);
        favoriteFilterButton.setOnClickListener(v ->{
            showFavorites = !showFavorites;
            filterRecipes(searchView.getQuery().toString(),categorySpinner.getSelectedItem().toString(),showFavorites);
            favoriteFilterButton.setText(showFavorites? "הצג הכל" :"הצג מועדפים ♥");
        });



        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterRecipes(searchView.getQuery().toString(), categorySpinner.getSelectedItem().toString(),showFavorites);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }



    private void fetchRecipesFromFirebase() {
        firebaseManager.getRecipesReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        recipeList.add(recipe);
                    }
                }
                recipeAdapter.updateList(recipeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"לא מצליחה לעלות את המתכון", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void filterRecipes(String query, String category,boolean showFavorites) {
        List<Recipe> tempFilteredList = new ArrayList<>();
        String queryLower = query.toLowerCase();

        for (Recipe recipe : recipeList) {
            String recipeCategory = recipe.getCategory() != null ? recipe.getCategory().trim() : "";
            String recipeTitle = recipe.getTitle() != null ? recipe.getTitle().toLowerCase() : "";

            boolean matchesCategory = category.equals("All") || recipeCategory.equalsIgnoreCase(category);
            boolean matchesSearch = recipeTitle.contains(queryLower);
            boolean matchesFavorite = !showFavorites || recipe.getisFavorite();

            if(showFavorites && !recipe.getisFavorite()){
                continue;
            }

            if (matchesCategory && matchesSearch && matchesFavorite ) {
                tempFilteredList.add(recipe);
            }

        }
        recipeAdapter.updateList(tempFilteredList);
    }

}
