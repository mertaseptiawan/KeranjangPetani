package com.example.keranjangpetani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;
    AdapterRecyclerview adapterRecyclerview;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Product> data;
    ArrayList<Product> filteredData;
    ProgressDialog progressDialog;
    private FirebaseFirestore db;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        data = new ArrayList<Product>();
        filteredData = new ArrayList<Product>();

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        searchView = findViewById(R.id.searchView);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.iHome);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.iDashboard:
                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.iSell:
                        startActivity(new Intent(getApplicationContext(), SellActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.iHome:
                        return true;
                }

                return false;
            }
        });

        getData();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return true;
            }
        });
    }

    private void search(String str) {
        if (!str.equals("")) {
            filteredData = new ArrayList<Product>();
            for (Product product : data) {
                if (product.getTitle().toLowerCase().contains(str.toLowerCase())) {
                    filteredData.add(product);
                }
            }
        } else {
            filteredData = data;
        }

        AdapterRecyclerview adapterRecyclerview = new AdapterRecyclerview(filteredData);
        recyclerView.setAdapter(adapterRecyclerview);
    }

    private void getData() {

        Log.d("test123", "masuk: ");
        db.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = new Product();
                        product.setUuid(document.getId());
                        product.setTitle(document.getString("title"));
                        product.setPrice(document.getString("price"));
                        product.setDescription(document.getString("description"));
                        product.setImage(Uri.parse(document.getString("image")));
                        data.add(product);
                        filteredData.add(product);
                        Log.d("Document", document.getId() + " => " + document.getData());
                    }

                    initRecyclerView(filteredData);
                } else {
                    Log.d("ErrorDocument", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void initRecyclerView(ArrayList<Product> data) {
        adapterRecyclerview = new AdapterRecyclerview(HomeActivity.this, data);

        recyclerView.setAdapter(adapterRecyclerview);

        adapterRecyclerview = new AdapterRecyclerview(data);
        recyclerView.setAdapter(adapterRecyclerview);

    }
}