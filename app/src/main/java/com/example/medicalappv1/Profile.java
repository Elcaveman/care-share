package com.example.medicalappv1;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Profile extends Fragment {

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String patientId, patientName, codePatientStr;
    TextView patientNameTV, patientEmailTV, phonePatient, adressePatient, codePatientTV, cinTV;
    ImageView profileImage;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    StorageReference profilePicRef;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.profile, container, false);

        profilePicRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");

        patientNameTV = view.findViewById(R.id.tv_name);
        patientEmailTV = view.findViewById(R.id.tv_address);
        phonePatient = view.findViewById(R.id.phonePatient);
        adressePatient = view.findViewById(R.id.adressePatient);
        codePatientTV = view.findViewById(R.id.codePatient);
        cinTV = view.findViewById(R.id.cin);
        profileImage = view.findViewById(R.id.profileImage);

        fStore.collection("Patients")
                .whereEqualTo("Email", fAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                patientId = document.getId();
                            }
                        }
                        fStore.collection("Patients")
                                .document(patientId)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            patientName = documentSnapshot.getString("Nom") + " " + documentSnapshot.getString("Prenom");
                                            codePatientStr = "Code médicale : " + documentSnapshot.getString("Code");
                                            patientEmailTV.setText(documentSnapshot.getString("Email"));
                                            patientNameTV.setText(patientName);
                                            phonePatient.setText(documentSnapshot.getString("Telephone"));
                                            adressePatient.setText(documentSnapshot.getString("Adresse"));
                                            codePatientTV.setText(codePatientStr);
                                            cinTV.setText(documentSnapshot.getString("CIN"));
                                        }
                                    }
                                });
                    }
                });

        profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 1000);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                uploadImagetoFirebase(imageUri);
            }
        }
    }

    private void uploadImagetoFirebase(Uri imageUri) {
        profilePicRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profilePicRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });
            }
        });
    }
}
