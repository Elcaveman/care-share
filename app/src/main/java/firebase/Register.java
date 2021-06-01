package firebase;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.medicalappv1.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register {

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    public void registerEmailPassword (final String email, final String password, final String nom, final String prenom, final String numero, final Context context) {
        fAuth.createUserWithEmailAndPassword(email,password) .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Compte créé !", Toast.LENGTH_SHORT).show();
                    String userID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("utilisateurs").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("Email", email);
                    user.put("Password", password);
                    user.put("Nom", nom);
                    user.put("Prenom", prenom);
                    user.put("Numero", numero);
                    documentReference.set(user);
                    context.startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(context, "Erreur : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
