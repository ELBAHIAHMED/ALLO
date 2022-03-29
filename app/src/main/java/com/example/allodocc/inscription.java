package com.example.allodocc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allodoc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class inscription extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText nomComplet , email , motDePasse ,confirmMotDePasse;
    Button enregistrer ;
    TextView compte ;
    FirebaseAuth firebaseAuth;
    ProgressBar enCours ;
    FirebaseFirestore fStore ;
    String userID ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        nomComplet= findViewById(R.id.nomComplet);
        email = findViewById(R.id.email);
        motDePasse = findViewById(R.id.motDePasse);
        confirmMotDePasse = findViewById(R.id.confirmMotDePasse);
        enregistrer = findViewById(R.id.enregistrer);
        enCours = findViewById(R.id.enCours);
        compte = findViewById(R.id.compte);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        enCours = findViewById(R.id.enCours);
        //utilisateur déja connecté ?
        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),ChoixConsultation.class));
            finish();
        }

        enregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //trim supprime les whitespaces
                String Nom =nomComplet.getText().toString();
                String Email = email.getText().toString().trim();
                String MotDePasse = motDePasse.getText().toString().trim();
                String ConfirmMotDePasse = confirmMotDePasse.getText().toString().trim();
                if (TextUtils.isEmpty(Nom)){
                    nomComplet.setError("merci d'entrer votre nom complet");
                    return ;
                }
                if (TextUtils.isEmpty(Email)){
                    email.setError("merci d'entrer votre email");
                    return ;
                }
                if (TextUtils.isEmpty(MotDePasse)){
                    motDePasse.setError("merci d'entrer votre mot de passe");
                    return ;
                }
                if (TextUtils.isEmpty(ConfirmMotDePasse)){
                    confirmMotDePasse.setError("merci de resaisir votre mot de passe");
                    return ;
                }
                if (MotDePasse.length()<6){
                    motDePasse.setError("le mot de passe doit contenir au moins 6 caractères");
                    return;
                }
                if (!MotDePasse.equals(ConfirmMotDePasse)){
                    confirmMotDePasse.setError("mot de passe resaisi non valide");
                    return;
                }
                enCours.setVisibility(View.VISIBLE);
                //enregistrer l'utilisateur
                firebaseAuth.createUserWithEmailAndPassword(Email,MotDePasse).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        //email de vérification
                        FirebaseUser utilisateur = firebaseAuth.getCurrentUser();
                        utilisateur.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(inscription.this, "Email de vérification a été envoyé ! ", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Erreur : Email non envoyé du à" +e.getMessage());
                            }
                        });


                        Toast.makeText(inscription.this,"BIENVENUE A alloDOC",Toast.LENGTH_SHORT).show();
                        userID = firebaseAuth.getCurrentUser().getUid();
                        //créer un document pour chaque utilisateur afin de collecter ses informations
                        DocumentReference documentReference = fStore.collection("users").document(userID);
                        Map<String,Object>  user = new HashMap<>();
                        //donnée d'un utilisateur
                        //TODO : ajouter le numéro de téléphone et séparer nom et prénom dans l'inscription
                        user.put("nomCompet",Nom);
                        user.put ("email",Email);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "onSuccess: le compte a été créé pour"+ userID);

                            }
                        });
                        startActivity(new Intent(getApplicationContext(),ChoixConsultation.class));
                    } else{
                        Toast.makeText(inscription.this, "Erreur " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        enCours.setVisibility(View.GONE);

                    }
                    }
                });
            }
        });
        compte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),connexion.class));
            }
        });

    }
}