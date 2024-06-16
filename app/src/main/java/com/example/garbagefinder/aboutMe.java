package com.example.garbagefinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.osmdroid.util.GeoPoint;

public class aboutMe extends AppCompatActivity {
    NavigationView navigationView;
    User user;
    TextView navHeaderUsername;
    ImageView navHeaderPicture;
    UserDao userDAO;


    private void loadUserData(User user) {
        String uriString = user.uri;
        String username = user.name;

        navHeaderUsername.setText(username);

        if (uriString != null && !uriString.isEmpty()) {
            Uri uri = Uri.parse(uriString);
            Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .into(navHeaderPicture);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        DatabaseClient db = DatabaseClient.getInstance(getApplicationContext());
        userDAO = db.getDb().userDao();

        // Set up the navigation view and header view
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navHeaderUsername = headerView.findViewById(R.id.name);
        navHeaderPicture = headerView.findViewById(R.id.picture);

        new Thread(new Runnable() {
            @Override
            public void run() {
                user = userDAO.findUserByUsername(username);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadUserData(user);
                    }
                });
            }
        }).start();

        
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_logout) {
                    Intent intent = new Intent(aboutMe.this, Login.class);
                    startActivity(intent);
                }
                else if (item.getItemId() == R.id.nav_home) {
                    Intent intent = new Intent(aboutMe.this, Login.class);
                    intent.putExtra("name", username);
                    startActivity(intent);
                }
                else if (item.getItemId() == R.id.nav_settings) {
                    Intent intent = new Intent(aboutMe.this, settingsPage.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                return false;
            }
        });
    }
}