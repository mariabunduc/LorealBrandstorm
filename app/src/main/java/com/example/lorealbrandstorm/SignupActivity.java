package com.example.lorealbrandstorm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    // Declararea variabilelor pentru elementele UI
    EditText signupName, signupUsername, signupEmail, signupPassword;
    TextView loginRedirectText;
    Button signupButton;

    // Referințe Firebase
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup); // Setează layout-ul pentru această activitate

        // Legătura cu elementele din layout
        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        // Ascultător pentru butonul de înregistrare
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obține valorile din câmpuri
                String name = signupName.getText().toString();
                String email = signupEmail.getText().toString();
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();

                // Verifică dacă câmpurile sunt goale
                if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Completați toate câmpurile!", Toast.LENGTH_SHORT).show();
                } else {
                    // Scrie datele în Firebase
                    database = FirebaseDatabase.getInstance();
                    reference = database.getReference("users");

                    // Creează un obiect HelperClass pentru a stoca datele
                    HelperClass helperClass = new HelperClass(name, email, username, password);

                    // Salvează datele în Firebase sub cheia "username"
                    reference.child(username).setValue(helperClass)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Înregistrare reușită!", Toast.LENGTH_SHORT).show();
                                    Log.d("FirebaseDebug", "Date scrise cu succes în Firebase");

                                    // Navighează către HomeActivity după înregistrare
                                    Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish(); // Închide activitatea de înregistrare
                                } else {
                                    // Afișează un mesaj de eroare dacă înregistrarea a eșuat
                                    Toast.makeText(SignupActivity.this, "Eroare la înregistrare: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("FirebaseError", "Eroare la înregistrare", task.getException());
                                }
                            });
                }
            }
        });

        // Ascultător pentru textul de redirecționare către login
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navighează către LoginActivity
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}