package com.example.medicalappv1;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import retrofit2.http.Url;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ResultatPrelevement extends AppCompatActivity {

    TextView nomLaboTV, numeroDossierTV, codePatientTV, nomPatientTV, nomMedecinTV, datePrelevementTV;
    String prelevementID, patientID;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    ArrayList<String> analyses;
    TextView analyse1Cat1, analyse2Cat1, analyse1Cat2, analyse2Cat2, analyse1Cat3, analyse2Cat3;
    TextView valeur1Cat1, valeur2Cat1, valeur1Cat2, valeur2Cat2, valeur1Cat3, valeur2Cat3;
    TextView unite1Cat1, unite2Cat1, unite1Cat2, unite2Cat2, unite1Cat3, unite2Cat3;
    TextView categorie1, categorie2, categorie3;
    LinearLayout line1Cat1, line2Cat1, line1Cat2, line2Cat2, line1Cat3, line2Cat3;
    ImageView line1Cat1Eye, line2Cat1Eye, line1Cat2Eye, line2Cat2Eye, line1Cat3Eye, line2Cat3Eye;

    int counterCat1 = 1;
    int counterCat2 = 1;
    int counterCat3 = 1;

    String normeMin, normeMax;
    boolean dangerous;

    ImageView downloadRapport;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    LinearLayout downloadLL;

    ImageView playAudio;
    LinearLayout audioLL;

    LinearLayout interpretationLL;
    TextView interpretationTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultat_prelevement);

        nomLaboTV = findViewById(R.id.nomLabo);
        numeroDossierTV = findViewById(R.id.numeroDossier);
        codePatientTV = findViewById(R.id.codePatient);
        nomPatientTV = findViewById(R.id.nomPatient);
        nomMedecinTV = findViewById(R.id.nomMedecin);
        datePrelevementTV = findViewById(R.id.datePrelevement);

        categorie1 = findViewById(R.id.categorie1); categorie2 = findViewById(R.id.categorie2); categorie3 = findViewById(R.id.categorie3);

        analyse1Cat1 = findViewById(R.id.analyse1Cat1); analyse2Cat1 = findViewById(R.id.analyse2Cat1);
        analyse1Cat2 = findViewById(R.id.analyse1Cat2); analyse2Cat2 = findViewById(R.id.analyse2Cat2);
        analyse1Cat3 = findViewById(R.id.analyse1Cat3); analyse2Cat3 = findViewById(R.id.analyse2Cat3);

        valeur1Cat1 = findViewById(R.id.valeur1Cat1); valeur2Cat1 = findViewById(R.id.valeur2Cat1);
        valeur1Cat2 = findViewById(R.id.valeur1Cat2); valeur2Cat2 = findViewById(R.id.valeur2Cat2);
        valeur1Cat3 = findViewById(R.id.valeur1Cat3); valeur2Cat3 = findViewById(R.id.valeur2Cat3);

        unite1Cat1 = findViewById(R.id.unite1Cat1); unite2Cat1 = findViewById(R.id.unite2Cat1);
        unite1Cat2 = findViewById(R.id.unite1Cat2); unite2Cat2 = findViewById(R.id.unite2Cat2);
        unite1Cat3 = findViewById(R.id.unite1Cat3); unite2Cat3 = findViewById(R.id.unite2Cat3);

        line1Cat1 = findViewById(R.id.line1Cat1); line2Cat1 = findViewById(R.id.line2Cat1);
        line1Cat2 = findViewById(R.id.line1Cat2); line2Cat2 = findViewById(R.id.line2Cat2);
        line1Cat3 = findViewById(R.id.line1Cat3); line2Cat3 = findViewById(R.id.line2Cat3);

        line1Cat1Eye = findViewById(R.id.line1Cat1Eye); line2Cat1Eye = findViewById(R.id.line2Cat1Eye);
        line1Cat2Eye = findViewById(R.id.line1Cat2Eye); line2Cat2Eye = findViewById(R.id.line2Cat2Eye);
        line1Cat3Eye = findViewById(R.id.line1Cat3Eye); line2Cat3Eye = findViewById(R.id.line2Cat3Eye);

        downloadLL = findViewById(R.id.downloadLL);
        downloadRapport = findViewById(R.id.downloadRapport);

        playAudio = findViewById(R.id.playAudio);
        audioLL = findViewById(R.id.audioLL);

        interpretationLL = findViewById(R.id.interpretationLL);
        interpretationTV = findViewById(R.id.interpretationTV);

        final Intent intent = getIntent();
        datePrelevementTV.setText(intent.getStringExtra("date"));
        prelevementID = intent.getStringExtra("PrelevementID");
        patientID = intent.getStringExtra("patientID");

        codePatientTV.setText(intent.getStringExtra("Code"));
        nomPatientTV.setText(intent.getStringExtra("Nom"));

        fStore.collection("Patients")
                .document(patientID)
                .collection("Prelevement")
                .document(prelevementID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            final DocumentSnapshot document = task.getResult();

                            if(document.getBoolean("HasRapport"))
                                downloadLL.setVisibility(View.VISIBLE);
                            if(document.getBoolean("HasAudio"))
                                audioLL.setVisibility(View.VISIBLE);

                            if(!(document.getString("Interpretation") == null)) {
                                interpretationLL.setVisibility(View.VISIBLE);
                                interpretationTV.setText(document.getString("Interpretation"));
                            }
                            //Setting up les resultats
                            Map<String, Object> allData = document.getData();

                            allData.remove("Date");
                            allData.remove("ID_Labo");
                            allData.remove("ID_Medecin");
                            allData.remove("Seen");
                            allData.remove("HasRapport");
                            allData.remove("HasAudio");
                            allData.remove("Interpretation");

                            for (String key: allData.keySet()) {
                                analyses = (ArrayList<String>) allData.get(key);
                                final String nomAnalyse = analyses.get(0);
                                final String valeurAnalyse = analyses.get(1);
                                final String uniteAnalyse = analyses.get(2);

                                fStore.collection("Analyses")
                                        .document(nomAnalyse)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        normeMin = document.getString("NormeMin");
                                                        normeMax = document.getString("NormeMax");
                                                        if (Float.parseFloat(valeurAnalyse) > Float.parseFloat(normeMax) || Float.parseFloat(valeurAnalyse) < Float.parseFloat(normeMin))
                                                            dangerous = true;
                                                        else
                                                            dangerous = false;
                                                        String categorie = document.getString("Categorie");
                                                        if (categorie.equals("Categorie1")) {
                                                            categorie1.setVisibility(View.VISIBLE);
                                                            switch (counterCat1) {
                                                                case 1 :
                                                                    line1Cat1.setVisibility(View.VISIBLE);
                                                                    analyse1Cat1.setText(nomAnalyse);
                                                                    valeur1Cat1.setText(valeurAnalyse);
                                                                    unite1Cat1.setText(uniteAnalyse);
                                                                    counterCat1++;
                                                                    if (dangerous)
                                                                        line1Cat1Eye.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark), PorterDuff.Mode.MULTIPLY);
                                                                    else
                                                                        line1Cat1Eye.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_green_dark), PorterDuff.Mode.MULTIPLY);
                                                                    break;
                                                                case 2 :
                                                                    line2Cat1.setVisibility(View.VISIBLE);
                                                                    analyse2Cat1.setVisibility(View.VISIBLE);
                                                                    valeur2Cat1.setVisibility(View.VISIBLE);
                                                                    unite2Cat1.setVisibility(View.VISIBLE);
                                                                    analyse2Cat1.setText(nomAnalyse);
                                                                    valeur2Cat1.setText(valeurAnalyse);
                                                                    unite2Cat1.setText(uniteAnalyse);
                                                                    counterCat1++;
                                                                    if (dangerous)
                                                                        line2Cat1Eye.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark), PorterDuff.Mode.MULTIPLY);
                                                                    else
                                                                        line2Cat1Eye.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_green_dark), PorterDuff.Mode.MULTIPLY);
                                                                    break;
                                                            }
                                                        }
                                                        if (categorie.equals("Categorie2")) {
                                                            categorie2.setVisibility(View.VISIBLE);
                                                            switch (counterCat2) {
                                                                case 1 :
                                                                    line1Cat2.setVisibility(View.VISIBLE);
                                                                    analyse1Cat2.setVisibility(View.VISIBLE);
                                                                    valeur1Cat2.setVisibility(View.VISIBLE);
                                                                    unite1Cat2.setVisibility(View.VISIBLE);
                                                                    analyse1Cat2.setText(nomAnalyse);
                                                                    valeur1Cat2.setText(valeurAnalyse);
                                                                    unite1Cat2.setText(uniteAnalyse);
                                                                    counterCat2++;
                                                                    if (dangerous)
                                                                        line1Cat2Eye.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark), PorterDuff.Mode.MULTIPLY);
                                                                    else
                                                                        line1Cat2Eye.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_green_dark), PorterDuff.Mode.MULTIPLY);
                                                                    break;
                                                                case 2 :
                                                                    line2Cat2.setVisibility(View.VISIBLE);
                                                                    analyse2Cat2.setVisibility(View.VISIBLE);
                                                                    valeur2Cat2.setVisibility(View.VISIBLE);
                                                                    unite2Cat2.setVisibility(View.VISIBLE);
                                                                    analyse2Cat2.setText(nomAnalyse);
                                                                    valeur2Cat2.setText(valeurAnalyse);
                                                                    unite2Cat2.setText(uniteAnalyse);
                                                                    counterCat2++;
                                                                    if (dangerous)
                                                                        line2Cat2Eye.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark), PorterDuff.Mode.MULTIPLY);
                                                                    else
                                                                        line2Cat2Eye.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_green_dark), PorterDuff.Mode.MULTIPLY);
                                                                    break;
                                                            }
                                                        }
                                                        if (categorie.equals("Categorie3")) {
                                                            categorie3.setVisibility(View.VISIBLE);
                                                            switch (counterCat1) {
                                                                case 1 :
                                                                    line1Cat3.setVisibility(View.VISIBLE);
                                                                    analyse1Cat3.setVisibility(View.VISIBLE);
                                                                    valeur1Cat3.setVisibility(View.VISIBLE);
                                                                    unite1Cat3.setVisibility(View.VISIBLE);
                                                                    analyse1Cat3.setText(nomAnalyse);
                                                                    valeur1Cat3.setText(valeurAnalyse);
                                                                    unite1Cat3.setText(uniteAnalyse);
                                                                    counterCat3++;
                                                                    if (dangerous)
                                                                        line1Cat3Eye.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark), PorterDuff.Mode.MULTIPLY);
                                                                    else
                                                                        line1Cat3Eye.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_green_dark), PorterDuff.Mode.MULTIPLY);
                                                                    break;
                                                                case 2 :
                                                                    line2Cat3.setVisibility(View.VISIBLE);
                                                                    analyse2Cat3.setVisibility(View.VISIBLE);
                                                                    valeur2Cat3.setVisibility(View.VISIBLE);
                                                                    unite2Cat3.setVisibility(View.VISIBLE);
                                                                    analyse2Cat3.setText(nomAnalyse);
                                                                    valeur2Cat3.setText(valeurAnalyse);
                                                                    unite2Cat3.setText(uniteAnalyse);
                                                                    counterCat3++;
                                                                    if (dangerous)
                                                                        line2Cat3Eye.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark), PorterDuff.Mode.MULTIPLY);
                                                                    else
                                                                        line2Cat3Eye.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_green_dark), PorterDuff.Mode.MULTIPLY);
                                                                    break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            final String laboID = document.getString("ID_Labo");
                            String medecinID = document.getString("ID_Medecin");

                            fStore.collection("Labos")
                                    .document(laboID)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                nomLaboTV.setText(task.getResult().getString("Nom"));
                                            }
                                        }
                                    });
                            fStore.collection("Labos")
                                    .document(laboID)
                                    .collection("Patients")
                                    .whereEqualTo("Code", intent.getStringExtra("Code"))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    numeroDossierTV.setText(document.getString("NumeroDossier"));
                                                }
                                            }
                                        }
                                    });
                            fStore.collection("Medecins")
                                    .document(medecinID)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                String temp = task.getResult().getString("Nom") + " " + task.getResult().getString("Prenom");
                                                nomMedecinTV.setText(temp);
                                            }
                                        }
                                    });
                        }
                    }
                });

        line1Cat1Eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AnalyseChart.class);
                intent.putExtra("patientID", patientID);
                intent.putExtra("nomAnalyse", analyse1Cat1.getText());
                intent.putExtra("DatePrelevement", datePrelevementTV.getText().toString());
                intent.putExtra("PrelevementID", prelevementID);
                startActivity(intent);
            }
        });
        line2Cat1Eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AnalyseChart.class);
                intent.putExtra("patientID", patientID);
                intent.putExtra("nomAnalyse", analyse2Cat1.getText());
                intent.putExtra("DatePrelevement", datePrelevementTV.getText().toString());
                intent.putExtra("PrelevementID", prelevementID);
                startActivity(intent);
            }
        });
        line1Cat2Eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AnalyseChart.class);
                intent.putExtra("patientID", patientID);
                intent.putExtra("nomAnalyse", analyse1Cat2.getText());
                intent.putExtra("DatePrelevement", datePrelevementTV.getText().toString());
                intent.putExtra("PrelevementID", prelevementID);
                startActivity(intent);
            }
        });
        line2Cat2Eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AnalyseChart.class);
                intent.putExtra("patientID", patientID);
                intent.putExtra("nomAnalyse", analyse2Cat2.getText());
                intent.putExtra("DatePrelevement", datePrelevementTV.getText().toString());
                intent.putExtra("PrelevementID", prelevementID);
                startActivity(intent);
            }
        });
        line1Cat3Eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AnalyseChart.class);
                intent.putExtra("patientID", patientID);
                intent.putExtra("nomAnalyse", analyse1Cat3.getText());
                intent.putExtra("DatePrelevement", datePrelevementTV.getText().toString());
                intent.putExtra("PrelevementID", prelevementID);
                startActivity(intent);
            }
        });
        line2Cat3Eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AnalyseChart.class);
                intent.putExtra("patientID", patientID);
                intent.putExtra("nomAnalyse", analyse2Cat3.getText());
                intent.putExtra("DatePrelevement", datePrelevementTV.getText().toString());
                intent.putExtra("PrelevementID", prelevementID);
                startActivity(intent);
            }
        });
        downloadRapport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference rapportRef = storageReference.child("Rapports").child(prelevementID).child("Rapport");
                Log.i("ref", String.valueOf(rapportRef));

                rapportRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(uri);

                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalFilesDir(getApplicationContext(), DIRECTORY_DOWNLOADS, "Rapport" + "pdf");
                        downloadManager.enqueue(request);
                    }
                });
            }
        });
        playAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference audioRef = storageReference.child("Audio").child(prelevementID).child("new_audio.mp3");
                audioRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i("uri", String.valueOf(uri));
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(String.valueOf(uri));
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.start();
                                }
                            });
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
