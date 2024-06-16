package com.example.garbagefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Register extends AppCompatActivity {

    // GET NECESSARY ELEMENTS

    Button registerButton;
    Button toLogin;
    EditText username;
    EditText password;
    TextView error;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        registerButton = findViewById(R.id.registerButton);
        username = findViewById(R.id.UsernameLogin);
        password = findViewById(R.id.UserPass);
        toLogin = findViewById(R.id.toLogin);
        error = findViewById(R.id.errorText);


        DatabaseClient db = DatabaseClient.getInstance(getApplicationContext());
        UserDao userDAO = db.getDb().userDao();



        toLogin.setOnClickListener(new View.OnClickListener() { // goes back to login
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().length() < 4){
                    error.setText("Username 4+ characters");
                }
                else if (password.getText().toString().length() < 5){
                    error.setText("Password 5+ characters");
                }
                else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (userDAO.findUserByUsername(username.getText().toString()) != null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        error.setText("Username Is Taken");
                                    }
                                });
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        error.setText("");
                                    }
                                });

                                // sends the current accepted the data to the next stage of register
                                Intent intent = new Intent(Register.this, profilePic.class);
                                intent.putExtra("name", username.getText().toString());
                                intent.putExtra("password", password.getText().toString());
                                startActivity(intent);
                            }
                        }
                    }).start();
                }
            }
        });

    }

}
