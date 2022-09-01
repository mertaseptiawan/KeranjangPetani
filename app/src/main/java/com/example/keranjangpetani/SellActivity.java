package com.example.keranjangpetani;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SellActivity extends AppCompatActivity {

    private final int SELECT_PICTURE = 1;
    private TextView etJudul, etHarga, etDeskripsi;
    private String judul, harga, deskripsi;
    private ImageView ivThumbnail;
    private Button btnPosting;
    private Uri selectedImageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ProgressDialog progressDialog;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        //hide action bar
        if (getSupportActionBar()!= null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        btnPosting = findViewById(R.id.btnPosting);
        ivThumbnail = findViewById(R.id.ivThumbnail);

        etJudul = findViewById(R.id.etJudul);
        etHarga = findViewById(R.id.etHarga);
        etDeskripsi = findViewById(R.id.etDeskripsi);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Memuat data...");

        ivThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.iSell);
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
                        return true;

                    case R.id.iHome:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }

                return false;
            }
        });

        btnPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validate();
            }
        });
    }

    private void validate() {

        judul = etJudul.getText().toString().trim();
        harga = etHarga.getText().toString().trim();
        deskripsi = etDeskripsi.getText().toString().trim();

        //validation
        if (judul.isEmpty()) {
            etJudul.setError("masukkan Judul postingan");
            etJudul.requestFocus();
            return;
        }

        if (harga.isEmpty()) {
            etHarga.setError("masukkan harga produk");
            etHarga.requestFocus();
            return;
        }

        if (deskripsi.isEmpty()) {
            etDeskripsi.setError("masukkan deskripsi produk");
            etDeskripsi.requestFocus();
            return;
        }

        uploadImage();

    }

    private void posting() {

        FirebaseUser user = mAuth.getCurrentUser();

        Map<String, Object> product = new HashMap<>();
        assert user != null;
        product.put("userId", user.getUid());
        product.put("title", judul);
        product.put("price", harga);
        product.put("description", deskripsi);

        if (selectedImageUri != null) {
            product.put("image", selectedImageUri.toString());
        }

        DocumentReference documentReference = db.collection("products").document(UUID.randomUUID().toString());
        documentReference.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void unused) {
                progressDialog.hide();
                Toast.makeText(SellActivity.this, "Berhasil Membuat Postingan", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SellActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.hide();
                Toast.makeText(SellActivity.this, "Gagal membuat postingan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();

                if (null != selectedImageUri) {
                    ivThumbnail.setImageURI(selectedImageUri);
                }
            }
        }
    }
    //
    private void uploadImage() {
        if (selectedImageUri ==null) {

            Toast.makeText(SellActivity.this, "foto produk masih kosong", Toast.LENGTH_SHORT).show();

        }

        if (selectedImageUri !=null) {

            progressDialog.show();

            StorageReference storageRef = storageReference .child("products/*" + UUID.randomUUID().toString());

            ivThumbnail.setDrawingCacheEnabled(true);
            ivThumbnail.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) ivThumbnail.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageRef.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(@NonNull Uri uri) {
                            selectedImageUri = uri;
                            Log.d("imageURL", selectedImageUri.toString());

                            posting();

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    //error, image not upload
                    progressDialog.hide();
                    Toast.makeText(SellActivity.this, "Gagal Mengupload Gambar" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                // Progress Listener for loading
                // percentage on the dialog box
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress
                            = (100.0
                            * taskSnapshot.getBytesTransferred()
                            / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage(
                            "Mengupload "
                                    + (int)progress + "%");
                }
            });
        }
    }
}