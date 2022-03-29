package com.example.allodoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class connexion extends AppCompatActivity {
    TextView compte , mdpOblié;
    EditText email , motDePasse;
    Button seConnecter;
    ProgressBar enCours ;
    FirebaseAuth firebaseAuth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        email = findViewById(R.id.email);
        motDePasse = findViewById(R.id.motDePasse);
        compte = findViewById(R.id.compte);
        enCours = findViewById(R.id.enCours);
        seConnecter = findViewById(R.id.seConnecter);
        firebaseAuth = FirebaseAuth.getInstance();
        mdpOblié = findViewById(R.id.mdpOblié);
        seConnecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = email.getText().toString().trim();
                String MotDePasse = motDePasse.getText().toString().trim();
                if (TextUtils.isEmpty(Email)){
                    email.setError("merci d'entrer votre email");
                    return ;
                }
                if (TextUtils.isEmpty(MotDePasse)){
                    motDePasse.setError("merci d'entrer votre mot de passe");
                    return ;
                }
                enCours.setVisibility(View.VISIBLE);
                //authenfication
                firebaseAuth.signInWithEmailAndPassword(Email,MotDePasse).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(connexion.this,"BIENVENUE A alloDOC",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),ChoixConsultation.class));
                        } else{
                            Toast.makeText(connexion.this, "Erreur " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            enCours.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        compte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),inscription.class));

            }
        });
        //mot de passe oublié
        mdpOblié.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nouveauMdpEmail = new EditText(view.getContext());
                AlertDialog.Builder motDepasseOubliéDialog = new AlertDialog.Builder(view.getContext());
                motDepasseOubliéDialog.setTitle("mot de passe oublié ?");
                motDepasseOubliéDialog.setMessage("entrer votre email pour réinitialiser votre mot de passe");
                motDepasseOubliéDialog.setView(nouveauMdpEmail);
                motDepasseOubliéDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = nouveauMdpEmail.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(connexion.this, "Lien envoyé à votre adresse mail", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(connexion.this, "Erreur ! Lien non envoyé du au "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                motDepasseOubliéDialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //la nouvelle fenetre sera fermé
                    }
                });
                motDepasseOubliéDialog.create().show();
            }
        });
    }
}