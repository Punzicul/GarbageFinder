package com.example.garbagefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class settingsPage extends AppCompatActivity {

    User user;
    TextView userDisplay;
    ImageView imageDisplay;
    Button changePic;
    Button changeName;
    Button confirmChange;

    String currentName;
    String currentURI;


    private void loadUserData(User user) {
        String uriString = user.uri;
        String username = user.name;

        userDisplay.setText(username);

        if (uriString != null && !uriString.isEmpty()) {
            Uri uri = Uri.parse(uriString);
            Glide.with(this)
                    .load(uri)
                    .centerCrop() // This will make the image fill the ImageView while maintaining aspect ratio
                    .into(imageDisplay);
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing to disable the back button
        Log.e("over", "overwritten");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        DatabaseClient db = DatabaseClient.getInstance(getApplicationContext());
        UserDao userDAO = db.getDb().userDao();

        String username = getIntent().getStringExtra("username");


        imageDisplay = findViewById(R.id.pictureDisplay);
        userDisplay = findViewById(R.id.nameDisplay);
        changePic = findViewById(R.id.changePic);
        changeName = findViewById(R.id.changeName);
        confirmChange = findViewById(R.id.confirm);

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


        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(settingsPage.this, changeName.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(settingsPage.this, changePic.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        confirmChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(settingsPage.this, mapPage.class);
                intent.putExtra("name", username);
                startActivity(intent);
            }
        });

    }
}