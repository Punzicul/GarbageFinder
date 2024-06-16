package com.example.garbagefinder;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;

public class changePic extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    String selectedPic;
    Button confirmButton;
    ImageView displayImage;
    Button changePic;
    User user;
    Context context;

    private void loadUserData(String uriString) {

        if (uriString != null && !uriString.isEmpty()) {
            Uri uri = Uri.parse(uriString);
            Glide.with(this)
                    .load(uri)
                    .centerCrop() // This will make the image fill the ImageView while maintaining aspect ratio
                    .into(displayImage);
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to pick your profile picture from the gallery.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(changePic.this,
                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Handle the URI
            Toast.makeText(this, "Image Picked", Toast.LENGTH_SHORT).show();

            selectedPic = data.getDataString(); // sets the selected pic as the URI of the selected image

            loadUserData(selectedPic);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pic);

        DatabaseClient db = DatabaseClient.getInstance(getApplicationContext());
        UserDao userDAO = db.getDb().userDao();

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        confirmButton = findViewById(R.id.confirm);
        displayImage = findViewById(R.id.imageDisplay);
        changePic = findViewById(R.id.changePic);
        context = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                user = userDAO.findUserByUsername(username);
                selectedPic = user.uri;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        loadUserData(user.uri);
                    }
                });
            }
        }).start();


        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermission();
                } else {
                    openGallery();
                }
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setURI(selectedPic);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userDAO.update(user);
                    }
                }).start();

                Intent intent = new Intent(changePic.this, settingsPage.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
}