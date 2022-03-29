package com.example.allodocc;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allodoc.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;


public class UserProfil extends AppCompatActivity {
    TextView textSalut;
    EditText name;
    EditText Email;
    EditText Phone;
    EditText Ville;
    Button localisation;
    Button save;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profil);
       textSalut = findViewById(R.id.Salut);
        name=findViewById(R.id.EditFULLName);
        Email = findViewById(R.id.EditEmail);
        Phone=findViewById(R.id.EditPhone);
        Ville=findViewById(R.id.EditVille);
        localisation=findViewById(R.id.Localisation);
        save=findViewById(R.id.save);
        firebaseAuth =FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();
        String UID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(UID);
        localisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),UserProfil.class));
            }
        });
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                name.setText(value.getString("nomCompet"));
                textSalut.setText("Salut "+value.getString("nomCompet"));
                Email.setText(value.getString("email"));
               if (value.getString("Telephone")!=null){
                Phone.setText(value.getString("Telephone"));}
                if(value.getString("Ville")!=null){
                Ville.setText(value.getString("Ville"));}

            }

        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> user = new HashMap<>();
                //donnée d'un utilisateur
                //TODO : ajouter le numéro de téléphone et séparer nom et prénom dans l'inscription
                String FULLNAME =name.getText().toString();
                String EMAIL=  Email.getText().toString();
                String PHONE = Phone.getText().toString();
                String VILLE = Ville.getText().toString();
                user.put("nomCompet",FULLNAME);
                user.put ("email",EMAIL);
                user.put ("Telephone",PHONE);
                user.put ("Ville",VILLE);


                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UserProfil.this, "success", Toast.LENGTH_SHORT).show();

                    }



                });
            }
        });







    }
}