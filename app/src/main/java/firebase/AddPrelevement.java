package firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import firebase.*;

public class AddPrelevement {

    String patientID;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    firebase.DatabaseGetters databaseGetters = new DatabaseGetters();
    ArrayList<String> nomAnalysesDejaFaites = new ArrayList<>();


    /*public void addAnalyse (String codePatient, final String nomAnalyse, final Float value) {
        nomAnalysesDejaFaites = databaseGetters.getAnalysesPatient(codePatient);
        fStore.collection("Patients")
                .whereEqualTo("Code", codePatient)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                patientID = document.getId();
                                DocumentReference documentReference = fStore.collection("Patients").document(patientID).collection("Analyses").document(nomAnalyse);
                                if (nomAnalysesDejaFaites.contains(nomAnalyse)) {
                                    documentReference.update("Valeur", FieldValue.arrayUnion(value));
                                }
                                else {
                                    Map<String, Object> analyseData = new HashMap<>();
                                    ArrayList<Float> values = new ArrayList<>();
                                    values.add(value);
                                    analyseData.put("Nom", nomAnalyse);
                                    analyseData.put("Valeur", values);
                                    documentReference.set(analyseData);
                                }
                            }
                        }
                    }
                });
    }*/


}
