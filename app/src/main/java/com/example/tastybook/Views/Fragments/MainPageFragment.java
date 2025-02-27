package com.example.tastybook.Views.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.tastybook.R;
import com.google.android.material.button.MaterialButton;

public class MainPageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);


        MaterialButton startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_mainPageFragment_to_recipeListFragment)
        );

        return view;
    }
}
