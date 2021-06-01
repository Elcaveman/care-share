package com.example.medicalappv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Misc.CommentaireDialogue;
import SendNotification.MyFirebaseInstanceIDService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CommentaireDialogue.ExampleDialogListener {
    private DrawerLayout drawerLayout;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    ArrayList<String> patientEmails = new ArrayList<>();
    ArrayList<String> laboEmails = new ArrayList<>();
    String userEmail;
    NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = this;
    Activity activity = this;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    public static String commentaire;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        //setTitle("Accueil");
        super.onCreate(savedInstanceState);

        //Setting up side menu depending on the account

        userEmail = firebaseAuth.getCurrentUser().getEmail();
        fStore.collection("Patients")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                patientEmails.add(document.getString("Email"));
                            }
                            fStore.collection("Labos")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    laboEmails.add(document.getString("Email"));
                                                }
                                                if (patientEmails.contains(userEmail)) {
                                                    Log.i("this is an", "user");

                                                    fStore.collection("Patients")
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(document.getString("Code"));
                                                                        }
                                                                        String email = firebaseAuth.getCurrentUser().getEmail();
                                                                        fStore.collection("Patients")
                                                                                .whereEqualTo("Email", email)
                                                                                .get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                                String codePatient = document.getString("Code");
                                                                                                FirebaseMessaging.getInstance().subscribeToTopic(codePatient);
                                                                                            }
                                                                                        }

                                                                                        Intent intent = getIntent();
                                                                                        boolean cameFromNotif = intent.getBooleanExtra("CameFromNotif", false);
                                                                                        if (cameFromNotif) {
                                                                                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationPatient()).commit();
                                                                                        }
                                                                                        intent.putExtra("CameFromNotif", false);
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                    setContentView(R.layout.patient_activity_main);

                                                    Toolbar toolbar = findViewById(R.id.toolbar);
                                                    setSupportActionBar(toolbar);

                                                    drawerLayout = findViewById(R.id.patient_drawer_layout);
                                                    NavigationView navigationView = findViewById(R.id.nav_view);
                                                    navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);


                                                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                                                    drawerLayout.addDrawerListener(toggle);
                                                    toggle.syncState();

                                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccueilFragment()).commit();

                                                    if (savedInstanceState == null) {
                                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccueilFragment()).commit();
                                                        navigationView.setCheckedItem(R.id.nav_home);
                                                    }
                                                }
                                                if (laboEmails.contains(userEmail)) {
                                                    Log.i("this a", "labo");
                                                    fStore.collection("Patients")
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(document.getString("Code"));
                                                                }
                                                            }
                                                        }
                                                    });
                                                    setContentView(R.layout.activity_main);

                                                    toolbar = findViewById(R.id.toolbar);
                                                    setSupportActionBar(toolbar);

                                                    drawerLayout = findViewById(R.id.drawer_layout);
                                                    navigationView = findViewById(R.id.nav_view);
                                                    navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);


                                                    toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                                                    drawerLayout.addDrawerListener(toggle);
                                                    toggle.syncState();

                                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccueilFragment()).commit();

                                                    if (savedInstanceState == null) {
                                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccueilFragment()).commit();
                                                        navigationView.setCheckedItem(R.id.nav_home);
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccueilFragment()).commit();
                break;
            case R.id.nav_resultat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Resultats()).commit();
                break;
            case R.id.nav_deconnexion:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
            case R.id.nav_add:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddPrelevement()).commit();
                break;
            case R.id.nav_notif:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationPatient()).commit();
                break;
            case R.id.nav_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Profile()).commit();
                break;
            case R.id.nav_location:
                startActivity(new Intent(getApplicationContext(), LabosProchesV2.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void applyTexts(String commentaireDialog) {
        commentaire = commentaireDialog ;
    }
}