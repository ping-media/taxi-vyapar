package org.taxivyapar.app;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class bookingdetail extends AppCompatActivity {
    View back;
    ConstraintLayout loading;
    TextView vehiclename, startdate, bookingid, location, triptext, tripdetail, droplocation, remark, negotiable, city, type,
            mobile, name, avatar, amount, earning, commision,review;
    ImageView profileimage, vehicleimage;
    ConstraintLayout hidden, profileview;
    LinearLayout layearning, laycommision;
    CardView call, chat, securetag;
    View rat1, rat2, rat3, rat4, rat5, map, verify;
    String transId = "", custno = "", custname = "", isActive = "no";
    BottomSheetDialog chatpopup;
    Dialog load;
    View closechat;
    RecyclerView recyclerchat;
    recycleradepterchat adeptrochat;
    ArrayList<Modelinfo> modelchat;
    TextView chatno, warring;
    DocumentSnapshot document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookingdetail);
        back = findViewById(R.id.view);
        loading = findViewById(R.id.loading);

        vehiclename = findViewById(R.id.vehiclename);
        vehicleimage = findViewById(R.id.vehicleimage);
        startdate = findViewById(R.id.startdate);
        location = findViewById(R.id.location);
        remark = findViewById(R.id.remark);
        name = findViewById(R.id.textView14);
        mobile = findViewById(R.id.mobilenumber);
        avatar = findViewById(R.id.avatar);
        profileimage = findViewById(R.id.profileimage);
        call = findViewById(R.id.call);
        chat = findViewById(R.id.chat);
        securetag = findViewById(R.id.securetag);
        bookingid = findViewById(R.id.bookingid);
        droplocation = findViewById(R.id.droplocation);
        amount = findViewById(R.id.amount);
        earning = findViewById(R.id.earning);
        commision = findViewById(R.id.commision);
        hidden = findViewById(R.id.hidden);
        layearning = findViewById(R.id.layearning);
        laycommision = findViewById(R.id.laycommi);
        negotiable = findViewById(R.id.negotiable);
        city = findViewById(R.id.city);
        type = findViewById(R.id.type);
        triptext = findViewById(R.id.triptext);
        tripdetail = findViewById(R.id.tripdetail);
        warring = findViewById(R.id.textView2);
        review = findViewById(R.id.review);

        rat1 = findViewById(R.id.rat1);
        rat2 = findViewById(R.id.rat2);
        rat3 = findViewById(R.id.rat3);
        rat4 = findViewById(R.id.rat4);
        rat5 = findViewById(R.id.rat5);

        map = findViewById(R.id.map);

        verify = findViewById(R.id.view10);
        profileview = findViewById(R.id.profileview);

        load = new Dialog(this);
        load.setContentView(R.layout.ui_loading);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        load.setCancelable(false);

        chatpopup = new BottomSheetDialog(this);
        chatpopup.setContentView(R.layout.ui_listchat);
        closechat = chatpopup.findViewById(R.id.viewmanu);
        chatno = chatpopup.findViewById(R.id.sample);
        recyclerchat = chatpopup.findViewById(R.id.recyclercutomer);

        recyclerchat.setLayoutManager(new LinearLayoutManager(this));
        modelchat = new ArrayList<>();
        adeptrochat = new recycleradepterchat(modelchat);
        recyclerchat.setAdapter(adeptrochat);

        Intent i = getIntent();
        transId = i.getStringExtra("transId");
        custno = i.getStringExtra("custno");
        custname = i.getStringExtra("custname");

        initial();

        closechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                closechat.startAnimation(animation);
                chatpopup.dismiss();
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                map.startAnimation(animation);
                String map = "https://www.google.com/maps/dir/?api=1" +
                        "&origin=" + document.getString("AddressLat") + "," + document.getString("AddressLng") +
                        "&destination=" + document.getString("DropAddressLat") + "," + document.getString("DropAddressLng");
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(map));
                    startActivity(i);
                } catch (Exception e) {
                }
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                call.startAnimation(animation);
                if (isActive.equals("no")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(bookingdetail.this);
                    builder.setTitle("Not Subscribe");
                    builder.setMessage("You're not subscribed! Unlock exclusive feature by subscribing.");
                    builder.create();
                    builder.setPositiveButton("Subscribe Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            startActivity(new Intent(getApplicationContext(), subscribe.class));
                        }
                    });
                    builder.show();
                    return;
                }
                try {
                    String url = "tel: " + document.getString("SenderMobileNo");
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (Exception e) {
                }
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                chat.startAnimation(animation);
                if (custno.equals(profileContainer.userMobileNo)) {
                    load.show();
                    FirebaseFirestore.getInstance().collection("postBooking")
                            .document(transId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.getString("BookingAssignNo") != null) {
                                        load.dismiss();
                                        Intent i = new Intent(getApplicationContext(), bookchatmy.class);
                                        i.putExtra("transId", transId);
                                        i.putExtra("custno", documentSnapshot.getString("BookingAssignNo"));
                                        i.putExtra("custname", documentSnapshot.getString("BookingAssignName"));
                                        startActivity(i);
                                    } else {
                                        FirebaseFirestore.getInstance().collection("postBooking")
                                                .document(transId)
                                                .collection("message")
                                                .orderBy("TimeStamp", Query.Direction.DESCENDING)
                                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        load.dismiss();
                                                        chatno.setVisibility(View.VISIBLE);
                                                        modelchat.clear();
                                                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                        for (DocumentSnapshot d : list) {
                                                            Modelinfo obj = d.toObject(Modelinfo.class);
                                                            modelchat.add(obj);
                                                        }
                                                        adeptrochat.notifyDataSetChanged();
                                                        chatpopup.show();
                                                    }
                                                });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    load.dismiss();
                                    Toast.makeText(getApplicationContext(), "Your internet is not working.", Toast.LENGTH_SHORT).show();
                                }
                            });

                } else {

                    if (isActive.equals("no")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(bookingdetail.this);
                        builder.setTitle("Not Subscribe");
                        builder.setMessage("You're not subscribed! Unlock exclusive feature by subscribing.");
                        builder.create();
                        builder.setPositiveButton("Subscribe Now", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                startActivity(new Intent(getApplicationContext(), subscribe.class));
                            }
                        });
                        builder.show();
                        return;
                    }
//                    load.show();
//                    FirebaseFirestore.getInstance().collection("users")
//                            .document(profileContainer.userMobileNo).collection("vehicles")
//                            .whereEqualTo("VehicleName",vehiclename.getText().toString().trim())
//                            .limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                @Override
//                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                    load.dismiss();
//                                    if (queryDocumentSnapshots.size() > 0) {
                                        Intent i = new Intent(getApplicationContext(), bookchat.class);
                                        i.putExtra("transId", transId);
                                        i.putExtra("custno", custno);
                                        i.putExtra("custname", custname);
                                        startActivity(i);
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "No " + vehiclename.getText().toString().trim() + " vehicle found in your account.", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    load.dismiss();
//                                    Toast.makeText(getApplicationContext(), "Your internet is not working.", Toast.LENGTH_SHORT).show();
//                                }
//                            });



                }
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), reviewuser.class);
                i.putExtra("custno", document.getString("SenderMobileNo"));
                i.putExtra("custname", document.getString("SenderName"));
                startActivity(i);
            }
        });
        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), reviewuser.class);
                i.putExtra("custno", document.getString("SenderMobileNo"));
                i.putExtra("custname", document.getString("SenderName"));
                startActivity(i);
            }
        });
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), reviewuser.class);
                i.putExtra("custno", document.getString("SenderMobileNo"));
                i.putExtra("custname", document.getString("SenderName"));
                startActivity(i);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                back.startAnimation(animation);
                bookingdetail.super.onBackPressed();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initial();
    }

    private void initial() {

        isActive = "no";
        try {
            if (profileContainer.freeTrial.equals("yes")) {
                isActive = "yes";
            } else if (profileContainer.userFreeTrial != null) {
                if (profileContainer.userFreeTrial.equals("yes")) {
                    isActive = "yes";
                }
            }else {
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
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        loading.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("postBooking")
                .document(transId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        loading.setVisibility(View.GONE);
                        mobile.setVisibility(View.GONE);
                        verify.setVisibility(View.GONE);

                        document = documentSnapshot;

                        if (isActive.equals("yes")) {
                            bookingid.setText("ID: " + documentSnapshot.getString("BookingId")
                                    + " (" + documentSnapshot.getString("BookingPlatform") + ")");
                        } else {
                            bookingid.setText("ID: *****");
                        }

                        call.setVisibility(View.GONE);
                        if (documentSnapshot.getString("PreferenceContact").equals("call")) {
                            call.setVisibility(View.VISIBLE);
                        }

                        city.setText(documentSnapshot.getString("AddressCity") + " - " + documentSnapshot.getString("DropAddressCity"));
                        location.setText(documentSnapshot.getString("Address"));
                        droplocation.setText(documentSnapshot.getString("DropAddress"));

                        tripdetail.setText(documentSnapshot.getString("Description"));

                        triptext.setVisibility(View.GONE);
                        tripdetail.setVisibility(View.GONE);
                        if (documentSnapshot.getString("BookingType").equals("oneWay")) {

                            String date = documentSnapshot.getString("StartTimeStamp").substring(0, 10);
                            String timestmp = new SimpleDateFormat("yyyy_MM_dd", Locale.CANADA).format(new Date());

                            if (date.equals(timestmp)) {
                                startdate.setText("One Way - Today @ " + documentSnapshot.getString("StartTime"));
                            } else {
                                startdate.setText("One Way - " + documentSnapshot.getString("StartDate")
                                        + " @ " + documentSnapshot.getString("StartTime"));
                            }

                        } else {
                            triptext.setVisibility(View.VISIBLE);
                            tripdetail.setVisibility(View.VISIBLE);

                            String date = documentSnapshot.getString("StartTimeStamp").substring(0, 10);
                            String timestmp = new SimpleDateFormat("yyyy_MM_dd", Locale.CANADA).format(new Date());

                            if (date.equals(timestmp)) {
                                startdate.setText("Round Trip - Today @ " + documentSnapshot.getString("StartTime")
                                        + "\n(for " + documentSnapshot.getString("TourDays") + " Days)");
                            } else {
                                startdate.setText("Round Trip - " + documentSnapshot.getString("StartDate")
                                        + " @ " + documentSnapshot.getString("StartTime")
                                        + "\n(for " + documentSnapshot.getString("TourDays") + " Days)");
                            }
                        }

                        vehiclename.setText(documentSnapshot.getString("VehicleName"));

                        if (documentSnapshot.getString("VehicleName").equals("Hatchback")) {
                            vehicleimage.setImageResource(R.drawable.ic_hatchback);
                        } else if (documentSnapshot.getString("VehicleName").equals("Sedan")) {
                            vehicleimage.setImageResource(R.drawable.ic_sedan);
                        } else if (documentSnapshot.getString("VehicleName").equals("Ertiga")) {
                            vehicleimage.setImageResource(R.drawable.ic_eartiga);
                        } else if (documentSnapshot.getString("VehicleName").equals("Suv")) {
                            vehicleimage.setImageResource(R.drawable.ic_suv);
                        } else if (documentSnapshot.getString("VehicleName").equals("Kia Cerens")) {
                            vehicleimage.setImageResource(R.drawable.ic_kia);
                        } else if (documentSnapshot.getString("VehicleName").equals("Innova")) {
                            vehicleimage.setImageResource(R.drawable.ic_innova);
                        } else if (documentSnapshot.getString("VehicleName").equals("Innova Crysta")) {
                            vehicleimage.setImageResource(R.drawable.ic_innova_crysta);
                        } else if (documentSnapshot.getString("VehicleName").equals("Innova Hycross")) {
                            vehicleimage.setImageResource(R.drawable.ic_innova_crysta);
                        } else if (documentSnapshot.getString("VehicleName").equals("Force Traveller")) {
                            vehicleimage.setImageResource(R.drawable.ic_force_traveller);
                        } else if (documentSnapshot.getString("VehicleName").equals("Bus")) {
                            vehicleimage.setImageResource(R.drawable.ic_bus);
                        }

//                        if (documentSnapshot.getString("BookingSecure").equals("yes")) {
//                            securetag.setVisibility(View.VISIBLE);
//                        } else {
//                            securetag.setVisibility(View.GONE);
//                        }

                        if (documentSnapshot.getString("ProfileHide").equals("yes")) {
                            hidden.setVisibility(View.VISIBLE);
                            profileview.setVisibility(View.GONE);
                        } else {
                            hidden.setVisibility(View.GONE);
                            profileview.setVisibility(View.VISIBLE);
                        }

                        if (documentSnapshot.getString("PaymentSystem").equals("booking")) {
                            long amt = Long.valueOf(documentSnapshot.getString("PaymentAmount"));
                            long com = 0;
                            if (!documentSnapshot.getString("PaymentCommission").equals("")) {
                                com = Long.valueOf(documentSnapshot.getString("PaymentCommission"));
                            }
                            long earn = amt - com;
                            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                            amount.setText("₹" + numberFormat.format(amt));
                            earning.setText("₹" + numberFormat.format(earn));
                            commision.setText("₹" + numberFormat.format(com));
                            laycommision.setVisibility(View.VISIBLE);
                            layearning.setVisibility(View.VISIBLE);

                            if (documentSnapshot.getString("PaymentNegotiable").equals("yes")) {
                                negotiable.setVisibility(View.VISIBLE);
                            } else {
                                negotiable.setVisibility(View.GONE);
                            }
                        } else {
                            negotiable.setVisibility(View.GONE);
                            laycommision.setVisibility(View.GONE);
                            layearning.setVisibility(View.GONE);
                            amount.setText("Bid Best Price");
                        }

                        String msg = "";
                        if (documentSnapshot.getString("Diesel").equals("yes")) {
                            msg += "only diesel, ";
                        }
                        if (documentSnapshot.getString("Carrier").equals("yes")) {
                            msg += "with carrier, ";
                        }
                        if (documentSnapshot.getString("Extra").equals("include")) {
                            if (!documentSnapshot.getString("Remark").equals("")) {
                                msg += "All inclusive, " + documentSnapshot.getString("Remark");
                            } else {
                                msg += "All inclusive";
                            }
                        } else {
                            if (!documentSnapshot.getString("Remark").equals("")) {
                                msg += "All exclusive, " + documentSnapshot.getString("Remark");
                            } else {
                                msg += "All exclusive";
                            }
                        }
                        remark.setText(msg);

                        if (documentSnapshot.getString("Network").equals("public")) {
                            type.setText("Public Booking");
                        } else {
                            type.setText("Private Booking");
                        }

                        try {
                            avatar.setText(documentSnapshot.getString("SenderName").substring(0, 1).toUpperCase());
                        } catch (Exception e) {
                            avatar.setText("A");
                        }
                        name.setText(documentSnapshot.getString("SenderName"));

                        warring.setVisibility(View.GONE);
                        warring.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                        if (documentSnapshot.getString("Status").equals("open")) {
                            String timestmp1 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
                            if (timestmp1.compareTo(documentSnapshot.getString("StartTimeStamp")) > 0) {
                                warring.setVisibility(View.VISIBLE);
                                warring.setText("*This booking is Expired");
                                warring.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red_color));
                            }
                        } else if (documentSnapshot.getString("Status").equals("assign")) {
                            warring.setVisibility(View.VISIBLE);
                            warring.setText("*This booking is assign to " + documentSnapshot.getString("BookingAssignName"));
                        } else if (documentSnapshot.getString("Status").equals("pickup")) {
                            warring.setVisibility(View.VISIBLE);
                            warring.setText("*This booking is pickup by " + documentSnapshot.getString("BookingAssignName"));
                        } else if (documentSnapshot.getString("Status").equals("complete")) {
                            warring.setText("*This booking is complete by " + documentSnapshot.getString("BookingAssignName"));
                            warring.setVisibility(View.VISIBLE);
                        } else if (documentSnapshot.getString("Status").equals("cancel")) {
                            warring.setText("*This booking is cancelled by "+ documentSnapshot.getString("cancelBy"));
                            warring.setVisibility(View.VISIBLE);
                            warring.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red_color));
                        }

                        review.setText("(0 Reviews)");
                        FirebaseFirestore.getInstance().collection("users")
                                .document(documentSnapshot.getString("SenderMobileNo"))
                                .collection("reviews").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        review.setText("("+queryDocumentSnapshots.size()+" Reviews)");
                                    }
                                });

                        FirebaseFirestore.getInstance().collection("users")
                                .document(documentSnapshot.getString("SenderMobileNo")).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        name.setText(documentSnapshot.getString("UserName"));
                                        if (documentSnapshot.getString("userCompany") != null) {
                                            if (!documentSnapshot.getString("userCompany").equals("")) {
                                                mobile.setVisibility(View.VISIBLE);
                                                mobile.setText(documentSnapshot.getString("userCompany"));
                                            }
                                        }

                                        if (documentSnapshot.getString("UserVerify").equals("yes")) {
                                            verify.setVisibility(View.VISIBLE);
                                        }

                                        try {
                                            Glide.with(getApplicationContext()).load(documentSnapshot.getString("UserProfileImageUri"))
                                                    .into(profileimage);
                                        } catch (Exception e) {
                                        }

                                        double prrat = 0;
                                        if (documentSnapshot.get("userRating") != null) {
                                            prrat = ((Number) documentSnapshot.get("userRating")).doubleValue();
                                        }

                                        if (prrat > 4) {
                                            rat1.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat2.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat3.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat4.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat5.setBackgroundResource(R.drawable.baseline_star_24);
                                        } else if (prrat > 3) {
                                            rat1.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat2.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat3.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat4.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                        } else if (prrat > 2) {
                                            rat1.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat2.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat3.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                        } else if (prrat > 1) {
                                            rat1.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat2.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat3.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                        } else if (prrat > 0) {
                                            rat1.setBackgroundResource(R.drawable.baseline_star_24);
                                            rat2.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat3.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                        } else {
                                            rat1.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat2.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat3.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat4.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                            rat5.setBackgroundResource(R.drawable.baseline_star_outline_24);
                                        }
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(bookingdetail.this, "Your internet is not working.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class recycleradepterchat extends RecyclerView.Adapter<recycleradepterchat.postviewholder> {
        ArrayList<Modelinfo> postmodels;

        public recycleradepterchat(ArrayList<Modelinfo> postmodels) {
            this.postmodels = postmodels;
        }

        @NonNull
        @Override
        public recycleradepterchat.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_customer, parent, false);
            return new recycleradepterchat.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull recycleradepterchat.postviewholder holder, @SuppressLint("RecyclerView") int position) {
            chatno.setVisibility(View.GONE);

            holder.number.setText(postmodels.get(position).getDate() + ", " + postmodels.get(position).getTime());
            if (postmodels.get(position).getSenderMobileNo().equals(profileContainer.userMobileNo)) {
                holder.city.setText("you: " + postmodels.get(position).getDescription());
            } else {
                holder.city.setText(postmodels.get(position).getDescription());
            }
            holder.bookid.setText("Booking Id: " + postmodels.get(position).getBookingId());
            try {
                holder.avatar.setText(postmodels.get(position).getUserName().substring(0, 1).toUpperCase());
            } catch (Exception e) {
                holder.avatar.setText("A");
            }

            holder.detail.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            FirebaseFirestore.getInstance().collection("postBooking")
                    .document(postmodels.get(position).getTransactionId())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                if (documentSnapshot.getString("BookingAssignNo") != null) {
                                    if (documentSnapshot.getString("BookingAssignNo")
                                            .equals(postmodels.get(position).getUserPhoneNumber())) {
                                        holder.detail.setBackgroundTintList(ColorStateList
                                                .valueOf(Color.parseColor("#CBFFCF")));
                                    }
                                }
                            }
                        }
                    });

            holder.name.setText(postmodels.get(position).getUserName());
            FirebaseFirestore.getInstance().collection("users")
                    .document(postmodels.get(position).getUserPhoneNumber()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            holder.name.setText(documentSnapshot.getString("UserName"));
                            if (documentSnapshot.getString("UserVerify") != null) {
                                if (documentSnapshot.getString("UserVerify").equals("yes")) {
                                    holder.verify.setVisibility(View.VISIBLE);
                                }
                            }
                            try {
                                Glide.with(getApplicationContext()).load(documentSnapshot.getString("UserProfileImageUri"))
                                        .into(holder.image);
                            } catch (Exception e) {
                            }

                        }
                    });

            holder.detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                    holder.detail.startAnimation(animation);
                    Intent i = new Intent(getApplicationContext(), bookchatmy.class);
                    i.putExtra("transId", postmodels.get(position).getTransactionId());
                    i.putExtra("custno", postmodels.get(position).getUserPhoneNumber());
                    i.putExtra("custname", postmodels.get(position).getUserName());
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return postmodels.size();
        }

        public class postviewholder extends RecyclerView.ViewHolder {

            ImageView image;
            ConstraintLayout detail;
            TextView name, number, city, avatar, bookid;
            View verify;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                city = itemView.findViewById(R.id.city);
                number = itemView.findViewById(R.id.mobilenumber);
                detail = itemView.findViewById(R.id.detail);
                image = itemView.findViewById(R.id.profileimage);
                avatar = itemView.findViewById(R.id.avatar);
                bookid = itemView.findViewById(R.id.bookingid);
                verify = itemView.findViewById(R.id.view10);
            }
        }

    }
}