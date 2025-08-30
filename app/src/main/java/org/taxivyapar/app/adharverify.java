package org.taxivyapar.app;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class adharverify extends AppCompatActivity {
    View back;
    CardView login;
    EditText adhar;
    Dialog loading;
    TextView warning;
    ImageView adharfront, adharback;
    String adharattachment1 = "", adharattachment2 = "", type = "", verify = "no";
    Uri imageuri;
    StorageReference storageReference;
    private ActivityResultLauncher<CropImageContractOptions> cropImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adharverify);

        back = findViewById(R.id.view);
        adhar = findViewById(R.id.adhar);
        adharfront = findViewById(R.id.adharfont);
        adharback = findViewById(R.id.adharback);

        login = findViewById(R.id.verify);
        warning = findViewById(R.id.textView2);

        loading = new Dialog(this);
        loading.setContentView(R.layout.ui_loading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);

        cropImage = registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful()) {
                imageuri = result.getUriContent();
                updaloadingprofile();
            } else {
                Exception exception = result.getError();
            }
        });

        loading.show();
        FirebaseFirestore.getInstance().collection("users").document(profileContainer.userMobileNo)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        loading.dismiss();
                        if (documentSnapshot.getString("userAdharStatus") != null) {
                            if (documentSnapshot.getString("userAdharStatus").equals("review")) {
                                verify = "review";
                                warning.setText("Your adhar verification is under review.\nIt will take 24-48 hrs.");
                                adharattachment1 = documentSnapshot.getString("userAdharFront");
                                adharattachment2 = documentSnapshot.getString("userAdharBack");
                                adhar.setText(documentSnapshot.getString("userAdhar"));
                                Glide.with(adharverify.this).load(adharattachment1).into(adharfront);
                                Glide.with(adharverify.this).load(adharattachment2).into(adharback);
                                adhar.setEnabled(false);
                                login.setVisibility(View.GONE);
                            } else if (documentSnapshot.getString("userAdharStatus").equals("success")) {
                                verify = "success";
                                warning.setText("Your adhar datails are Verified");
                                warning.setTextColor(Color.parseColor("#4CAF50"));
                                adharattachment1 = documentSnapshot.getString("userAdharFront");
                                adharattachment2 = documentSnapshot.getString("userAdharBack");
                                adhar.setText(documentSnapshot.getString("userAdhar"));
                                adhar.setEnabled(false);
                                login.setVisibility(View.GONE);
                                Glide.with(adharverify.this).load(adharattachment1).into(adharfront);
                                Glide.with(adharverify.this).load(adharattachment2).into(adharback);
                            } else {
                                verify = "reject";
                                warning.setText("Your adhar details are rejected");
                                warning.setTextColor(Color.parseColor("#4CAF50"));
                                adharattachment1 = documentSnapshot.getString("userAdharFront");
                                adharattachment2 = documentSnapshot.getString("userAdharBack");
                                adhar.setText(documentSnapshot.getString("userAdhar"));
                                Glide.with(adharverify.this).load(adharattachment1).into(adharfront);
                                Glide.with(adharverify.this).load(adharattachment2).into(adharback);
                            }
                        } else {
                            verify = "no";
                        }
                    }
                });

        adharfront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verify.equals("review") || verify.equals("success")) {
                    return;
                }
                type = "adharfront";
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });
        adharback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verify.equals("review") || verify.equals("success")) {
                    return;
                }
                type = "adharback";
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verify.equals("review") || verify.equals("success")) {
                    return;
                }
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                login.startAnimation(animation);
                if (adhar.getText().toString().trim().isEmpty()) {
                    Toast.makeText(adharverify.this, "Please enter Aadhar number", Toast.LENGTH_SHORT).show();
                } else if (adhar.getText().toString().length() != 12) {
                    Toast.makeText(adharverify.this, "Invalid Aadhar number", Toast.LENGTH_SHORT).show();
                } else if (adharattachment1.equals("")) {
                    Toast.makeText(adharverify.this, "Upload Aadhar font image", Toast.LENGTH_SHORT).show();
                } else if (adharattachment2.equals("")) {
                    Toast.makeText(adharverify.this, "Upload Aadhar back image", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> user = new HashMap<>();
                    user.put("userAdhar", adhar.getText().toString().trim().toUpperCase());
                    user.put("userAdharFront", adharattachment1);
                    user.put("userAdharBack", adharattachment2);
                    user.put("userAdharStatus", "review");
                    loading.show();
                    FirebaseFirestore.getInstance().collection("users").document(profileContainer.userMobileNo)
                            .update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    loading.dismiss();
                                    Toast.makeText(adharverify.this, "Your detail send successfully.", Toast.LENGTH_SHORT).show();
                                    verify = "review";
                                    warning.setText("Your adhar verification is under review.\nIt will take 24-48 hrs.");
                                    adhar.setEnabled(false);
                                    login.setVisibility(View.GONE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading.dismiss();
                                    Toast.makeText(adharverify.this, "Your internet is not working.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                back.startAnimation(animation);
                adharverify.super.onBackPressed();
            }
        });
    }

    private void updaloadingprofile() {
        String timestmp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
        String transId = profileContainer.userMobileNo + "_" + timestmp;
        String filename = transId + "_" + type;
        storageReference = FirebaseStorage.getInstance().getReference("ProfileImage/" + profileContainer.userMobileNo + "/" + filename);
        loading.show();
        storageReference.putFile(imageuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                loading.dismiss();
                                if (type.equals("adharfront")) {
                                    adharattachment1 = uri.toString();
                                } else if (type.equals("adharback")) {
                                    adharattachment2 = uri.toString();
                                }
                                Glide.with(adharverify.this).load(adharattachment1).into(adharfront);
                                Glide.with(adharverify.this).load(adharattachment2).into(adharback);

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(), "Uploading Error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}