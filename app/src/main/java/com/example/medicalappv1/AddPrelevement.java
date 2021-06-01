package com.example.medicalappv1;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import Misc.CommentaireDialogue;
import SendNotification.MySingleton;
import firebase.*;

import static android.app.Activity.RESULT_OK;

public class AddPrelevement extends Fragment {

    private static final String LOG_TAG = "bruh" ;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAelmrZxw:APA91bHZyqwU7TgfGm5T3smHEc7khpkK674tXmx82AqcSYb-dVYwgnd23thDH67L4xpV3737pJVVnrwOPb9vqknYhBxHCFDxMTVd_CtOC4VCtz6Z7Y1OJNDvcwV3trYcE9Fvja0Fr4_x";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    private ArrayList<String> codesPatients = new ArrayList<>();
    private ArrayList<String> nomAnalyses = new ArrayList<>();
    private ArrayList<String> nomsMedecins = new ArrayList<>();
    private ArrayList<String> numeroDossiers = new ArrayList<>();
    private firebase.DatabaseGetters databaseGetters = new DatabaseGetters();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    AutoCompleteTextView codePatientACTV, nomMedecin, numeroDossier;
    TextView  prelNomPatientTV;
    EditText datePrelET;
    FloatingActionButton addPatientButton, addAnalyseButton;
    AutoCompleteTextView analyse1, analyse2, analyse3, analyse4, analyse5, analyse6, analyse7, analyse8, analyse9, analyse10, analyse11;
    EditText valeur1, valeur2, valeur3, valeur4, valeur5, valeur6, valeur7, valeur8, valeur9, valeur10, valeur11;
    Spinner unite1, unite2, unite3, unite4, unite5, unite6, unite7, unite8, unite9, unite10, unite11;
    Button enregistrerButton, validerButton;
    int counter = 2;
    ArrayList<String> analyse1Array = new ArrayList<>(); ArrayList<String> analyse2Array = new ArrayList<>(); ArrayList<String> analyse3Array = new ArrayList<>(); ArrayList<String> analyse4Array = new ArrayList<>(); ArrayList<String> analyse5Array = new ArrayList<>(); ArrayList<String> analyse6Array = new ArrayList<>(); ArrayList<String> analyse7Array = new ArrayList<>(); ArrayList<String> analyse8Array = new ArrayList<>(); ArrayList<String> analyse9Array = new ArrayList<>(); ArrayList<String> analyse10Array = new ArrayList<>(); ArrayList<String> analyse11Array = new ArrayList<>();
    String medecinLastName; String medecinFirstName;
    SharedPreferences sharedPreferences;

    ImageView searchFile;
    Uri pdfUri;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ProgressBar progressBar;
    boolean pdfExists = false;

    ImageView recordAudio;
    private MediaRecorder recorder;
    private String fileName = null;
    boolean audioExists = false;

    ImageView addCommentaire;
    String commentaire = null;

    public AddPrelevement() {
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.ajouter_prelevement, container, false);

        codePatientACTV = view.findViewById(R.id.codePatient); nomMedecin = view.findViewById(R.id.nomMedecin); numeroDossier = view.findViewById(R.id.numeroDossier);
        prelNomPatientTV = view.findViewById(R.id.prelNomPatient);
        datePrelET = view.findViewById(R.id.datePrelevement);
        addPatientButton = view.findViewById(R.id.addPatientButton);
        addAnalyseButton = view.findViewById(R.id.addAnalyseButton);
        analyse1 = view.findViewById(R.id.analyse1); valeur1 = view.findViewById(R.id.valeur1); unite1 = view.findViewById(R.id.unite1);
        analyse2 = view.findViewById(R.id.analyse2); valeur2 = view.findViewById(R.id.valeur2); unite2 = view.findViewById(R.id.unite2);
        analyse3 = view.findViewById(R.id.analyse3); valeur3 = view.findViewById(R.id.valeur3); unite3 = view.findViewById(R.id.unite3);
        analyse4 = view.findViewById(R.id.analyse4); valeur4 = view.findViewById(R.id.valeur4); unite4 = view.findViewById(R.id.unite4);
        analyse5 = view.findViewById(R.id.analyse5); valeur5 = view.findViewById(R.id.valeur5); unite5 = view.findViewById(R.id.unite5);
        analyse6 = view.findViewById(R.id.analyse6); valeur6 = view.findViewById(R.id.valeur6); unite6 = view.findViewById(R.id.unite6);
        analyse7 = view.findViewById(R.id.analyse7); valeur7 = view.findViewById(R.id.valeur7); unite7 = view.findViewById(R.id.unite7);
        analyse8 = view.findViewById(R.id.analyse8); valeur8 = view.findViewById(R.id.valeur8); unite8 = view.findViewById(R.id.unite8);
        analyse9 = view.findViewById(R.id.analyse9); valeur9 = view.findViewById(R.id.valeur9); unite9 = view.findViewById(R.id.unite9);
        analyse10 = view.findViewById(R.id.analyse10); valeur10 = view.findViewById(R.id.valeur10); unite10 = view.findViewById(R.id.unite10);
        analyse11 = view.findViewById(R.id.analyse11); valeur11 = view.findViewById(R.id.valeur11); unite11 = view.findViewById(R.id.unite11);
        enregistrerButton = view.findViewById(R.id.enregisterButton); validerButton = view.findViewById(R.id.validerButton);
        searchFile= view.findViewById(R.id.searchFile);
        progressBar = view.findViewById(R.id.progressBar);
        recordAudio = view.findViewById(R.id.recordAudio);
        addCommentaire = view.findViewById(R.id.addCommentaire);

        fileName = getContext().getExternalCacheDir().getAbsolutePath();
        fileName += "/recorded_audio.mp3";

        //setting sharedPreferences
        sharedPreferences = getContext().getSharedPreferences("com.example.medicalappv1", Context.MODE_PRIVATE);
        numeroDossier.setText(sharedPreferences.getString("numeroDossier", ""));
        codePatientACTV.setText(sharedPreferences.getString("codePatient", ""));
        prelNomPatientTV.setText(sharedPreferences.getString("nomPatient", ""));
        nomMedecin.setText(sharedPreferences.getString("nomMedecin", ""));
        datePrelET.setText(sharedPreferences.getString("datePrelevement", ""));

        counter = sharedPreferences.getInt("counter", 2);
        switch(counter) {
            case 3 :
                analyse2.setVisibility(View.VISIBLE);
                valeur2.setVisibility(View.VISIBLE);
                unite2.setVisibility(View.VISIBLE);
                counter++;
                break;
            case 4 :
                analyse2.setVisibility(View.VISIBLE);
                valeur2.setVisibility(View.VISIBLE);
                unite2.setVisibility(View.VISIBLE);
                analyse3.setVisibility(View.VISIBLE);
                valeur3.setVisibility(View.VISIBLE);
                unite3.setVisibility(View.VISIBLE);
                counter++;
                break;
            case 5 :
                analyse2.setVisibility(View.VISIBLE);
                valeur2.setVisibility(View.VISIBLE);
                unite2.setVisibility(View.VISIBLE);
                analyse3.setVisibility(View.VISIBLE);
                valeur3.setVisibility(View.VISIBLE);
                unite3.setVisibility(View.VISIBLE);
                analyse4.setVisibility(View.VISIBLE);
                valeur4.setVisibility(View.VISIBLE);
                unite4.setVisibility(View.VISIBLE);
                counter++;
                break;
            case 6 :
                analyse2.setVisibility(View.VISIBLE);
                valeur2.setVisibility(View.VISIBLE);
                unite2.setVisibility(View.VISIBLE);
                analyse3.setVisibility(View.VISIBLE);
                valeur3.setVisibility(View.VISIBLE);
                unite3.setVisibility(View.VISIBLE);
                analyse4.setVisibility(View.VISIBLE);
                valeur4.setVisibility(View.VISIBLE);
                unite4.setVisibility(View.VISIBLE);
                analyse5.setVisibility(View.VISIBLE);
                valeur5.setVisibility(View.VISIBLE);
                unite5.setVisibility(View.VISIBLE);
                counter++;
                break;
            case 7 :
                analyse2.setVisibility(View.VISIBLE);
                valeur2.setVisibility(View.VISIBLE);
                unite2.setVisibility(View.VISIBLE);
                analyse3.setVisibility(View.VISIBLE);
                valeur3.setVisibility(View.VISIBLE);
                unite3.setVisibility(View.VISIBLE);
                analyse4.setVisibility(View.VISIBLE);
                valeur4.setVisibility(View.VISIBLE);
                unite4.setVisibility(View.VISIBLE);
                analyse5.setVisibility(View.VISIBLE);
                valeur5.setVisibility(View.VISIBLE);
                unite5.setVisibility(View.VISIBLE);
                analyse6.setVisibility(View.VISIBLE);
                valeur6.setVisibility(View.VISIBLE);
                unite6.setVisibility(View.VISIBLE);
                counter++;
                break;
            case 8 :
                analyse2.setVisibility(View.VISIBLE);
                valeur2.setVisibility(View.VISIBLE);
                unite2.setVisibility(View.VISIBLE);
                analyse3.setVisibility(View.VISIBLE);
                valeur3.setVisibility(View.VISIBLE);
                unite3.setVisibility(View.VISIBLE);
                analyse4.setVisibility(View.VISIBLE);
                valeur4.setVisibility(View.VISIBLE);
                unite4.setVisibility(View.VISIBLE);
                analyse5.setVisibility(View.VISIBLE);
                valeur5.setVisibility(View.VISIBLE);
                unite5.setVisibility(View.VISIBLE);
                analyse6.setVisibility(View.VISIBLE);
                valeur6.setVisibility(View.VISIBLE);
                unite6.setVisibility(View.VISIBLE);
                analyse7.setVisibility(View.VISIBLE);
                valeur7.setVisibility(View.VISIBLE);
                unite7.setVisibility(View.VISIBLE);
                counter++;
                break;
            case 9 :
                analyse2.setVisibility(View.VISIBLE);
                valeur2.setVisibility(View.VISIBLE);
                unite2.setVisibility(View.VISIBLE);
                analyse3.setVisibility(View.VISIBLE);
                valeur3.setVisibility(View.VISIBLE);
                unite3.setVisibility(View.VISIBLE);
                analyse4.setVisibility(View.VISIBLE);
                valeur4.setVisibility(View.VISIBLE);
                unite4.setVisibility(View.VISIBLE);
                analyse5.setVisibility(View.VISIBLE);
                valeur5.setVisibility(View.VISIBLE);
                unite5.setVisibility(View.VISIBLE);
                analyse6.setVisibility(View.VISIBLE);
                valeur6.setVisibility(View.VISIBLE);
                unite6.setVisibility(View.VISIBLE);
                analyse7.setVisibility(View.VISIBLE);
                valeur7.setVisibility(View.VISIBLE);
                unite7.setVisibility(View.VISIBLE);
                analyse8.setVisibility(View.VISIBLE);
                valeur8.setVisibility(View.VISIBLE);
                unite8.setVisibility(View.VISIBLE);
                counter++;
                break;
            case 10 :
                analyse2.setVisibility(View.VISIBLE);
                valeur2.setVisibility(View.VISIBLE);
                unite2.setVisibility(View.VISIBLE);
                analyse3.setVisibility(View.VISIBLE);
                valeur3.setVisibility(View.VISIBLE);
                unite3.setVisibility(View.VISIBLE);
                analyse4.setVisibility(View.VISIBLE);
                valeur4.setVisibility(View.VISIBLE);
                unite4.setVisibility(View.VISIBLE);
                analyse5.setVisibility(View.VISIBLE);
                valeur5.setVisibility(View.VISIBLE);
                unite5.setVisibility(View.VISIBLE);
                analyse6.setVisibility(View.VISIBLE);
                valeur6.setVisibility(View.VISIBLE);
                unite6.setVisibility(View.VISIBLE);
                analyse7.setVisibility(View.VISIBLE);
                valeur7.setVisibility(View.VISIBLE);
                unite7.setVisibility(View.VISIBLE);
                analyse8.setVisibility(View.VISIBLE);
                valeur8.setVisibility(View.VISIBLE);
                unite8.setVisibility(View.VISIBLE);
                analyse9.setVisibility(View.VISIBLE);
                valeur9.setVisibility(View.VISIBLE);
                unite9.setVisibility(View.VISIBLE);
                counter++;
                break;
            case 11 :
                analyse2.setVisibility(View.VISIBLE);
                valeur2.setVisibility(View.VISIBLE);
                unite2.setVisibility(View.VISIBLE);
                analyse3.setVisibility(View.VISIBLE);
                valeur3.setVisibility(View.VISIBLE);
                unite3.setVisibility(View.VISIBLE);
                analyse4.setVisibility(View.VISIBLE);
                valeur4.setVisibility(View.VISIBLE);
                unite4.setVisibility(View.VISIBLE);
                analyse5.setVisibility(View.VISIBLE);
                valeur5.setVisibility(View.VISIBLE);
                unite5.setVisibility(View.VISIBLE);
                analyse6.setVisibility(View.VISIBLE);
                valeur6.setVisibility(View.VISIBLE);
                unite6.setVisibility(View.VISIBLE);
                analyse7.setVisibility(View.VISIBLE);
                valeur7.setVisibility(View.VISIBLE);
                unite7.setVisibility(View.VISIBLE);
                analyse8.setVisibility(View.VISIBLE);
                valeur8.setVisibility(View.VISIBLE);
                unite8.setVisibility(View.VISIBLE);
                analyse9.setVisibility(View.VISIBLE);
                valeur9.setVisibility(View.VISIBLE);
                unite9.setVisibility(View.VISIBLE);
                analyse10.setVisibility(View.VISIBLE);
                valeur10.setVisibility(View.VISIBLE);
                unite10.setVisibility(View.VISIBLE);
                counter++;
                break;
            case 12 :
                analyse2.setVisibility(View.VISIBLE);
                valeur2.setVisibility(View.VISIBLE);
                unite2.setVisibility(View.VISIBLE);
                analyse3.setVisibility(View.VISIBLE);
                valeur3.setVisibility(View.VISIBLE);
                unite3.setVisibility(View.VISIBLE);
                analyse4.setVisibility(View.VISIBLE);
                valeur4.setVisibility(View.VISIBLE);
                unite4.setVisibility(View.VISIBLE);
                analyse5.setVisibility(View.VISIBLE);
                valeur5.setVisibility(View.VISIBLE);
                unite5.setVisibility(View.VISIBLE);
                analyse6.setVisibility(View.VISIBLE);
                valeur6.setVisibility(View.VISIBLE);
                unite6.setVisibility(View.VISIBLE);
                analyse7.setVisibility(View.VISIBLE);
                valeur7.setVisibility(View.VISIBLE);
                unite7.setVisibility(View.VISIBLE);
                analyse8.setVisibility(View.VISIBLE);
                valeur8.setVisibility(View.VISIBLE);
                unite8.setVisibility(View.VISIBLE);
                analyse9.setVisibility(View.VISIBLE);
                valeur9.setVisibility(View.VISIBLE);
                unite9.setVisibility(View.VISIBLE);
                analyse10.setVisibility(View.VISIBLE);
                valeur10.setVisibility(View.VISIBLE);
                unite10.setVisibility(View.VISIBLE);
                analyse11.setVisibility(View.VISIBLE);
                valeur11.setVisibility(View.VISIBLE);
                unite11.setVisibility(View.VISIBLE);
                counter++;
                break;
        }
        analyse1.setText(sharedPreferences.getString("analyse1", "")); valeur1.setText(sharedPreferences.getString("valeur1", ""));
        analyse2.setText(sharedPreferences.getString("analyse2", "")); valeur2.setText(sharedPreferences.getString("valeur2", ""));
        analyse3.setText(sharedPreferences.getString("analyse3", "")); valeur3.setText(sharedPreferences.getString("valeur3", ""));
        analyse4.setText(sharedPreferences.getString("analyse4", "")); valeur4.setText(sharedPreferences.getString("valeur4", ""));
        analyse5.setText(sharedPreferences.getString("analyse5", "")); valeur5.setText(sharedPreferences.getString("valeur5", ""));
        analyse6.setText(sharedPreferences.getString("analyse6", "")); valeur6.setText(sharedPreferences.getString("valeur6", ""));
        analyse7.setText(sharedPreferences.getString("analyse7", "")); valeur7.setText(sharedPreferences.getString("valeur7", ""));
        analyse8.setText(sharedPreferences.getString("analyse8", "")); valeur8.setText(sharedPreferences.getString("valeur8", ""));
        analyse9.setText(sharedPreferences.getString("analyse9", "")); valeur9.setText(sharedPreferences.getString("valeur9", ""));
        analyse10.setText(sharedPreferences.getString("analyse10", "")); valeur10.setText(sharedPreferences.getString("valeur10", ""));
        analyse11.setText(sharedPreferences.getString("analyse11", "")); valeur11.setText(sharedPreferences.getString("valeur11", ""));

        sharedPreferences.edit().putString("numeroDossier", "").apply();
        sharedPreferences.edit().putString("codePatient", "").apply();
        sharedPreferences.edit().putString("nomPatient", "").apply();
        sharedPreferences.edit().putString("nomMedecin", "").apply();
        sharedPreferences.edit().putString("datePrelevement", "").apply();
        sharedPreferences.edit().putInt("counter", 2).apply();
        sharedPreferences.edit().putString("analyse1", "").apply(); sharedPreferences.edit().putString("valeur1", "").apply();
        sharedPreferences.edit().putString("analyse2", "").apply(); sharedPreferences.edit().putString("valeur2", "").apply();
        sharedPreferences.edit().putString("analyse3", "").apply(); sharedPreferences.edit().putString("valeur3", "").apply();
        sharedPreferences.edit().putString("analyse4", "").apply(); sharedPreferences.edit().putString("valeur4", "").apply();
        sharedPreferences.edit().putString("analyse5", "").apply(); sharedPreferences.edit().putString("valeur5", "").apply();
        sharedPreferences.edit().putString("analyse6", "").apply(); sharedPreferences.edit().putString("valeur6", "").apply();
        sharedPreferences.edit().putString("analyse7", "").apply(); sharedPreferences.edit().putString("valeur7", "").apply();
        sharedPreferences.edit().putString("analyse8", "").apply(); sharedPreferences.edit().putString("valeur8", "").apply();
        sharedPreferences.edit().putString("analyse9", "").apply(); sharedPreferences.edit().putString("valeur9", "").apply();
        sharedPreferences.edit().putString("analyse10", "").apply(); sharedPreferences.edit().putString("valeur10", "").apply();
        sharedPreferences.edit().putString("analyse11", "").apply(); sharedPreferences.edit().putString("valeur11", "").apply();

        //setting autocomplete
        codesPatients = databaseGetters.getCodesPatient();
        ArrayAdapter<String> arrayAdapterCode = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, codesPatients);
        codePatientACTV.setAdapter(arrayAdapterCode);

        nomsMedecins = databaseGetters.getNomsMedecins();
        ArrayAdapter<String> arrayAdapterNomMedecin = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, nomsMedecins);
        nomMedecin.setAdapter(arrayAdapterNomMedecin);

        numeroDossiers = databaseGetters.getNumeroDossiers();
        ArrayAdapter<String> arrayAdapterNumeroDossier = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, numeroDossiers);
        numeroDossier.setAdapter(arrayAdapterNumeroDossier);
        
        //setting autofill
        codePatientACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                String codePatient = codePatientACTV.getText().toString();
                fStore.collection("Patients")
                        .whereEqualTo("Code", codePatient)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String nomPatient = document.getString("Nom") + " " + document.getString("Prenom");
                                        prelNomPatientTV.setText(nomPatient);
                                    }
                                }
                            }
                        });
        }
        });

        numeroDossier.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String numeroDossierText = numeroDossier.getText().toString();
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
                                                .whereEqualTo("NumeroDossier", numeroDossierText)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                                                String patientID = document.getString("ID_Patient");
                                                                fStore.collection("Patients")
                                                                        .document(patientID)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                                                    String codePatient = documentSnapshot.getString("Code");
                                                                                    String nomPatient = documentSnapshot.getString("Nom") + " " + documentSnapshot.getString("Prenom");
                                                                                    codePatientACTV.setText(codePatient);
                                                                                    prelNomPatientTV.setText(nomPatient);
                                                                                }
                                                                            }
                                                                        });
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

        //setting the date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        datePrelET.setText(formattedDate);

        //setting addPatient button

        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new CreateNewPatient());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //setting analyse autocomplete

        nomAnalyses = databaseGetters.getNomAnalyses();
        ArrayAdapter<String> arrayAdapterAnalyses = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, nomAnalyses);
        analyse1.setAdapter(arrayAdapterAnalyses);
        analyse2.setAdapter(arrayAdapterAnalyses);
        analyse3.setAdapter(arrayAdapterAnalyses);
        analyse4.setAdapter(arrayAdapterAnalyses);
        analyse5.setAdapter(arrayAdapterAnalyses);
        analyse6.setAdapter(arrayAdapterAnalyses);
        analyse7.setAdapter(arrayAdapterAnalyses);
        analyse8.setAdapter(arrayAdapterAnalyses);
        analyse9.setAdapter(arrayAdapterAnalyses);
        analyse10.setAdapter(arrayAdapterAnalyses);
        analyse11.setAdapter(arrayAdapterAnalyses);

        //setting adding analyses in view

        addAnalyseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(counter) {
                    case 2 :
                        analyse2.setVisibility(View.VISIBLE);
                        valeur2.setVisibility(View.VISIBLE);
                        unite2.setVisibility(View.VISIBLE);
                        counter++;
                        break;
                    case 3 :
                        analyse3.setVisibility(View.VISIBLE);
                        valeur3.setVisibility(View.VISIBLE);
                        unite3.setVisibility(View.VISIBLE);
                        counter++;
                        break;
                    case 4 :
                        analyse4.setVisibility(View.VISIBLE);
                        valeur4.setVisibility(View.VISIBLE);
                        unite4.setVisibility(View.VISIBLE);
                        counter++;
                        break;
                    case 5 :
                        analyse5.setVisibility(View.VISIBLE);
                        valeur5.setVisibility(View.VISIBLE);
                        unite5.setVisibility(View.VISIBLE);
                        counter++;
                        break;
                    case 6 :
                        analyse6.setVisibility(View.VISIBLE);
                        valeur6.setVisibility(View.VISIBLE);
                        unite6.setVisibility(View.VISIBLE);
                        counter++;
                        break;
                    case 7 :
                        analyse7.setVisibility(View.VISIBLE);
                        valeur7.setVisibility(View.VISIBLE);
                        unite7.setVisibility(View.VISIBLE);
                        counter++;
                        break;
                    case 8 :
                        analyse8.setVisibility(View.VISIBLE);
                        valeur8.setVisibility(View.VISIBLE);
                        unite8.setVisibility(View.VISIBLE);
                        counter++;
                        break;
                    case 9 :
                        analyse9.setVisibility(View.VISIBLE);
                        valeur9.setVisibility(View.VISIBLE);
                        unite9.setVisibility(View.VISIBLE);
                        counter++;
                        break;
                    case 10 :
                        analyse10.setVisibility(View.VISIBLE);
                        valeur10.setVisibility(View.VISIBLE);
                        unite10.setVisibility(View.VISIBLE);
                        counter++;
                        break;
                    case 11 :
                        analyse11.setVisibility(View.VISIBLE);
                        valeur11.setVisibility(View.VISIBLE);
                        unite11.setVisibility(View.VISIBLE);
                        counter++;
                        break;
                }

            }
        });

        setSpinnerContent(analyse1, unite1, view.getContext());
        setSpinnerContent(analyse2, unite2, view.getContext());
        setSpinnerContent(analyse3, unite3, view.getContext());
        setSpinnerContent(analyse4, unite4, view.getContext());
        setSpinnerContent(analyse5, unite5, view.getContext());
        setSpinnerContent(analyse6, unite6, view.getContext());
        setSpinnerContent(analyse7, unite7, view.getContext());
        setSpinnerContent(analyse8, unite8, view.getContext());
        setSpinnerContent(analyse9, unite9, view.getContext());
        setSpinnerContent(analyse10, unite10, view.getContext());
        setSpinnerContent(analyse11, unite11, view.getContext());

        validerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentaire = MainActivity.commentaire;
                progressBar.setVisibility(View.VISIBLE);

                fStore.collection("Patients")
                        .whereEqualTo("Code", codePatientACTV.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        final String patientID = document.getId();

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
                                                                String temp = nomMedecin.getText().toString();
                                                                String[] splitted = temp.split("\\s+");
                                                                medecinLastName = splitted[0];
                                                                medecinFirstName = splitted[1];
                                                                fStore.collection("Medecins")
                                                                        .whereEqualTo("Nom", medecinLastName)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    for (final QueryDocumentSnapshot document : task.getResult()) {
                                                                                        String medecinID = document.getId();
                                                                                        Map<String, Object> prelevementData = new HashMap<>();
                                                                                        prelevementData.put("Date", datePrelET.getText().toString());
                                                                                        prelevementData.put("ID_Medecin", medecinID);
                                                                                        prelevementData.put("ID_Labo", laboID);
                                                                                        prelevementData.put("Seen", false);

                                                                                        switch(counter) {
                                                                                            case 2 :
                                                                                                analyse1Array.add(analyse1.getText().toString()); analyse1Array.add(valeur1.getText().toString()); analyse1Array.add(unite1.getSelectedItem().toString());
                                                                                                prelevementData.put(analyse1Array.get(0), analyse1Array);
                                                                                                break;
                                                                                            case 3 :
                                                                                                analyse1Array.add(analyse1.getText().toString()); analyse1Array.add(valeur1.getText().toString()); analyse1Array.add(unite1.getSelectedItem().toString());
                                                                                                analyse2Array.add(analyse2.getText().toString()); analyse2Array.add(valeur2.getText().toString()); analyse2Array.add(unite2.getSelectedItem().toString());
                                                                                                prelevementData.put(analyse1Array.get(0), analyse1Array);
                                                                                                prelevementData.put(analyse2Array.get(0), analyse2Array);
                                                                                                break;
                                                                                            case 4 :
                                                                                                analyse1Array.add(analyse1.getText().toString()); analyse1Array.add(valeur1.getText().toString()); analyse1Array.add(unite1.getSelectedItem().toString());
                                                                                                analyse2Array.add(analyse2.getText().toString()); analyse2Array.add(valeur2.getText().toString()); analyse2Array.add(unite2.getSelectedItem().toString());
                                                                                                analyse3Array.add(analyse3.getText().toString()); analyse3Array.add(valeur3.getText().toString()); analyse3Array.add(unite3.getSelectedItem().toString());
                                                                                                prelevementData.put(analyse1Array.get(0), analyse1Array);
                                                                                                prelevementData.put(analyse2Array.get(0), analyse2Array);
                                                                                                prelevementData.put(analyse3Array.get(0), analyse3Array);
                                                                                                break;
                                                                                            case 5 :
                                                                                                analyse1Array.add(analyse1.getText().toString()); analyse1Array.add(valeur1.getText().toString()); analyse1Array.add(unite1.getSelectedItem().toString());
                                                                                                analyse2Array.add(analyse2.getText().toString()); analyse2Array.add(valeur2.getText().toString()); analyse2Array.add(unite2.getSelectedItem().toString());
                                                                                                analyse3Array.add(analyse3.getText().toString()); analyse3Array.add(valeur3.getText().toString()); analyse3Array.add(unite3.getSelectedItem().toString());
                                                                                                analyse4Array.add(analyse4.getText().toString()); analyse4Array.add(valeur4.getText().toString()); analyse4Array.add(unite4.getSelectedItem().toString());
                                                                                                prelevementData.put(analyse1Array.get(0), analyse1Array);
                                                                                                prelevementData.put(analyse2Array.get(0), analyse2Array);
                                                                                                prelevementData.put(analyse3Array.get(0), analyse3Array);
                                                                                                prelevementData.put(analyse4Array.get(0), analyse4Array);
                                                                                                break;
                                                                                            case 6 :
                                                                                                analyse1Array.add(analyse1.getText().toString()); analyse1Array.add(valeur1.getText().toString()); analyse1Array.add(unite1.getSelectedItem().toString());
                                                                                                analyse2Array.add(analyse2.getText().toString()); analyse2Array.add(valeur2.getText().toString()); analyse2Array.add(unite2.getSelectedItem().toString());
                                                                                                analyse3Array.add(analyse3.getText().toString()); analyse3Array.add(valeur3.getText().toString()); analyse3Array.add(unite3.getSelectedItem().toString());
                                                                                                analyse4Array.add(analyse4.getText().toString()); analyse4Array.add(valeur4.getText().toString()); analyse4Array.add(unite4.getSelectedItem().toString());
                                                                                                analyse5Array.add(analyse5.getText().toString()); analyse5Array.add(valeur5.getText().toString()); analyse5Array.add(unite5.getSelectedItem().toString());
                                                                                                prelevementData.put(analyse1Array.get(0), analyse1Array);
                                                                                                prelevementData.put(analyse2Array.get(0), analyse2Array);
                                                                                                prelevementData.put(analyse3Array.get(0), analyse3Array);
                                                                                                prelevementData.put(analyse4Array.get(0), analyse4Array);
                                                                                                prelevementData.put(analyse5Array.get(0), analyse5Array);
                                                                                                break;
                                                                                            case 7 :
                                                                                                analyse1Array.add(analyse1.getText().toString()); analyse1Array.add(valeur1.getText().toString()); analyse1Array.add(unite1.getSelectedItem().toString());
                                                                                                analyse2Array.add(analyse2.getText().toString()); analyse2Array.add(valeur2.getText().toString()); analyse2Array.add(unite2.getSelectedItem().toString());
                                                                                                analyse3Array.add(analyse3.getText().toString()); analyse3Array.add(valeur3.getText().toString()); analyse3Array.add(unite3.getSelectedItem().toString());
                                                                                                analyse4Array.add(analyse4.getText().toString()); analyse4Array.add(valeur4.getText().toString()); analyse4Array.add(unite4.getSelectedItem().toString());
                                                                                                analyse5Array.add(analyse5.getText().toString()); analyse5Array.add(valeur5.getText().toString()); analyse5Array.add(unite5.getSelectedItem().toString());
                                                                                                analyse6Array.add(analyse6.getText().toString()); analyse6Array.add(valeur6.getText().toString()); analyse6Array.add(unite6.getSelectedItem().toString());
                                                                                                prelevementData.put(analyse1Array.get(0), analyse1Array);
                                                                                                prelevementData.put(analyse2Array.get(0), analyse2Array);
                                                                                                prelevementData.put(analyse3Array.get(0), analyse3Array);
                                                                                                prelevementData.put(analyse4Array.get(0), analyse4Array);
                                                                                                prelevementData.put(analyse5Array.get(0), analyse5Array);
                                                                                                prelevementData.put(analyse6Array.get(0), analyse6Array);
                                                                                                break;
                                                                                            case 8 :
                                                                                                analyse1Array.add(analyse1.getText().toString()); analyse1Array.add(valeur1.getText().toString()); analyse1Array.add(unite1.getSelectedItem().toString());
                                                                                                analyse2Array.add(analyse2.getText().toString()); analyse2Array.add(valeur2.getText().toString()); analyse2Array.add(unite2.getSelectedItem().toString());
                                                                                                analyse3Array.add(analyse3.getText().toString()); analyse3Array.add(valeur3.getText().toString()); analyse3Array.add(unite3.getSelectedItem().toString());
                                                                                                analyse4Array.add(analyse4.getText().toString()); analyse4Array.add(valeur4.getText().toString()); analyse4Array.add(unite4.getSelectedItem().toString());
                                                                                                analyse5Array.add(analyse5.getText().toString()); analyse5Array.add(valeur5.getText().toString()); analyse5Array.add(unite5.getSelectedItem().toString());
                                                                                                analyse6Array.add(analyse6.getText().toString()); analyse6Array.add(valeur6.getText().toString()); analyse6Array.add(unite6.getSelectedItem().toString());
                                                                                                analyse7Array.add(analyse7.getText().toString()); analyse7Array.add(valeur7.getText().toString()); analyse7Array.add(unite7.getSelectedItem().toString());
                                                                                                prelevementData.put(analyse1Array.get(0), analyse1Array);
                                                                                                prelevementData.put(analyse2Array.get(0), analyse2Array);
                                                                                                prelevementData.put(analyse3Array.get(0), analyse3Array);
                                                                                                prelevementData.put(analyse4Array.get(0), analyse4Array);
                                                                                                prelevementData.put(analyse5Array.get(0), analyse5Array);
                                                                                                prelevementData.put(analyse6Array.get(0), analyse6Array);
                                                                                                prelevementData.put(analyse7Array.get(0), analyse7Array);
                                                                                                break;
                                                                                            case 9 :
                                                                                                analyse1Array.add(analyse1.getText().toString()); analyse1Array.add(valeur1.getText().toString()); analyse1Array.add(unite1.getSelectedItem().toString());
                                                                                                analyse2Array.add(analyse2.getText().toString()); analyse2Array.add(valeur2.getText().toString()); analyse2Array.add(unite2.getSelectedItem().toString());
                                                                                                analyse3Array.add(analyse3.getText().toString()); analyse3Array.add(valeur3.getText().toString()); analyse3Array.add(unite3.getSelectedItem().toString());
                                                                                                analyse4Array.add(analyse4.getText().toString()); analyse4Array.add(valeur4.getText().toString()); analyse4Array.add(unite4.getSelectedItem().toString());
                                                                                                analyse5Array.add(analyse5.getText().toString()); analyse5Array.add(valeur5.getText().toString()); analyse5Array.add(unite5.getSelectedItem().toString());
                                                                                                analyse6Array.add(analyse6.getText().toString()); analyse6Array.add(valeur6.getText().toString()); analyse6Array.add(unite6.getSelectedItem().toString());
                                                                                                analyse7Array.add(analyse7.getText().toString()); analyse7Array.add(valeur7.getText().toString()); analyse7Array.add(unite7.getSelectedItem().toString());
                                                                                                analyse8Array.add(analyse8.getText().toString()); analyse8Array.add(valeur8.getText().toString()); analyse8Array.add(unite8.getSelectedItem().toString());
                                                                                                prelevementData.put(analyse1Array.get(0), analyse1Array);
                                                                                                prelevementData.put(analyse2Array.get(0), analyse2Array);
                                                                                                prelevementData.put(analyse3Array.get(0), analyse3Array);
                                                                                                prelevementData.put(analyse4Array.get(0), analyse4Array);
                                                                                                prelevementData.put(analyse5Array.get(0), analyse5Array);
                                                                                                prelevementData.put(analyse6Array.get(0), analyse6Array);
                                                                                                prelevementData.put(analyse7Array.get(0), analyse7Array);
                                                                                                prelevementData.put(analyse8Array.get(0), analyse8Array);
                                                                                                break;
                                                                                            case 10 :
                                                                                                analyse1Array.add(analyse1.getText().toString()); analyse1Array.add(valeur1.getText().toString()); analyse1Array.add(unite1.getSelectedItem().toString());
                                                                                                analyse2Array.add(analyse2.getText().toString()); analyse2Array.add(valeur2.getText().toString()); analyse2Array.add(unite2.getSelectedItem().toString());
                                                                                                analyse3Array.add(analyse3.getText().toString()); analyse3Array.add(valeur3.getText().toString()); analyse3Array.add(unite3.getSelectedItem().toString());
                                                                                                analyse4Array.add(analyse4.getText().toString()); analyse4Array.add(valeur4.getText().toString()); analyse4Array.add(unite4.getSelectedItem().toString());
                                                                                                analyse5Array.add(analyse5.getText().toString()); analyse5Array.add(valeur5.getText().toString()); analyse5Array.add(unite5.getSelectedItem().toString());
                                                                                                analyse6Array.add(analyse6.getText().toString()); analyse6Array.add(valeur6.getText().toString()); analyse6Array.add(unite6.getSelectedItem().toString());
                                                                                                analyse7Array.add(analyse7.getText().toString()); analyse7Array.add(valeur7.getText().toString()); analyse7Array.add(unite7.getSelectedItem().toString());
                                                                                                analyse8Array.add(analyse8.getText().toString()); analyse8Array.add(valeur8.getText().toString()); analyse8Array.add(unite8.getSelectedItem().toString());
                                                                                                analyse9Array.add(analyse9.getText().toString()); analyse9Array.add(valeur9.getText().toString()); analyse9Array.add(unite9.getSelectedItem().toString());
                                                                                                prelevementData.put(analyse1Array.get(0), analyse1Array);
                                                                                                prelevementData.put(analyse2Array.get(0), analyse2Array);
                                                                                                prelevementData.put(analyse3Array.get(0), analyse3Array);
                                                                                                prelevementData.put(analyse4Array.get(0), analyse4Array);
                                                                                                prelevementData.put(analyse5Array.get(0), analyse5Array);
                                                                                                prelevementData.put(analyse6Array.get(0), analyse6Array);
                                                                                                prelevementData.put(analyse7Array.get(0), analyse7Array);
                                                                                                prelevementData.put(analyse8Array.get(0), analyse8Array);
                                                                                                prelevementData.put(analyse9Array.get(0), analyse9Array);
                                                                                                break;
                                                                                            case 11 :
                                                                                                analyse1Array.add(analyse1.getText().toString()); analyse1Array.add(valeur1.getText().toString()); analyse1Array.add(unite1.getSelectedItem().toString());
                                                                                                analyse2Array.add(analyse2.getText().toString()); analyse2Array.add(valeur2.getText().toString()); analyse2Array.add(unite2.getSelectedItem().toString());
                                                                                                analyse3Array.add(analyse3.getText().toString()); analyse3Array.add(valeur3.getText().toString()); analyse3Array.add(unite3.getSelectedItem().toString());
                                                                                                analyse4Array.add(analyse4.getText().toString()); analyse4Array.add(valeur4.getText().toString()); analyse4Array.add(unite4.getSelectedItem().toString());
                                                                                                analyse5Array.add(analyse5.getText().toString()); analyse5Array.add(valeur5.getText().toString()); analyse5Array.add(unite5.getSelectedItem().toString());
                                                                                                analyse6Array.add(analyse6.getText().toString()); analyse6Array.add(valeur6.getText().toString()); analyse6Array.add(unite6.getSelectedItem().toString());
                                                                                                analyse7Array.add(analyse7.getText().toString()); analyse7Array.add(valeur7.getText().toString()); analyse7Array.add(unite7.getSelectedItem().toString());
                                                                                                analyse8Array.add(analyse8.getText().toString()); analyse8Array.add(valeur8.getText().toString()); analyse8Array.add(unite8.getSelectedItem().toString());
                                                                                                analyse9Array.add(analyse9.getText().toString()); analyse9Array.add(valeur9.getText().toString()); analyse9Array.add(unite9.getSelectedItem().toString());
                                                                                                analyse10Array.add(analyse10.getText().toString()); analyse10Array.add(valeur10.getText().toString()); analyse10Array.add(unite10.getSelectedItem().toString());
                                                                                                prelevementData.put(analyse1Array.get(0), analyse1Array);
                                                                                                prelevementData.put(analyse2Array.get(0), analyse2Array);
                                                                                                prelevementData.put(analyse3Array.get(0), analyse3Array);
                                                                                                prelevementData.put(analyse4Array.get(0), analyse4Array);
                                                                                                prelevementData.put(analyse5Array.get(0), analyse5Array);
                                                                                                prelevementData.put(analyse6Array.get(0), analyse6Array);
                                                                                                prelevementData.put(analyse7Array.get(0), analyse7Array);
                                                                                                prelevementData.put(analyse8Array.get(0), analyse8Array);
                                                                                                prelevementData.put(analyse9Array.get(0), analyse9Array);
                                                                                                prelevementData.put(analyse10Array.get(0), analyse10Array);
                                                                                                break;
                                                                                            case 12 :
                                                                                                analyse1Array.add(analyse1.getText().toString()); analyse1Array.add(valeur1.getText().toString()); analyse1Array.add(unite1.getSelectedItem().toString());
                                                                                                analyse2Array.add(analyse2.getText().toString()); analyse2Array.add(valeur2.getText().toString()); analyse2Array.add(unite2.getSelectedItem().toString());
                                                                                                analyse3Array.add(analyse3.getText().toString()); analyse3Array.add(valeur3.getText().toString()); analyse3Array.add(unite3.getSelectedItem().toString());
                                                                                                analyse4Array.add(analyse4.getText().toString()); analyse4Array.add(valeur4.getText().toString()); analyse4Array.add(unite4.getSelectedItem().toString());
                                                                                                analyse5Array.add(analyse5.getText().toString()); analyse5Array.add(valeur5.getText().toString()); analyse5Array.add(unite5.getSelectedItem().toString());
                                                                                                analyse6Array.add(analyse6.getText().toString()); analyse6Array.add(valeur6.getText().toString()); analyse6Array.add(unite6.getSelectedItem().toString());
                                                                                                analyse7Array.add(analyse7.getText().toString()); analyse7Array.add(valeur7.getText().toString()); analyse7Array.add(unite7.getSelectedItem().toString());
                                                                                                analyse8Array.add(analyse8.getText().toString()); analyse8Array.add(valeur8.getText().toString()); analyse8Array.add(unite8.getSelectedItem().toString());
                                                                                                analyse9Array.add(analyse9.getText().toString()); analyse9Array.add(valeur9.getText().toString()); analyse9Array.add(unite9.getSelectedItem().toString());
                                                                                                analyse10Array.add(analyse10.getText().toString()); analyse10Array.add(valeur10.getText().toString()); analyse10Array.add(unite10.getSelectedItem().toString());
                                                                                                analyse11Array.add(analyse11.getText().toString()); analyse11Array.add(valeur11.getText().toString()); analyse11Array.add(unite11.getSelectedItem().toString());
                                                                                                prelevementData.put(analyse1Array.get(0), analyse1Array);
                                                                                                prelevementData.put(analyse2Array.get(0), analyse2Array);
                                                                                                prelevementData.put(analyse3Array.get(0), analyse3Array);
                                                                                                prelevementData.put(analyse4Array.get(0), analyse4Array);
                                                                                                prelevementData.put(analyse5Array.get(0), analyse5Array);
                                                                                                prelevementData.put(analyse6Array.get(0), analyse6Array);
                                                                                                prelevementData.put(analyse7Array.get(0), analyse7Array);
                                                                                                prelevementData.put(analyse8Array.get(0), analyse8Array);
                                                                                                prelevementData.put(analyse9Array.get(0), analyse9Array);
                                                                                                prelevementData.put(analyse10Array.get(0), analyse10Array);
                                                                                                prelevementData.put(analyse11Array.get(0), analyse11Array);
                                                                                                break;
                                                                                        }

                                                                                        if (pdfExists && audioExists) {
                                                                                            prelevementData.put("HasRapport", true);
                                                                                            prelevementData.put("HasAudio", true);
                                                                                            prelevementData.put("Interpretation", commentaire);
                                                                                            fStore.collection("Patients")
                                                                                                    .document(patientID)
                                                                                                    .collection("Prelevement")
                                                                                                    .add(prelevementData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                                @Override
                                                                                                public void onSuccess(DocumentReference documentReference) {
                                                                                                    final String prelevementID = documentReference.getId();

                                                                                                    StorageReference storageReference = storage.getReference();
                                                                                                    StorageReference audioReference = storageReference.child("Audio").child(prelevementID).child("new_audio.mp3");
                                                                                                    Uri uri = Uri.fromFile(new File(fileName));
                                                                                                    audioReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                            String fileName = "Rapport";
                                                                                                            StorageReference storageReference = storage.getReference();
                                                                                                            storageReference.child("Rapports").child(prelevementID).child(fileName).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                                                @Override
                                                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                                    progressBar.setVisibility(View.GONE);
                                                                                                                    Toast.makeText(view.getContext(), "Prélévement ajouté avec succés !", Toast.LENGTH_SHORT).show();
                                                                                                                    TOPIC = "/topics/" + codePatientACTV.getText().toString();
                                                                                                                    Log.i("topic", TOPIC); //topic must match with what the receiver subscribed to
                                                                                                                    NOTIFICATION_TITLE = "Title";
                                                                                                                    NOTIFICATION_MESSAGE = "notification message";

                                                                                                                    JSONObject notification = new JSONObject();
                                                                                                                    JSONObject notifcationBody = new JSONObject();
                                                                                                                    try {
                                                                                                                        notifcationBody.put("title", NOTIFICATION_TITLE);
                                                                                                                        notifcationBody.put("message", NOTIFICATION_MESSAGE);

                                                                                                                        notification.put("to", TOPIC);
                                                                                                                        notification.put("data", notifcationBody);
                                                                                                                    } catch (JSONException e) {
                                                                                                                        Log.i(TAG, "onCreate: " + e.getMessage() );
                                                                                                                    }
                                                                                                                    sendNotification(notification);
                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    });

                                                                                                }
                                                                                            });
                                                                                        }

                                                                                        else if (pdfExists) {
                                                                                            prelevementData.put("HasRapport", true);
                                                                                            prelevementData.put("HasAudio", false);
                                                                                            prelevementData.put("Interpretation", commentaire);
                                                                                            fStore.collection("Patients")
                                                                                                    .document(patientID)
                                                                                                    .collection("Prelevement")
                                                                                                    .add(prelevementData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                                @Override
                                                                                                public void onSuccess(DocumentReference documentReference) {
                                                                                                    String prelevementID = documentReference.getId();
                                                                                                    String fileName = "Rapport";
                                                                                                    StorageReference storageReference = storage.getReference();
                                                                                                    storageReference.child("Rapports").child(prelevementID).child(fileName).putFile(pdfUri)
                                                                                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                                                @Override
                                                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                                    progressBar.setVisibility(View.GONE);
                                                                                                                    Toast.makeText(view.getContext(), "Prélévement ajouté avec succés !", Toast.LENGTH_SHORT).show();
                                                                                                                    TOPIC = "/topics/" + codePatientACTV.getText().toString();
                                                                                                                    Log.i("topic", TOPIC); //topic must match with what the receiver subscribed to
                                                                                                                    NOTIFICATION_TITLE = "Title";
                                                                                                                    NOTIFICATION_MESSAGE = "notification message";

                                                                                                                    JSONObject notification = new JSONObject();
                                                                                                                    JSONObject notifcationBody = new JSONObject();
                                                                                                                    try {
                                                                                                                        notifcationBody.put("title", NOTIFICATION_TITLE);
                                                                                                                        notifcationBody.put("message", NOTIFICATION_MESSAGE);

                                                                                                                        notification.put("to", TOPIC);
                                                                                                                        notification.put("data", notifcationBody);
                                                                                                                    } catch (JSONException e) {
                                                                                                                        Log.i(TAG, "onCreate: " + e.getMessage() );
                                                                                                                    }
                                                                                                                    sendNotification(notification);
                                                                                                                }
                                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            Toast.makeText(getContext(), "Erreur durant l'envoie du PDF", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            });
                                                                                        }

                                                                                        else if (audioExists) {
                                                                                            prelevementData.put("HasAudio", true);
                                                                                            prelevementData.put("HasRapport", false);
                                                                                            prelevementData.put("Interpretation", commentaire);
                                                                                            fStore.collection("Patients")
                                                                                                    .document(patientID)
                                                                                                    .collection("Prelevement")
                                                                                                    .add(prelevementData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                                @Override
                                                                                                public void onSuccess(DocumentReference documentReference) {
                                                                                                    String prelevementID = documentReference.getId();

                                                                                                    StorageReference storageReference = storage.getReference();
                                                                                                    StorageReference audioReference = storageReference.child("Audio").child(prelevementID).child("new_audio.mp3");
                                                                                                    Uri uri = Uri.fromFile(new File(fileName));
                                                                                                    audioReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                                                @Override
                                                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                                    progressBar.setVisibility(View.GONE);
                                                                                                                    Toast.makeText(view.getContext(), "Prélévement ajouté avec succés !", Toast.LENGTH_SHORT).show();
                                                                                                                    TOPIC = "/topics/" + codePatientACTV.getText().toString();
                                                                                                                    Log.i("topic", TOPIC); //topic must match with what the receiver subscribed to
                                                                                                                    NOTIFICATION_TITLE = "Title";
                                                                                                                    NOTIFICATION_MESSAGE = "notification message";

                                                                                                                    JSONObject notification = new JSONObject();
                                                                                                                    JSONObject notifcationBody = new JSONObject();
                                                                                                                    try {
                                                                                                                        notifcationBody.put("title", NOTIFICATION_TITLE);
                                                                                                                        notifcationBody.put("message", NOTIFICATION_MESSAGE);

                                                                                                                        notification.put("to", TOPIC);
                                                                                                                        notification.put("data", notifcationBody);
                                                                                                                    } catch (JSONException e) {
                                                                                                                        Log.i(TAG, "onCreate: " + e.getMessage() );
                                                                                                                    }
                                                                                                                    sendNotification(notification);
                                                                                                                }
                                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            Toast.makeText(getContext(), "Erreur durant l'envoie de l'audio", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            });
                                                                                        }

                                                                                        else {
                                                                                            prelevementData.put("HasRapport", false);
                                                                                            prelevementData.put("HasAudio", false);
                                                                                            prelevementData.put("Interpretation", commentaire);
                                                                                            fStore.collection("Patients")
                                                                                                    .document(patientID)
                                                                                                    .collection("Prelevement")
                                                                                                    .add(prelevementData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                                @Override
                                                                                                public void onSuccess(DocumentReference documentReference) {
                                                                                                    progressBar.setVisibility(View.GONE);
                                                                                                    Toast.makeText(view.getContext(), "Prélévement ajouté avec succés !", Toast.LENGTH_SHORT).show();
                                                                                                    TOPIC = "/topics/" + codePatientACTV.getText().toString();
                                                                                                    Log.i("topic", TOPIC); //topic must match with what the receiver subscribed to
                                                                                                    NOTIFICATION_TITLE = "Title";
                                                                                                    NOTIFICATION_MESSAGE = "notification message";

                                                                                                    JSONObject notification = new JSONObject();
                                                                                                    JSONObject notifcationBody = new JSONObject();
                                                                                                    try {
                                                                                                        notifcationBody.put("title", NOTIFICATION_TITLE);
                                                                                                        notifcationBody.put("message", NOTIFICATION_MESSAGE);

                                                                                                        notification.put("to", TOPIC);
                                                                                                        notification.put("data", notifcationBody);
                                                                                                    } catch (JSONException e) {
                                                                                                        Log.i(TAG, "onCreate: " + e.getMessage() );
                                                                                                    }
                                                                                                    sendNotification(notification);
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        });
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

        enregistrerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().putString("numeroDossier", String.valueOf(numeroDossier.getText())).apply();
                sharedPreferences.edit().putString("codePatient", String.valueOf(codePatientACTV.getText())).apply();
                sharedPreferences.edit().putString("nomPatient", String.valueOf(prelNomPatientTV.getText())).apply();
                sharedPreferences.edit().putString("nomMedecin", String.valueOf(nomMedecin.getText())).apply();
                sharedPreferences.edit().putString("datePrelevement", String.valueOf(datePrelET.getText())).apply();
                sharedPreferences.edit().putInt("counter", counter).apply();

                switch (counter) {
                    case 2:
                        sharedPreferences.edit().putString("analyse1", String.valueOf(analyse1.getText())).apply();
                        sharedPreferences.edit().putString("valeur1", String.valueOf(valeur1.getText())).apply();
                        sharedPreferences.edit().putString("unite1", unite1.getSelectedItem().toString()).apply();
                        break;
                    case 3:
                        sharedPreferences.edit().putString("analyse1", String.valueOf(analyse1.getText())).apply();
                        sharedPreferences.edit().putString("valeur1", String.valueOf(valeur1.getText())).apply();
                        sharedPreferences.edit().putString("unite1", unite1.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse2", String.valueOf(analyse2.getText())).apply();
                        sharedPreferences.edit().putString("valeur2", String.valueOf(valeur2.getText())).apply();
                        sharedPreferences.edit().putString("unite2", unite2.getSelectedItem().toString()).apply();
                        break;
                    case 4:
                        sharedPreferences.edit().putString("analyse1", String.valueOf(analyse1.getText())).apply();
                        sharedPreferences.edit().putString("valeur1", String.valueOf(valeur1.getText())).apply();
                        sharedPreferences.edit().putString("unite1", unite1.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse2", String.valueOf(analyse2.getText())).apply();
                        sharedPreferences.edit().putString("valeur2", String.valueOf(valeur2.getText())).apply();
                        sharedPreferences.edit().putString("unite2", unite2.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse3", String.valueOf(analyse3.getText())).apply();
                        sharedPreferences.edit().putString("valeur3", String.valueOf(valeur3.getText())).apply();
                        sharedPreferences.edit().putString("unite3", unite3.getSelectedItem().toString()).apply();
                        break;
                    case 5:
                        sharedPreferences.edit().putString("analyse1", String.valueOf(analyse1.getText())).apply();
                        sharedPreferences.edit().putString("valeur1", String.valueOf(valeur1.getText())).apply();
                        sharedPreferences.edit().putString("unite1", unite1.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse2", String.valueOf(analyse2.getText())).apply();
                        sharedPreferences.edit().putString("valeur2", String.valueOf(valeur2.getText())).apply();
                        sharedPreferences.edit().putString("unite2", unite2.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse3", String.valueOf(analyse3.getText())).apply();
                        sharedPreferences.edit().putString("valeur3", String.valueOf(valeur3.getText())).apply();
                        sharedPreferences.edit().putString("unite3", unite3.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse4", String.valueOf(analyse4.getText())).apply();
                        sharedPreferences.edit().putString("valeur4", String.valueOf(valeur4.getText())).apply();
                        sharedPreferences.edit().putString("unite4", unite4.getSelectedItem().toString()).apply();
                        break;
                    case 6:
                        sharedPreferences.edit().putString("analyse1", String.valueOf(analyse1.getText())).apply();
                        sharedPreferences.edit().putString("valeur1", String.valueOf(valeur1.getText())).apply();
                        sharedPreferences.edit().putString("unite1", unite1.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse2", String.valueOf(analyse2.getText())).apply();
                        sharedPreferences.edit().putString("valeur2", String.valueOf(valeur2.getText())).apply();
                        sharedPreferences.edit().putString("unite2", unite2.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse3", String.valueOf(analyse3.getText())).apply();
                        sharedPreferences.edit().putString("valeur3", String.valueOf(valeur3.getText())).apply();
                        sharedPreferences.edit().putString("unite3", unite3.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse4", String.valueOf(analyse4.getText())).apply();
                        sharedPreferences.edit().putString("valeur4", String.valueOf(valeur4.getText())).apply();
                        sharedPreferences.edit().putString("unite4", unite4.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse5", String.valueOf(analyse5.getText())).apply();
                        sharedPreferences.edit().putString("valeur5", String.valueOf(valeur5.getText())).apply();
                        sharedPreferences.edit().putString("unite5", unite5.getSelectedItem().toString()).apply();
                        break;
                    case 7:
                        sharedPreferences.edit().putString("analyse1", String.valueOf(analyse1.getText())).apply();
                        sharedPreferences.edit().putString("valeur1", String.valueOf(valeur1.getText())).apply();
                        sharedPreferences.edit().putString("unite1", unite1.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse2", String.valueOf(analyse2.getText())).apply();
                        sharedPreferences.edit().putString("valeur2", String.valueOf(valeur2.getText())).apply();
                        sharedPreferences.edit().putString("unite2", unite2.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse3", String.valueOf(analyse3.getText())).apply();
                        sharedPreferences.edit().putString("valeur3", String.valueOf(valeur3.getText())).apply();
                        sharedPreferences.edit().putString("unite3", unite3.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse4", String.valueOf(analyse4.getText())).apply();
                        sharedPreferences.edit().putString("valeur4", String.valueOf(valeur4.getText())).apply();
                        sharedPreferences.edit().putString("unite4", unite4.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse5", String.valueOf(analyse5.getText())).apply();
                        sharedPreferences.edit().putString("valeur5", String.valueOf(valeur5.getText())).apply();
                        sharedPreferences.edit().putString("unite5", unite5.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse6", String.valueOf(analyse6.getText())).apply();
                        sharedPreferences.edit().putString("valeur6", String.valueOf(valeur6.getText())).apply();
                        sharedPreferences.edit().putString("unite6", unite6.getSelectedItem().toString()).apply();
                        break;
                    case 8:
                        sharedPreferences.edit().putString("analyse1", String.valueOf(analyse1.getText())).apply();
                        sharedPreferences.edit().putString("valeur1", String.valueOf(valeur1.getText())).apply();
                        sharedPreferences.edit().putString("unite1", unite1.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse2", String.valueOf(analyse2.getText())).apply();
                        sharedPreferences.edit().putString("valeur2", String.valueOf(valeur2.getText())).apply();
                        sharedPreferences.edit().putString("unite2", unite2.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse3", String.valueOf(analyse3.getText())).apply();
                        sharedPreferences.edit().putString("valeur3", String.valueOf(valeur3.getText())).apply();
                        sharedPreferences.edit().putString("unite3", unite3.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse4", String.valueOf(analyse4.getText())).apply();
                        sharedPreferences.edit().putString("valeur4", String.valueOf(valeur4.getText())).apply();
                        sharedPreferences.edit().putString("unite4", unite4.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse5", String.valueOf(analyse5.getText())).apply();
                        sharedPreferences.edit().putString("valeur5", String.valueOf(valeur5.getText())).apply();
                        sharedPreferences.edit().putString("unite5", unite5.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse6", String.valueOf(analyse6.getText())).apply();
                        sharedPreferences.edit().putString("valeur6", String.valueOf(valeur6.getText())).apply();
                        sharedPreferences.edit().putString("unite6", unite6.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse7", String.valueOf(analyse7.getText())).apply();
                        sharedPreferences.edit().putString("valeur7", String.valueOf(valeur7.getText())).apply();
                        sharedPreferences.edit().putString("unite7", unite7.getSelectedItem().toString()).apply();
                        break;
                    case 9:
                        sharedPreferences.edit().putString("analyse1", String.valueOf(analyse1.getText())).apply();
                        sharedPreferences.edit().putString("valeur1", String.valueOf(valeur1.getText())).apply();
                        sharedPreferences.edit().putString("unite1", unite1.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse2", String.valueOf(analyse2.getText())).apply();
                        sharedPreferences.edit().putString("valeur2", String.valueOf(valeur2.getText())).apply();
                        sharedPreferences.edit().putString("unite2", unite2.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse3", String.valueOf(analyse3.getText())).apply();
                        sharedPreferences.edit().putString("valeur3", String.valueOf(valeur3.getText())).apply();
                        sharedPreferences.edit().putString("unite3", unite3.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse4", String.valueOf(analyse4.getText())).apply();
                        sharedPreferences.edit().putString("valeur4", String.valueOf(valeur4.getText())).apply();
                        sharedPreferences.edit().putString("unite4", unite4.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse5", String.valueOf(analyse5.getText())).apply();
                        sharedPreferences.edit().putString("valeur5", String.valueOf(valeur5.getText())).apply();
                        sharedPreferences.edit().putString("unite5", unite5.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse6", String.valueOf(analyse6.getText())).apply();
                        sharedPreferences.edit().putString("valeur6", String.valueOf(valeur6.getText())).apply();
                        sharedPreferences.edit().putString("unite6", unite6.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse7", String.valueOf(analyse7.getText())).apply();
                        sharedPreferences.edit().putString("valeur7", String.valueOf(valeur7.getText())).apply();
                        sharedPreferences.edit().putString("unite7", unite7.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse8", String.valueOf(analyse8.getText())).apply();
                        sharedPreferences.edit().putString("valeur8", String.valueOf(valeur8.getText())).apply();
                        sharedPreferences.edit().putString("unite8", unite8.getSelectedItem().toString()).apply();
                        break;
                    case 10:
                        sharedPreferences.edit().putString("analyse1", String.valueOf(analyse1.getText())).apply();
                        sharedPreferences.edit().putString("valeur1", String.valueOf(valeur1.getText())).apply();
                        sharedPreferences.edit().putString("unite1", unite1.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse2", String.valueOf(analyse2.getText())).apply();
                        sharedPreferences.edit().putString("valeur2", String.valueOf(valeur2.getText())).apply();
                        sharedPreferences.edit().putString("unite2", unite2.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse3", String.valueOf(analyse3.getText())).apply();
                        sharedPreferences.edit().putString("valeur3", String.valueOf(valeur3.getText())).apply();
                        sharedPreferences.edit().putString("unite3", unite3.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse4", String.valueOf(analyse4.getText())).apply();
                        sharedPreferences.edit().putString("valeur4", String.valueOf(valeur4.getText())).apply();
                        sharedPreferences.edit().putString("unite4", unite4.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse5", String.valueOf(analyse5.getText())).apply();
                        sharedPreferences.edit().putString("valeur5", String.valueOf(valeur5.getText())).apply();
                        sharedPreferences.edit().putString("unite5", unite5.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse6", String.valueOf(analyse6.getText())).apply();
                        sharedPreferences.edit().putString("valeur6", String.valueOf(valeur6.getText())).apply();
                        sharedPreferences.edit().putString("unite6", unite6.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse7", String.valueOf(analyse7.getText())).apply();
                        sharedPreferences.edit().putString("valeur7", String.valueOf(valeur7.getText())).apply();
                        sharedPreferences.edit().putString("unite7", unite7.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse8", String.valueOf(analyse8.getText())).apply();
                        sharedPreferences.edit().putString("valeur8", String.valueOf(valeur8.getText())).apply();
                        sharedPreferences.edit().putString("unite8", unite8.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse9", String.valueOf(analyse9.getText())).apply();
                        sharedPreferences.edit().putString("valeur9", String.valueOf(valeur9.getText())).apply();
                        sharedPreferences.edit().putString("unite9", unite9.getSelectedItem().toString()).apply();
                        break;
                    case 11:
                        sharedPreferences.edit().putString("analyse1", String.valueOf(analyse1.getText())).apply();
                        sharedPreferences.edit().putString("valeur1", String.valueOf(valeur1.getText())).apply();
                        sharedPreferences.edit().putString("unite1", unite1.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse2", String.valueOf(analyse2.getText())).apply();
                        sharedPreferences.edit().putString("valeur2", String.valueOf(valeur2.getText())).apply();
                        sharedPreferences.edit().putString("unite2", unite2.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse3", String.valueOf(analyse3.getText())).apply();
                        sharedPreferences.edit().putString("valeur3", String.valueOf(valeur3.getText())).apply();
                        sharedPreferences.edit().putString("unite3", unite3.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse4", String.valueOf(analyse4.getText())).apply();
                        sharedPreferences.edit().putString("valeur4", String.valueOf(valeur4.getText())).apply();
                        sharedPreferences.edit().putString("unite4", unite4.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse5", String.valueOf(analyse5.getText())).apply();
                        sharedPreferences.edit().putString("valeur5", String.valueOf(valeur5.getText())).apply();
                        sharedPreferences.edit().putString("unite5", unite5.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse6", String.valueOf(analyse6.getText())).apply();
                        sharedPreferences.edit().putString("valeur6", String.valueOf(valeur6.getText())).apply();
                        sharedPreferences.edit().putString("unite6", unite6.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse7", String.valueOf(analyse7.getText())).apply();
                        sharedPreferences.edit().putString("valeur7", String.valueOf(valeur7.getText())).apply();
                        sharedPreferences.edit().putString("unite7", unite7.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse8", String.valueOf(analyse8.getText())).apply();
                        sharedPreferences.edit().putString("valeur8", String.valueOf(valeur8.getText())).apply();
                        sharedPreferences.edit().putString("unite8", unite8.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse9", String.valueOf(analyse9.getText())).apply();
                        sharedPreferences.edit().putString("valeur9", String.valueOf(valeur9.getText())).apply();
                        sharedPreferences.edit().putString("unite9", unite9.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse10", String.valueOf(analyse10.getText())).apply();
                        sharedPreferences.edit().putString("valeur10", String.valueOf(valeur10.getText())).apply();
                        sharedPreferences.edit().putString("unite10", unite10.getSelectedItem().toString()).apply();
                        break;
                    case 12:
                        sharedPreferences.edit().putString("analyse1", String.valueOf(analyse1.getText())).apply();
                        sharedPreferences.edit().putString("valeur1", String.valueOf(valeur1.getText())).apply();
                        sharedPreferences.edit().putString("unite1", unite1.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse2", String.valueOf(analyse2.getText())).apply();
                        sharedPreferences.edit().putString("valeur2", String.valueOf(valeur2.getText())).apply();
                        sharedPreferences.edit().putString("unite2", unite2.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse3", String.valueOf(analyse3.getText())).apply();
                        sharedPreferences.edit().putString("valeur3", String.valueOf(valeur3.getText())).apply();
                        sharedPreferences.edit().putString("unite3", unite3.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse4", String.valueOf(analyse4.getText())).apply();
                        sharedPreferences.edit().putString("valeur4", String.valueOf(valeur4.getText())).apply();
                        sharedPreferences.edit().putString("unite4", unite4.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse5", String.valueOf(analyse5.getText())).apply();
                        sharedPreferences.edit().putString("valeur5", String.valueOf(valeur5.getText())).apply();
                        sharedPreferences.edit().putString("unite5", unite5.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse6", String.valueOf(analyse6.getText())).apply();
                        sharedPreferences.edit().putString("valeur6", String.valueOf(valeur6.getText())).apply();
                        sharedPreferences.edit().putString("unite6", unite6.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse7", String.valueOf(analyse7.getText())).apply();
                        sharedPreferences.edit().putString("valeur7", String.valueOf(valeur7.getText())).apply();
                        sharedPreferences.edit().putString("unite7", unite7.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse8", String.valueOf(analyse8.getText())).apply();
                        sharedPreferences.edit().putString("valeur8", String.valueOf(valeur8.getText())).apply();
                        sharedPreferences.edit().putString("unite8", unite8.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse9", String.valueOf(analyse9.getText())).apply();
                        sharedPreferences.edit().putString("valeur9", String.valueOf(valeur9.getText())).apply();
                        sharedPreferences.edit().putString("unite9", unite9.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse10", String.valueOf(analyse10.getText())).apply();
                        sharedPreferences.edit().putString("valeur10", String.valueOf(valeur10.getText())).apply();
                        sharedPreferences.edit().putString("unite10", unite10.getSelectedItem().toString()).apply();
                        sharedPreferences.edit().putString("analyse11", String.valueOf(analyse11.getText())).apply();
                        sharedPreferences.edit().putString("valeur11", String.valueOf(valeur11.getText())).apply();
                        sharedPreferences.edit().putString("unite11", unite11.getSelectedItem().toString()).apply();
                        break;
                }
            }
        });

        //Uploading pdf file

        searchFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }
        });

        recordAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    audioExists = true;
                    startRecording();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopRecording();
                }
                return false;
            }
        });

        addCommentaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogue();
            }
        });

        return view;
    }

    private void setSpinnerContent (final AutoCompleteTextView analyse, final Spinner unite, final Context context) {

        analyse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                fStore.collection("Analyses")
                        .document(analyse.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    ArrayList<String> units;
                                    units = (ArrayList<String>) document.get("Unite");
                                    ArrayAdapter<String> arrayAdaptereUnit = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, units);
                                    arrayAdaptereUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    unite.setAdapter(arrayAdaptereUnit);
                                }
                            }
                        });
            }

        });
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void selectPDF () {
        Intent pdfIntent = new Intent();
        pdfIntent.setType("application/pdf");
        pdfIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pdfIntent, 86);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectPDF();
        } else {
            Toast.makeText(getView().getContext(), "Veuiller donner la permission pour lancer le PDF", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 86 && resultCode==RESULT_OK && data!=null) {
            pdfExists = true;
            pdfUri = data.getData();
        } else {
            Toast.makeText(getContext(), "Veuiller selection une PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;

        uploadAudio();
    }

    private void uploadAudio() {
        StorageReference storageReference = storage.getReference();
        StorageReference audioReference = storageReference.child("Audio").child("new_audio.mp3");
        Uri uri = Uri.fromFile(new File(fileName));
        audioReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "finished uploading", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDialogue() {
        CommentaireDialogue commentaireDialogue = new CommentaireDialogue();
        commentaireDialogue.show(getFragmentManager(), "example dialog");
    }
}
