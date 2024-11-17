package com.example.udaan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    private static final String STATIC_PASSWORD = "12345";
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvWelcome = findViewById(R.id.tvWelcome);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(2000);
        tvWelcome.startAnimation(fadeIn);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputPassword = etPassword.getText().toString().trim();
                if (STATIC_PASSWORD.equals(inputPassword)) {
//                    Intent intent = new Intent(login.this, MainActivity.class);
//                    startActivity(intent);
                    Intent intent = new Intent(login.this, MainActivity.class);
                    intent.putExtra("showFragment", true); // Add extra to indicate that the fragment should be shown
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                } else {
                    Toast.makeText(login.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
