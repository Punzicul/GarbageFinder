package com.example.garbagefinder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

public class Login extends AppCompatActivity {

    // GET NECESSARY ELEMENTS
    Button loginButton;
    Button toRegister;
    EditText username;
    EditText password;
    TextView errorText;


    public void logUserIn(String name) {
        errorText.setText("");

        Log.d("Login", "Proceeding to map page with name: " + name);
        Intent intent = new Intent(Login.this, mapPage.class);
        intent.putExtra("name", name); // passes name to the main application in order to retrieve client object
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        DatabaseClient db = DatabaseClient.getInstance(getApplicationContext());
        UserDao userDAO = db.getDb().userDao();

        loginButton = findViewById(R.id.LoginButton);
        username = findViewById(R.id.UsernameLogin);
        password = findViewById(R.id.UserPass);
        toRegister = findViewById(R.id.toRegister);
        errorText = findViewById(R.id.errorText);

        Log.d("check", "here");
        loginButton.setOnClickListener(new View.OnClickListener() { // checks if user data matches and if it does it lets the user login
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        User toCheck = userDAO.findUserByUsername(username.getText().toString());
                        if (toCheck == null || !Objects.equals(toCheck.password, password.getText().toString())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    errorText.setText("USER OR PASS INCORRECT");
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    logUserIn(toCheck.name);
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        userDAO.getAllUsers().observe(this, new Observer<List<User>>() { // gets updated list of all users
            @Override
            public void onChanged(List<User> users) {
                for (User user : users) {
                    Log.d("UserList", "User: " + user.name);
                }
            }
        });
    }
}
