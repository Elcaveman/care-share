package firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DatabaseChecker {

    Boolean patientExists = false;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    public Boolean patientExists(final String code, final String cin) {
        fStore.collection("Patients")
                .whereEqualTo("Code", code)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            patientExists = false;
                        } else {
                            patientExists = true;
                        }
                    }
                });
        return patientExists;
    }
}
