package com.example.keranjangpetani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyshopActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    AdapterRecyclerview adapterRecyclerview;
    RecyclerView.LayoutManager layoutManager;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myshop);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        if (getSupportActionBar()!= null) {
            getSupportActionBar().hide();
        }
        getData();
    }

    private void getData() {

        Log.d("test123", "masuk: ");
        db.collection("products").whereEqualTo("userId", currentUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Product> data = new ArrayList<Product>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = new Product();
                        product.setUuid(document.getId());
                        product.setTitle(document.getString("title"));
                        product.setPrice(document.getString("price"));
                        product.setDescription(document.getString("description"));
                        product.setImage(Uri.parse(document.getString("image")));
                        data.add(product);
                        Log.d("Document", document.getId() + " => " + document.getData());
                    }

                    initRecyclerView(data);
                } else {
                    Log.d("ErrorDocument", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    private void initRecyclerView(ArrayList<Product> data) {
        adapterRecyclerview = new AdapterRecyclerview(MyshopActivity.this, data);

        recyclerView.setAdapter(adapterRecyclerview);

        adapterRecyclerview = new AdapterRecyclerview(data);
        recyclerView.setAdapter(adapterRecyclerview);

    }
}