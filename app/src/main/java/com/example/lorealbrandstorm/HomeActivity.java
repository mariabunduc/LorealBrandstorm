package com.example.lorealbrandstorm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menuIcon;
    private NavigationView navigationView;
    private TextView profileName, profileEmail;
    private ImageView profileImage;
    private Button logoutButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerLayout);
        menuIcon = findViewById(R.id.menuIcon);
        navigationView = findViewById(R.id.navigationView);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        View headerView = navigationView.getHeaderView(0);
        profileName = headerView.findViewById(R.id.profileName);
        profileEmail = headerView.findViewById(R.id.profileEmail);
        profileImage = headerView.findViewById(R.id.profileImage);
        logoutButton = headerView.findViewById(R.id.logout_button);

        menuIcon.setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
                showStatusBar();
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
                hideStatusBar();
            }
        });

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        loadUserData();
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void showStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                profileEmail.setText(email);
                Log.d("FirebaseAuth", "Email utilizator: " + email);
            } else {
                profileEmail.setText("Email necunoscut");
                Log.d("FirebaseAuth", "Emailul utilizatorului este null");
            }

            String username = currentUser.getDisplayName();
            if (username != null) {
                db.collection("users").document(username).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    String name = document.getString("name");
                                    if (name != null) {
                                        profileName.setText(name);
                                        Log.d("Firestore", "Nume utilizator: " + name);
                                    } else {
                                        profileName.setText("Nume necunoscut");
                                        Log.d("Firestore", "Numele utilizatorului este null");
                                    }
                                } else {
                                    profileName.setText("Nume necunoscut");
                                    Log.d("Firestore", "Documentul nu există în Firestore pentru username: " + username);
                                }
                            } else {
                                profileName.setText("Eroare la încărcare");
                                Log.e("Firestore", "Eroare la preluarea datelor", task.getException());
                            }
                        });
            } else {
                profileName.setText("Username necunoscut");
                Log.d("FirebaseAuth", "Username-ul utilizatorului este null");
            }
        } else {
            profileName.setText("No user");
            profileEmail.setText("No email");
            Log.d("FirebaseAuth", "Niciun utilizator autentificat");
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            showStatusBar();
        } else {
            super.onBackPressed();
        }
    }
}