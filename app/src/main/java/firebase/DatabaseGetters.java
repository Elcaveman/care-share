package firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DatabaseGetters {

    public ArrayList<String> codesPatients = new ArrayList<>();
    public ArrayList<String> nomAnalyses = new ArrayList<>();
    public ArrayList<String> analysesPatient = new ArrayList<>();
    public ArrayList<String> nomsMedecins = new ArrayList<>();
    public ArrayList<String> numeroDossiers = new ArrayList<>();
    public String patientID;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    public ArrayList<String> getNomsMedecins() {
        fStore.collection("Medecins")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("Nom") + " " + document.getString("Prenom");
                                nomsMedecins.add(name);
                            }
                        }
                    }
                });
        return nomsMedecins;
    }

    public ArrayList<String> getCodesPatient() {
        String emailLabo = firebaseAuth.getCurrentUser().getEmail();
        fStore.collection("Labos")
                .whereEqualTo("Email", emailLabo)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String laboID = document.getId();
                                fStore.collection("Labos").document(laboID)
                                        .collection("Patients")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        codesPatients.add(document.getString("Code"));
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });

        return codesPatients;
    }

    public ArrayList<String> getNomAnalyses () {
        fStore.collection("Analyses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                nomAnalyses.add(document.getString("Nom"));
                            }
                        }
                    }
                });
        return nomAnalyses;
    }

    public ArrayList<String> getAnalysesPatient (String codePatient) {
        fStore.collection("Patients")
                .whereEqualTo("Code", codePatient)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                patientID = document.getId();
                                fStore.collection("Patients").document(patientID).collection("Analyses")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        analysesPatient.add(document.getString("Nom"));
                                                        Log.i("analyse patient", analysesPatient.get(0));
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
        return analysesPatient;
    }

    public ArrayList<String> getNumeroDossiers () {
        String emailLabo = firebaseAuth.getCurrentUser().getEmail();
        fStore.collection("Labos")
                .whereEqualTo("Email", emailLabo)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String laboID = document.getId();
                                fStore.collection("Labos").document(laboID)
                                        .collection("Patients")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        numeroDossiers.add(document.getString("NumeroDossier"));
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
        return numeroDossiers;
    }
}
