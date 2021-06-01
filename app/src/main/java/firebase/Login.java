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

public class Login {

    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    public void signInEmailPassword (String email, String password, final Context context) {
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Vous êtes maintenant connectés !", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(context, "Erreur : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
