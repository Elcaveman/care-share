package firebase;

import androidx.annotation.NonNull;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.medicalappv1.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.LazyStringArrayList;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class FirebaseUtils {

    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    public void addAnalyse (final String analyseNom, final String unite, final String value, String userID) {
        final DocumentReference documentReference = fStore.collection("utilisateurs").document(userID).collection("Analyses").document(analyseNom);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        documentReference.update("Valeurs", FieldValue.arrayUnion(Float.parseFloat(value)));
                    } else {
                        Map<String, Object> analyse = new HashMap<>();
                        ArrayList<Float> values= new ArrayList<>();
                        values.add(Float.parseFloat(value));
                        analyse.put("Nom", analyseNom);
                        analyse.put("Unite", unite);
                        analyse.put("Valeurs", values);
                        documentReference.set(analyse);
                    }
                }
            }
        });
    }

    public ArrayList<String> getAnalyseNames(String userID) {
        final ArrayList<String> mNames = new ArrayList<>();
        fStore.collection("utilisateurs").document(userID).collection("Analyses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mNames.add(document.getId());
                            }
                        }
                    }
                });
        return mNames;
    }
}
