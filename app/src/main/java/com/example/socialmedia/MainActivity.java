package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        BottomNavigationView mainBottomNavigationView = findViewById(R.id.idMainBottomNavigation);
        mainBottomNavigationView.setOnItemSelectedListener(onNav);
        getSupportFragmentManager().beginTransaction().replace(R.id.idMainFrame, new HomeFragment()).commit();


    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNav = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selected = null;
            switch (item.getItemId()) {
                case R.id.idMenuHome:
                    selected = new HomeFragment();
                    break;
                case R.id.idMenuProfile:
                    selected = new ProfileFragment();
                    break;
                case R.id.idMenuAsk:
                    selected = new AskFragment();
                    break;
                case R.id.idMenuQueue:
                    selected = new PeopleFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.idMainFrame, selected).commit();

            return true;
        }
    };

    public void logOut(View view) {
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}