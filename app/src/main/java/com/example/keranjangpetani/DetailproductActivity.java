package com.example.keranjangpetani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class DetailproductActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailproduct);

        //hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = FirebaseFirestore.getInstance();

        ImageView ivThumbnail = findViewById(R.id.ivThumbnail);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvDescription = findViewById(R.id.tvDescription);
        LinearLayout btnWatsapp = findViewById(R.id.btnWatsapp);

        btnWatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnWatsapp();
            }
        });

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            String id = extras.getString("productId");

            DocumentReference docRef = db.collection("products").document(id);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            Glide.with(DetailproductActivity.this)
                                    .load(document.getString("image"))
                                    .placeholder(R.drawable.ic_baseline_image_24)
                                    .into(ivThumbnail);
                            tvTitle.setText(document.getString("title"));
                            tvPrice.setText(document.getString("price"));
                            tvDescription.setText(document.getString("description"));

                            getUser(document.getString("userId"));
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    private void getUser(String userId) {
        DocumentReference documentReference = db.collection("users").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        phone = document.getString("phone");
                    } else {
                        Log.d("TAG", "No such document");
                    }

                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    private void btnWatsapp() {
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String url = "https://api.whatsapp.com/send?phone=" + phone;
                try {
                    PackageManager pm = DetailproductActivity.this.getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(DetailproductActivity.this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}