package org.taxivyapar.app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class profile extends AppCompatActivity {
    ConstraintLayout home, booking, addpost, chat, profile, yeslogout, nologout;
    CardView addbooking, addvehicle, profileimageupdate, upgrade, verify;
    View closeaddpost, verifyicon;
    BottomSheetDialog addpostPopup;
    Dialog loading, logoutpopup;
    TextView balance;
    TextView username, usermobileno, avatar, onoff, warning, plan, verifytext, upgradetext;
    ImageView profileimage;
    Uri imageuri;
    StorageReference storageReference;
    String isActive = "no";
    private ActivityResultLauncher<CropImageContractOptions> cropImage;
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        home = findViewById(R.id.home);
        booking = findViewById(R.id.booking);
        addpost = findViewById(R.id.addpost);
        chat = findViewById(R.id.chat);
        profile = findViewById(R.id.profile);

        balance = findViewById(R.id.balance);

        verifytext = findViewById(R.id.verifytext);
        upgradetext = findViewById(R.id.upgradetext);
        verifyicon = findViewById(R.id.view10);
        warning = findViewById(R.id.textView2);
        plan = findViewById(R.id.plan);

        upgrade = findViewById(R.id.cardView);
        verify = findViewById(R.id.verify);

        username = findViewById(R.id.profilename);
        usermobileno = findViewById(R.id.mobilenumber);
        profileimage = findViewById(R.id.profileimage);
        avatar = findViewById(R.id.avatar);
        profileimageupdate = findViewById(R.id.updateprodileimage);

        onoff = findViewById(R.id.onoff);

        addpostPopup = new BottomSheetDialog(this);
        addpostPopup.setContentView(R.layout.ui_addpost);
        addbooking = addpostPopup.findViewById(R.id.addbooking);
        addvehicle = addpostPopup.findViewById(R.id.addvehicle);
        closeaddpost = addpostPopup.findViewById(R.id.viewmanu);

        loading = new Dialog(this);
        loading.setContentView(R.layout.ui_loading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);

        logoutpopup = new Dialog(this);
        logoutpopup.setContentView(R.layout.popup_logout);
        logoutpopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        yeslogout = logoutpopup.findViewById(R.id.yeslogout);
        nologout = logoutpopup.findViewById(R.id.nologout);

        try {
            Glide.with(profile.this).load(profileContainer.userProfileImageUrl).into(profileimage);
        } catch (Exception e) {
        }
        try {
            avatar.setText(profileContainer.userName.substring(0, 1).toUpperCase());
        } catch (Exception e) {
            avatar.setText("A");
        }

        cropImage = registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful()) {
                imageuri = result.getUriContent();
                updaloadingprofile();
            } else {
                Exception exception = result.getError();
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                verify.startAnimation(animation);
                startActivity(new Intent(getApplicationContext(), adharverify.class));
            }
        });

        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                upgrade.startAnimation(animation);
                startActivity(new Intent(getApplicationContext(), subscribe.class));
            }
        });

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileContainer.imageviewuser = profileContainer.userName;
                profileContainer.imageviewuserurl = profileContainer.userProfileImageUrl;
                startActivity(new Intent(getApplicationContext(), imageview.class));
            }
        });

        profileimageupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                profileimageupdate.startAnimation(animation);
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });

        closeaddpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                closeaddpost.startAnimation(animation);
                addpostPopup.dismiss();
            }
        });
        addbooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                addbooking.startAnimation(animation);
                startActivity(new Intent(getApplicationContext(), postbooking.class));
            }
        });
        addvehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                addvehicle.startAnimation(animation);
                startActivity(new Intent(getApplicationContext(), postvehicle.class));
            }
        });

        nologout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                nologout.startAnimation(animation);
                logoutpopup.dismiss();
            }
        });

        yeslogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                yeslogout.startAnimation(animation);
                SharedPreferences roomdbusermobileno = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = roomdbusermobileno.edit();
                editor.putString("userMobileNo", "00000");
                editor.apply();
                startActivity(new Intent(profile.this, MainActivity.class));
                finishAffinity();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), mybooking.class));
                finish();
                overridePendingTransition(0, 0);
            }
        });
        addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                addpost.startAnimation(animation);
                addpostPopup.show();
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), chat.class));
                finish();
                overridePendingTransition(0, 0);
            }
        });

    }

    @Override
    protected void onResume() {
        username.setText(profileContainer.userName);
        usermobileno.setText(profileContainer.userMobileNo);
        balance.setText("₹" + df.format(profileContainer.userWallet));

        SharedPreferences gettheme = getSharedPreferences("user", MODE_PRIVATE);
        String noti = gettheme.getString("notification", "on");
        if (noti.equals("on")) {
            onoff.setText("On");
        } else {
            onoff.setText("Off");
        }

        if (profileContainer.userVerify.equals("no")) {
            verifytext.setText("Verify Now");
            verifyicon.setVisibility(View.GONE);
//            verify.setVisibility(View.VISIBLE);
        } else {
            verifytext.setText("Verified");
            verifyicon.setVisibility(View.VISIBLE);
//            verify.setVisibility(View.GONE);
        }

        isActive = "no";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
            Date now = new Date();
            Date currentTime = sdf.parse(sdf.format(now));

            if (profileContainer.isActive != null && profileContainer.isActive.equals("yes")) {
                try {
                    Date expiryDate = sdf.parse(profileContainer.planExpiry);
                    if (expiryDate != null && expiryDate.after(currentTime)) {
                        isActive = "yes";
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        if (profileContainer.freeTrial.equals("yes")) {
            warning.setText("Free Trail");
            plan.setText("Active User");
            plan.setTextColor(Color.parseColor("#4CAF50"));
            plan.setBackgroundColor(Color.parseColor("#E9FFEB"));
            upgradetext.setText("Subscribed");
        } else if (profileContainer.userFreeTrial != null && profileContainer.userFreeTrial.equals("yes")) {
            warning.setText("Free Trail");
            plan.setText("Active User");
            plan.setTextColor(Color.parseColor("#4CAF50"));
            plan.setBackgroundColor(Color.parseColor("#E9FFEB"));
            upgradetext.setText("Subscribed");
        } else if (isActive.equals("yes")) {
            String inputString = profileContainer.planExpiry;
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
            LocalDateTime dateTime = LocalDateTime.parse(inputString, inputFormatter);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.ENGLISH);
            String outputString = dateTime.format(outputFormatter);
            warning.setText("Plan will expire on " + outputString);
            plan.setText("Active User");
            plan.setTextColor(Color.parseColor("#4CAF50"));
            plan.setBackgroundColor(Color.parseColor("#E9FFEB"));
            upgradetext.setText("Subscribed");
        } else {
            plan.setText("Free User");
            plan.setTextColor(Color.parseColor("#F44336"));
            plan.setBackgroundColor(Color.parseColor("#FFECEB"));
            warning.setText("You have not subscribe to any plan.");
            upgradetext.setText("Upgrade Now");

        }

        super.onResume();
    }

    private void updaloadingprofile() {
        String filename = profileContainer.userMobileNo + "_ProfileImage";
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
                                profileContainer.userProfileImageUrl = uri.toString();

                                Glide.with(profile.this).load(profileContainer.userProfileImageUrl).into(profileimage);
                                Map<String, Object> user1 = new HashMap<>();
                                user1.put("UserProfileImageUri", profileContainer.userProfileImageUrl);
                                FirebaseFirestore.getInstance().collection("users")
                                        .document(profileContainer.userMobileNo).update(user1);

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.dismiss();
                        Toast.makeText(profile.this, "Uploading Error!", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public void editProfile(View view) {
        startActivity(new Intent(getApplicationContext(), editprofile.class));
    }

    public void myNetwork(View view) {
        startActivity(new Intent(getApplicationContext(), mynetwork.class));
    }

    public void manageDriver(View view) {
        startActivity(new Intent(getApplicationContext(), managedriver.class));
    }

    public void manageVehicle(View view) {
        startActivity(new Intent(getApplicationContext(), managevehicle.class));
    }

    public void myPayment(View view) {
        startActivity(new Intent(getApplicationContext(), mykyc.class));
    }

    public void myTransaction(View view) {
        startActivity(new Intent(getApplicationContext(), transaction.class));

    }

    public void alert(View view) {
        startActivity(new Intent(getApplicationContext(), notifications.class));
    }

    public void about(View view) {
        startActivity(new Intent(getApplicationContext(), about.class));
    }

    public void privacy(View view) {
        startActivity(new Intent(getApplicationContext(), privacy.class));
    }

    public void support(View view) {
        startActivity(new Intent(getApplicationContext(), support.class));
    }

    public void logout(View view) {
        logoutpopup.show();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }

}