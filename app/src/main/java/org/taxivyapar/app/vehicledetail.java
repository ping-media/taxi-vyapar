package org.taxivyapar.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class vehicledetail extends AppCompatActivity {

    View back;
    Spinner spinner;
    ImageView image1, image2, image3, image4, image5, rcfront, rcback, insurance;
    TextView startdate;
    EditText vehno, vehyear;
    CardView login;
    Dialog loading;
    ArrayAdapter ad;
    String hrNm = "";
    String st_date = "", attachment1 = "", attachment2 = "", attachment3 = "", attachment4 = "", timestmp = "",
            attachment5 = "", type = "", rcattachment1 = "", rcattachment2 = "", insurattachment = "", transId = "";
    Uri imageuri;
    StorageReference storageReference;
    private ActivityResultLauncher<CropImageContractOptions> cropImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicledetail);

        back = findViewById(R.id.view);

        vehno = findViewById(R.id.name);
        vehyear = findViewById(R.id.company);
        spinner = findViewById(R.id.spinner);
        startdate = findViewById(R.id.startdate);

        login = findViewById(R.id.login);

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        rcfront = findViewById(R.id.rcfont);
        rcback = findViewById(R.id.rcback);
        insurance = findViewById(R.id.imageinsurance);

        loading = new Dialog(this);
        loading.setContentView(R.layout.ui_loading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);

        ArrayList<String> vehicle = new ArrayList<>();
        vehicle.add("Hatchback");
        vehicle.add("Sedan");
        vehicle.add("Ertiga");
        vehicle.add("Suv");
        vehicle.add("Kia Cerens");
        vehicle.add("Innova");
        vehicle.add("Innova Crysta");
        vehicle.add("Innova Hycross");
        vehicle.add("Force Traveller");
        vehicle.add("Bus");

        ad = new ArrayAdapter(getApplicationContext(), R.layout.simple_spinner_item, vehicle);
        ad.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(ad);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                hrNm = vehicle.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        timestmp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
        transId = profileContainer.userMobileNo + "_" + timestmp;

        cropImage = registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful()) {
                imageuri = result.getUriContent();
                updaloadingprofile();
            } else {
                Exception exception = result.getError();
            }
        });

        loading.show();
        FirebaseFirestore.getInstance().collection("users")
                .document(profileContainer.userMobileNo).collection("vehicles")
                .document(profileContainer.productId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        loading.dismiss();
                        if (documentSnapshot.exists()) {
                            hrNm = documentSnapshot.getString("VehicleName");
                            spinner.setSelection(vehicle.indexOf(hrNm));
                            vehno.setText(documentSnapshot.getString("VehicleNumber"));
                            vehyear.setText(documentSnapshot.getString("VehicleYear"));
                            startdate.setText(documentSnapshot.getString("InsuranceExpiry"));

                            insurattachment = documentSnapshot.getString("Insurance");
                            rcattachment1 = documentSnapshot.getString("RC1");
                            rcattachment2 = documentSnapshot.getString("RC2");
                            attachment1 = documentSnapshot.getString("Attachment1");
                            attachment2 = documentSnapshot.getString("Attachment2");
                            attachment3 = documentSnapshot.getString("Attachment3");
                            attachment4 = documentSnapshot.getString("Attachment4");
                            attachment5 = documentSnapshot.getString("Attachment5");

                            Glide.with(vehicledetail.this).load(insurattachment).into(insurance);
                            Glide.with(vehicledetail.this).load(rcattachment1).into(rcfront);
                            Glide.with(vehicledetail.this).load(rcattachment2).into(rcback);
                            Glide.with(vehicledetail.this).load(attachment1).into(image1);
                            Glide.with(vehicledetail.this).load(attachment2).into(image2);
                            Glide.with(vehicledetail.this).load(attachment3).into(image3);
                            Glide.with(vehicledetail.this).load(attachment4).into(image4);
                            Glide.with(vehicledetail.this).load(attachment5).into(image5);
                        } else {
                            Toast.makeText(vehicledetail.this, "vehicle is deleted", Toast.LENGTH_SHORT).show();
                            vehicledetail.super.onBackPressed();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(vehicledetail.this, "Your internet is not working", Toast.LENGTH_SHORT).show();
                        vehicledetail.super.onBackPressed();
                    }
                });

        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        vehicledetail.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        st_date = year + "_" + String.format("%02d", (month + 1)) + "_" + String.format("%02d", dayOfMonth);
                        if (month == 0) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Jan, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Jan, " + year);
                            }
                        }
                        if (month == 1) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Feb, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Feb, " + year);
                            }
                        }
                        if (month == 2) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Mar, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Mar, " + year);
                            }
                        }
                        if (month == 3) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Apr, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Apr, " + year);
                            }
                        }
                        if (month == 4) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " May, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " May, " + year);
                            }
                        }
                        if (month == 5) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Jun, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Jun, " + year);
                            }
                        }
                        if (month == 6) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Jul, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Jul, " + year);
                            }
                        }
                        if (month == 7) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Aug, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Aug, " + year);
                            }
                        }
                        if (month == 8) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Sep, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Sep, " + year);
                            }
                        }
                        if (month == 9) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Oct, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Oct, " + year);
                            }
                        }
                        if (month == 10) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Nov, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Nov, " + year);
                            }
                        }
                        if (month == 11) {
                            if (dayOfMonth < 10) {
                                startdate.setText("0" + dayOfMonth + " Dec, " + year);
                            } else {
                                startdate.setText(dayOfMonth + " Dec, " + year);
                            }
                        }

                    }
                }, year, month, day
                );
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        insurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "insurance";
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });
        rcfront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "rcfront";
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });
        rcback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "rcback";
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "image1";
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "image2";
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "image3";
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "image4";
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });
        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "image5";
                cropImage.launch(new CropImageContractOptions(null, new CropImageOptions()));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                login.startAnimation(animation);
                String vehicleNumber = vehno.getText().toString().trim();
                boolean isValid = vehicleNumber.toUpperCase().matches("^[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}$");

                if (vehno.getText().toString().trim().isEmpty()) {
                    Toast.makeText(vehicledetail.this, "Please add vehicle number", Toast.LENGTH_SHORT).show();
                } else if (!isValid) {
                    Toast.makeText(vehicledetail.this, "Invalid Vehicle Number", Toast.LENGTH_SHORT).show();
                } else if (vehyear.getText().toString().trim().isEmpty()) {
                    Toast.makeText(vehicledetail.this, "Please add vehicle registration year", Toast.LENGTH_SHORT).show();
                } else if (vehyear.getText().toString().trim().length() < 4) {
                    Toast.makeText(vehicledetail.this, "Invalid registration year", Toast.LENGTH_SHORT).show();
                } else if (insurattachment.equals("")) {
                    Toast.makeText(vehicledetail.this, "Please add vehicle insurance image", Toast.LENGTH_SHORT).show();
                } else if (startdate.getText().toString().trim().isEmpty()) {
                    Toast.makeText(vehicledetail.this, "Please add insurance expiry date", Toast.LENGTH_SHORT).show();
                } else if (rcattachment1.equals("")) {
                    Toast.makeText(vehicledetail.this, "Please add vehicle RC Front image", Toast.LENGTH_SHORT).show();
                } else if (rcattachment2.equals("")) {
                    Toast.makeText(vehicledetail.this, "Please add vehicle RC Back image", Toast.LENGTH_SHORT).show();
                } else if (attachment1.equals("") && attachment2.equals("") && attachment3.equals("")
                        && attachment4.equals("") && attachment5.equals("")) {
                    Toast.makeText(vehicledetail.this, "Please add vehicle image", Toast.LENGTH_SHORT).show();
                } else {

                    Map<String, Object> user = new HashMap<>();
                    user.put("VehicleName", hrNm);
                    user.put("VehicleYear", vehyear.getText().toString().trim());
                    user.put("VehicleNumber", vehno.getText().toString().trim().toUpperCase());
                    user.put("Insurance", insurattachment);
                    user.put("InsuranceExpiry", startdate.getText().toString());
                    user.put("RC1", rcattachment1);
                    user.put("RC2", rcattachment2);
                    user.put("Attachment1", attachment1);
                    user.put("Attachment2", attachment2);
                    user.put("Attachment3", attachment3);
                    user.put("Attachment4", attachment4);
                    user.put("Attachment5", attachment5);

                    loading.show();

                    FirebaseFirestore.getInstance().collection("users").document(profileContainer.userMobileNo)
                            .collection("vehicles").document(profileContainer.productId)
                            .update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    loading.dismiss();
                                    Toast.makeText(vehicledetail.this, "Vehicle updated successfully.", Toast.LENGTH_SHORT).show();
                                    vehicledetail.super.onBackPressed();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading.dismiss();
                                    Toast.makeText(vehicledetail.this, "Your internet is not working.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void updaloadingprofile() {
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
                                if (type.equals("insurance")) {
                                    insurattachment = uri.toString();
                                } else if (type.equals("rcfront")) {
                                    rcattachment1 = uri.toString();
                                } else if (type.equals("rcback")) {
                                    rcattachment2 = uri.toString();
                                } else if (type.equals("image1")) {
                                    attachment1 = uri.toString();
                                } else if (type.equals("image2")) {
                                    attachment2 = uri.toString();
                                } else if (type.equals("image3")) {
                                    attachment3 = uri.toString();
                                } else if (type.equals("image4")) {
                                    attachment4 = uri.toString();
                                } else if (type.equals("image5")) {
                                    attachment5 = uri.toString();
                                }

                                Glide.with(vehicledetail.this).load(insurattachment).into(insurance);
                                Glide.with(vehicledetail.this).load(rcattachment1).into(rcfront);
                                Glide.with(vehicledetail.this).load(rcattachment2).into(rcback);
                                Glide.with(vehicledetail.this).load(attachment1).into(image1);
                                Glide.with(vehicledetail.this).load(attachment2).into(image2);
                                Glide.with(vehicledetail.this).load(attachment3).into(image3);
                                Glide.with(vehicledetail.this).load(attachment4).into(image4);
                                Glide.with(vehicledetail.this).load(attachment5).into(image5);

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