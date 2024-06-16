package com.example.garbagefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class changeName extends AppCompatActivity {

    EditText newName;
    Button confirmButton;
    TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_name);

        Intent intent = getIntent();
        String currentUsername = intent.getStringExtra("username");


        newName = findViewById(R.id.newName);
        confirmButton = findViewById(R.id.confirm);
        errorText = findViewById(R.id.errorText);

        DatabaseClient db = DatabaseClient.getInstance(getApplicationContext());
        UserDao userDAO = db.getDb().userDao();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newName.getText().toString().length() < 4){
                    errorText.setText("USERNAME MUST BE 4+ CHARACTERS");
                }
                else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            User checkUser = userDAO.findUserByUsername(newName.getText().toString());
                            User currentUser = userDAO.findUserByUsername(currentUsername);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (checkUser != null){
                                        errorText.setText("USER ALREADY EXISTS");
                                    }
                                    else{
                                        currentUser.setName(newName.getText().toString());

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                userDAO.update(currentUser);

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent intent = new Intent(changeName.this, settingsPage.class);
                                                        intent.putExtra("username", currentUser.name);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                }
                            });

                        }
                    }).start();

                }
            }
        });
    }
}