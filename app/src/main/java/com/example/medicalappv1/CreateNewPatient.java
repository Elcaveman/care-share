package com.example.medicalappv1;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;

import Misc.PasswordGenerator;
import firebase.*;

public class CreateNewPatient extends Fragment {

    EditText codePatient, nomPatient, prenomPatient, cinPatient, adressePatient, numeroPatient, emailPatient, mdpPatient, numeroDossier, dateNaissance;
    Button button;
    FloatingActionButton generateMdp, generateCode, generateNumeroDossier;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    firebase.CreateUser createUser = new CreateUser();
    firebase.DatabaseChecker databaseChecker = new DatabaseChecker();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Random random = new Random();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.adding_fragment, container, false);
        codePatient = view.findViewById(R.id.codePatient);
        numeroDossier = view.findViewById(R.id.numeroDossier);
        nomPatient = view.findViewById(R.id.nomPatient_addprelevement);
        prenomPatient = view.findViewById(R.id.prenomPatient);
        cinPatient = view.findViewById(R.id.cinPatient);
        adressePatient = view.findViewById(R.id.adressePatient);
        numeroPatient = view.findViewById(R.id.editTextPhone);
        emailPatient = view.findViewById(R.id.emailPatient);
        mdpPatient = view.findViewById(R.id.mdpPatient);
        button = view.findViewById(R.id.addButton);
        generateMdp = view.findViewById(R.id.generateMdp); generateCode = view.findViewById(R.id.generateCode); generateNumeroDossier = view.findViewById(R.id.generateNumeroDossier);
        dateNaissance = view.findViewById(R.id.dateNaissance);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(codePatient.getText().toString())) {
                    codePatient.setError("Un code est nécessaire");
                    return;
                }
                if (TextUtils.isEmpty(numeroDossier.getText().toString())) {
                    codePatient.setError("Un numéro de dossier est nécessaire");
                    return;
                }
                if (TextUtils.isEmpty(nomPatient.getText().toString())) {
                    nomPatient.setError("Un nom est nécessaire");
                    return;
                }
                if (TextUtils.isEmpty(prenomPatient.getText().toString())) {
                    prenomPatient.setError("Un prénom est nécessaire");
                    return;
                }
                if (TextUtils.isEmpty(cinPatient.getText().toString())) {
                    cinPatient.setError("Le cin est nécessaire");
                    return;
                }
                if (TextUtils.isEmpty(adressePatient.getText().toString())) {
                    adressePatient.setError("Une adresse est nécessaire");
                    return;
                }
                if (TextUtils.isEmpty(numeroPatient.getText().toString())) {
                    numeroPatient.setError("Un numéro est nécessaire");
                    return;
                }
                if (TextUtils.isEmpty(emailPatient.getText().toString())) {
                    emailPatient.setError("Un email est nécessaire");
                    return;
                }
                if (TextUtils.isEmpty(mdpPatient.getText().toString())) {
                    mdpPatient.setError("Un mot de passe est nécessaire");
                    return;
                }

                fStore.collection("Patients")
                        .whereEqualTo("Code", codePatient.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult().isEmpty()) {
                                    fAuth.createUserWithEmailAndPassword(emailPatient.getText().toString(), mdpPatient.getText().toString());
                                    createUser.addPatient(codePatient.getText().toString(), nomPatient.getText().toString(), prenomPatient.getText().toString(), cinPatient.getText().toString(), adressePatient.getText().toString(), Float.parseFloat(numeroPatient.getText().toString()), emailPatient.getText().toString(), mdpPatient.getText().toString(), dateNaissance.getText().toString());
                                    createUser.addPatientInLabo(codePatient.getText().toString(), numeroDossier.getText().toString());
                                    Toast.makeText(view.getContext(), "Patient créé avec succés !", Toast.LENGTH_SHORT).show();
                                } else {
                                    createUser.addPatientInLabo(codePatient.getText().toString(), numeroDossier.getText().toString());
                                    new AlertDialog.Builder(view.getContext())
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle("Ce patient existe déjà")
                                            .setMessage("Ce patient existe déjà dans la base de données, il est maintenant disponible dans la base de données du labo")
                                            .setNegativeButton("OK",null)
                                            .show();
                                    return;
                                }
                            }
                        });
            }
        });

        //generate password
        generateMdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordGenerator passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
                        .useDigits(true)
                        .useLower(true)
                        .useUpper(true)
                        .build();
                mdpPatient.setText(passwordGenerator.generate(10));
            }
        });

        //generate code patient
        generateCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("Patients")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        int generatedCode;
                                        final ArrayList<String> allCodes = new ArrayList<>();
                                        allCodes.add(document.getString("Code"));
                                        do {
                                            generatedCode = 100000 + random.nextInt(900000);
                                        } while (allCodes.contains(String.valueOf(generatedCode)));
                                        codePatient.setText(String.valueOf(generatedCode));
                                    }
                                }
                            }
                        });
            }
        });

        //generate num dossier
        generateNumeroDossier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailLabo = firebaseAuth.getCurrentUser().getEmail();
                fStore.collection("Labos")
                        .whereEqualTo("Email", emailLabo)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                               if (task.isSuccessful()) {
                                   for (QueryDocumentSnapshot document : task.getResult()) {
                                       final String laboID = document.getId();
                                       fStore.collection("Labos")
                                               .document(laboID)
                                               .collection("Patients")
                                               .get()
                                               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                       if (task.isSuccessful()) {
                                                           for (QueryDocumentSnapshot document : task.getResult()) {
                                                               String generatedNumeroDossier;
                                                               ArrayList<String> allNumeroDossier = new ArrayList<>();
                                                               allNumeroDossier.add(document.getString("NumeroDossier"));
                                                               do {
                                                                   PasswordGenerator passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
                                                                           .useDigits(true)
                                                                           .useLower(false)
                                                                           .useUpper(true)
                                                                           .build();
                                                                   generatedNumeroDossier = passwordGenerator.generate(7);
                                                               } while (allNumeroDossier.contains(generatedNumeroDossier));

                                                               numeroDossier.setText(generatedNumeroDossier);
                                                           }
                                                       }
                                                   }
                                               });
                                   }
                               }
                           }
                       });
            }
        });

        return view;
    }
}
