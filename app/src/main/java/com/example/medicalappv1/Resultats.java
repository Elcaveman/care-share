package com.example.medicalappv1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.internal.HashAccumulator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import Adapters.NotificationRecyclerAdapter;

public class Resultats extends Fragment implements NotificationRecyclerAdapter.OnNoteListener {

    ArrayList<String> titleList = new ArrayList<>();
    ArrayList<String> descriptionList = new ArrayList<>();
    ArrayList<String> datesList = new ArrayList<>();
    ArrayList<Integer> imagesList = new ArrayList<>();
    ArrayList<String> prelevementID = new ArrayList<>();

    RecyclerView recyclerView;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String patientID, codePatient, nomPatient;

    HashMap<String, String> idDate;

    NotificationRecyclerAdapter.OnNoteListener onNoteListener = this;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.resultats, container, false);

        fStore.collection("Patients")
                .whereEqualTo("Email", firebaseAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                patientID = document.getId();
                                codePatient = document.getString("Code");
                                nomPatient = document.getString("Nom") + document.getString("Prenom");
                            }
                            fStore.collection("Patients")
                                    .document(patientID)
                                    .collection("Prelevement")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                idDate = new HashMap<>();
                                                for(QueryDocumentSnapshot document : task.getResult()) {
                                                    titleList.add("Prélévement disponible");
                                                    descriptionList.add("Les résultats sont prêts");
                                                    datesList.add(document.getString("Date"));
                                                    imagesList.add(R.drawable.analyses_logo);
                                                    prelevementID.add(document.getId());

                                                    idDate.put(document.getString("Date"), document.getId());
                                                }
                                                //sorting les dates
                                                Collections.sort(datesList, new Comparator<String>() {
                                                    DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                                                    @Override
                                                    public int compare(String o1, String o2) {
                                                        try {
                                                            return f.parse(o1).compareTo(f.parse(o2));
                                                        } catch (ParseException e) {
                                                            throw new IllegalArgumentException(e);
                                                        }
                                                    }
                                                });
                                                Collections.reverse(datesList);

                                                String[] titleArray = new String[titleList.size()];
                                                titleArray = titleList.toArray(titleArray);
                                                String[] descriptionArray = new String[descriptionList.size()];
                                                descriptionArray = descriptionList.toArray(descriptionArray);
                                                String[] dateArray = new String[datesList.size()];
                                                dateArray = datesList.toArray(dateArray);
                                                Integer[] imagesArray = new Integer[imagesList.size()];
                                                imagesArray = imagesList.toArray(imagesArray);

                                                recyclerView = view.findViewById(R.id.recyclerView);
                                                NotificationRecyclerAdapter notificationRecyclerAdapter = new NotificationRecyclerAdapter(view.getContext(), titleArray, descriptionArray, dateArray, imagesArray, onNoteListener);

                                                recyclerView.setAdapter(notificationRecyclerAdapter);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

                                            }
                                        }
                                    });
                        }
                    }
                });
        return view;
    }

    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(getContext(), ResultatPrelevement.class);

        String date = datesList.get(position);
        String matchingID = idDate.get(date);

        intent.putExtra("date", datesList.get(position));
        intent.putExtra("PrelevementID", matchingID);
        intent.putExtra("Nom", nomPatient);
        intent.putExtra("Code", codePatient);
        intent.putExtra("patientID", patientID);
        startActivity(intent);
    }
}
