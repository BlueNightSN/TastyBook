package com.example.tastybook.Views.Activities;

import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.tastybook.R;



public class MainActivity extends AppCompatActivity {
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentContainerView navHostFragmentView = findViewById(R.id.nav_host_fragment);
        if (navHostFragmentView != null) {
            getSupportFragmentManager().executePendingTransactions();
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (fragment instanceof NavHostFragment) {
                navController = ((NavHostFragment) fragment).getNavController();
            }
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navController != null && navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() != R.id.mainPageFragment) {
                    navController.navigateUp();
                } else {
                    finish();
                }
            }
        });
    }
}
