package firebase;

import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import firebase.*;

public class CreateUser {

    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public void addPatient (String codePatient, String nomPatient,String prenomPatient, String cinPatient,String adressePatient, Float numeroPatient, String emailPatient, String mdpPatient, String dateNaissance) {
        Map<String, Object> patientData = new HashMap<>();
        patientData.put("Code", codePatient);
        patientData.put("Nom", nomPatient);
        patientData.put("Prenom", prenomPatient);
        patientData.put("CIN", cinPatient);
        patientData.put("Adresse", adressePatient);
        patientData.put("Telephone", numeroPatient);
        patientData.put("Email", emailPatient);
        patientData.put("Mdp", mdpPatient);
        patientData.put("DateNaissance", dateNaissance);
        fStore.collection("Patients").add(patientData);
    }

    public void addPatientInLabo (final String codePatient, final String numeroDossier) {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        final String formattedDate = df.format(c);

        fStore.collection("Patients")
                .whereEqualTo("Code", codePatient)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final String temp = document.getId();
                                String emailLabo = firebaseAuth.getCurrentUser().getEmail();
                                fStore.collection("Labos")
                                        .whereEqualTo("Email", emailLabo)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Map<String, Object> patientData = new HashMap<>();
                                                        patientData.put("DateInscription", formattedDate);
                                                        patientData.put("NumeroDossier", numeroDossier);
                                                        patientData.put("ID_Patient", temp);
                                                        patientData.put("Code", codePatient);
                                                        String laboID = document.getId();
                                                        fStore.collection("Labos")
                                                                .document(laboID)
                                                                .collection("Patients")
                                                                .add(patientData);
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
