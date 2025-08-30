package org.taxivyapar.app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class mynetwork extends AppCompatActivity {
    View back, close;
    CardView addbutton, delete, save;
    EditText name, number, company;
    CheckBox oneway, round, vh1, vh2, vh3, vh4, vh5, vh6, vh7, vh8, vh9, vh10;
    BottomSheetDialog addpostPopup;
    Dialog load;
    ConstraintLayout loading;
    TextView sample;
    RecyclerView recyler;
    recycleradepter adeptro;
    ArrayList<Modelinfo> postmodels;
    String type = "add";
    int pos = 0;
    ArrayList<String> trip;
    ArrayList<String> vehicle;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mynetwork);

        back = findViewById(R.id.view);
        addbutton = findViewById(R.id.login);
        loading = findViewById(R.id.loading);
        sample = findViewById(R.id.sample);
        recyler = findViewById(R.id.recycler);

        trip = new ArrayList<>();
        vehicle = new ArrayList<>();

        recyler.setLayoutManager(new LinearLayoutManager(this));
        postmodels = new ArrayList<>();
        adeptro = new recycleradepter(postmodels);
        recyler.setAdapter(adeptro);

        load = new Dialog(this);
        load.setContentView(R.layout.ui_loading);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        load.setCancelable(false);

        addpostPopup = new BottomSheetDialog(this);
        addpostPopup.setContentView(R.layout.ui_addnetwork);
        View bottomSheetInternal = addpostPopup.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetInternal);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
        name = addpostPopup.findViewById(R.id.name);
        number = addpostPopup.findViewById(R.id.number);
        company = addpostPopup.findViewById(R.id.company);
        oneway = addpostPopup.findViewById(R.id.oneway);
        round = addpostPopup.findViewById(R.id.round);
        vh1 = addpostPopup.findViewById(R.id.vh1);
        vh2 = addpostPopup.findViewById(R.id.vh2);
        vh3 = addpostPopup.findViewById(R.id.vh3);
        vh4 = addpostPopup.findViewById(R.id.vh4);
        vh5 = addpostPopup.findViewById(R.id.vh5);
        vh6 = addpostPopup.findViewById(R.id.vh6);
        vh7 = addpostPopup.findViewById(R.id.vh7);
        vh8 = addpostPopup.findViewById(R.id.vh8);
        vh9 = addpostPopup.findViewById(R.id.vh9);
        vh10 = addpostPopup.findViewById(R.id.vh10);
        delete = addpostPopup.findViewById(R.id.addvehicle);
        save = addpostPopup.findViewById(R.id.addbooking);
        close = addpostPopup.findViewById(R.id.viewmanu);

        initiate();

        oneway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oneway.isChecked()) {
                    if (!trip.contains("oneWay")) {
                        trip.add("oneWay");
                    }
                } else {
                    trip.remove("oneWay");
                }
            }
        });
        round.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oneway.isChecked()) {
                    if (!trip.contains("roundWay")) {
                        trip.add("roundWay");
                    }
                } else {
                    trip.remove("roundWay");
                }
            }
        });

        vh1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh1.isChecked()) {
                    if (!vehicle.contains("Hatchback")) {
                        vehicle.add("Hatchback");
                    }
                } else {
                    vehicle.remove("Hatchback");
                }
            }
        });
        vh2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh2.isChecked()) {
                    if (!vehicle.contains("Sedan")) {
                        vehicle.add("Sedan");
                    }
                } else {
                    vehicle.remove("Sedan");
                }
            }
        });
        vh3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh3.isChecked()) {
                    if (!vehicle.contains("Ertiga")) {
                        vehicle.add("Ertiga");
                    }
                } else {
                    vehicle.remove("Ertiga");
                }
            }
        });
        vh4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh4.isChecked()) {
                    if (!vehicle.contains("Suv")) {
                        vehicle.add("Suv");
                    }
                } else {
                    vehicle.remove("Suv");
                }
            }
        });
        vh9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh9.isChecked()) {
                    if (!vehicle.contains("Kia Cerens")) {
                        vehicle.add("Kia Cerens");
                    }
                } else {
                    vehicle.remove("Kia Cerens");
                }
            }
        });
        vh5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh5.isChecked()) {
                    if (!vehicle.contains("Innova")) {
                        vehicle.add("Innova");
                    }
                } else {
                    vehicle.remove("Innova");
                }
            }
        });
        vh6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh6.isChecked()) {
                    if (!vehicle.contains("Innova Crysta")) {
                        vehicle.add("Innova Crysta");
                    }
                } else {
                    vehicle.remove("Innova Crysta");
                }
            }
        });
        vh10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh10.isChecked()) {
                    if (!vehicle.contains("Innova Hycross")) {
                        vehicle.add("Innova Hycross");
                    }
                } else {
                    vehicle.remove("Innova Hycross");
                }
            }
        });
        vh7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh7.isChecked()) {
                    if (!vehicle.contains("Force Traveller")) {
                        vehicle.add("Force Traveller");
                    }
                } else {
                    vehicle.remove("Force Traveller");
                }
            }
        });
        vh8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh8.isChecked()) {
                    if (!vehicle.contains("Bus")) {
                        vehicle.add("Bus");
                    }
                } else {
                    vehicle.remove("Bus");
                }
            }
        });


        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                addbutton.startAnimation(animation);
                type = "add";
                number.setEnabled(true);
                name.setText("");
                number.setText("");
                company.setText("");
                oneway.setChecked(false);
                round.setChecked(false);
                vh1.setChecked(false);
                vh2.setChecked(false);
                vh3.setChecked(false);
                vh4.setChecked(false);
                vh5.setChecked(false);
                vh6.setChecked(false);
                vh7.setChecked(false);
                vh8.setChecked(false);
                vh9.setChecked(false);
                vh10.setChecked(false);
                addpostPopup.show();
                delete.setVisibility(View.GONE);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                close.startAnimation(animation);
                addpostPopup.dismiss();

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                delete.startAnimation(animation);
                load.show();
                FirebaseFirestore.getInstance().collection("users")
                        .document(profileContainer.userMobileNo).collection("network")
                        .document(type).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                load.dismiss();
                                addpostPopup.dismiss();
                                postmodels.remove(pos);
                                adeptro.notifyItemRemoved(pos);
                                Toast.makeText(mynetwork.this, "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                load.dismiss();
                                Toast.makeText(mynetwork.this, "Your internet is not working.", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                save.startAnimation(animation);
                if (name.getText().toString().trim().isEmpty()) {
                    Toast.makeText(mynetwork.this, "Please enter name", Toast.LENGTH_SHORT).show();
                } else if (number.getText().toString().trim().isEmpty()) {
                    Toast.makeText(mynetwork.this, "Please enter number", Toast.LENGTH_SHORT).show();
                } else if (number.getText().toString().length() != 10) {
                    Toast.makeText(mynetwork.this, "Invalid mobile number", Toast.LENGTH_SHORT).show();
                } else if (company.getText().toString().trim().isEmpty()) {
                    Toast.makeText(mynetwork.this, "Please enter company name", Toast.LENGTH_SHORT).show();
                } else {

                    if (type.equals("add")) {
                        String mob = "+91" + number.getText().toString().trim();
                        String timestmp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
                        Map<String, Object> user = new HashMap<>();
                        user.put("UserName", name.getText().toString().trim());
                        user.put("UserPhoneNumber", mob);
                        user.put("userCompany", company.getText().toString().trim());
                        user.put("userTrip", trip);
                        user.put("userVehicle", vehicle);
                        user.put("TimeStamp", timestmp);

                        load.show();

                        FirebaseFirestore.getInstance().collection("users")
                                .document(profileContainer.userMobileNo).collection("network")
                                .document(mob).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            load.dismiss();
                                            Toast.makeText(mynetwork.this, "Mobile Number is already register.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            FirebaseFirestore.getInstance().collection("users")
                                                    .document(profileContainer.userMobileNo).collection("network")
                                                    .document(mob).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            FirebaseFirestore.getInstance().collection("users")
                                                                    .document(profileContainer.userMobileNo).collection("network")
                                                                    .document(mob).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                            Modelinfo obj = documentSnapshot.toObject(Modelinfo.class);
                                                                            postmodels.add(0, obj);
                                                                            load.dismiss();
                                                                            addpostPopup.dismiss();
                                                                            adeptro.notifyItemInserted(0);
                                                                            recyler.scrollToPosition(0);
                                                                            Toast.makeText(mynetwork.this, "Added", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });

                                                        }
                                                    });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        load.dismiss();
                                        Toast.makeText(mynetwork.this, "Your internet is not working", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Map<String, Object> user = new HashMap<>();
                        user.put("UserName", name.getText().toString().trim());
                        user.put("userCompany", company.getText().toString().trim());
                        user.put("userTrip", trip);
                        user.put("userVehicle", vehicle);

                        load.show();
                        String mob = "+91" + number.getText().toString().trim();
                        FirebaseFirestore.getInstance().collection("users")
                                .document(profileContainer.userMobileNo).collection("network")
                                .document(mob).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        FirebaseFirestore.getInstance().collection("users")
                                                .document(profileContainer.userMobileNo).collection("network")
                                                .document(mob).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        Modelinfo obj = documentSnapshot.toObject(Modelinfo.class);
                                                        postmodels.set(pos, obj);
                                                        load.dismiss();
                                                        addpostPopup.dismiss();
                                                        adeptro.notifyItemChanged(pos);
                                                        Toast.makeText(mynetwork.this, "Updated", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        load.dismiss();
                                        Toast.makeText(mynetwork.this, "Your internet is not working", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                back.startAnimation(animation);
                mynetwork.super.onBackPressed();
            }
        });

    }

    private void initiate() {
        loading.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("users")
                .document(profileContainer.userMobileNo).collection("network")
                .orderBy("TimeStamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        loading.setVisibility(View.GONE);
                        sample.setVisibility(View.VISIBLE);
                        postmodels.clear();
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            Modelinfo obj = d.toObject(Modelinfo.class);
                            postmodels.add(obj);
                        }
                        adeptro.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mynetwork.this, "Your internet is not working", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class recycleradepter extends RecyclerView.Adapter<recycleradepter.postviewholder> {
        ArrayList<Modelinfo> postmodels;

        public recycleradepter(ArrayList<Modelinfo> postmodels) {
            this.postmodels = postmodels;
        }


        @NonNull
        @Override
        public recycleradepter.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_network, parent, false);
            return new recycleradepter.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull recycleradepter.postviewholder holder, @SuppressLint("RecyclerView") int position) {
            sample.setVisibility(View.GONE);
            holder.name.setText(postmodels.get(position).getUserName());
            holder.mobilenumber.setText(postmodels.get(position).getUserPhoneNumber());
            holder.company.setText(postmodels.get(position).getUserCompany());
            holder.invite.setVisibility(View.GONE);
            holder.remark.setText("");
            FirebaseFirestore.getInstance().collection("users")
                    .document(postmodels.get(position).getUserPhoneNumber()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                holder.remark.setText("Member Since, " + documentSnapshot.getString("RegistrationDate"));
                            } else {
                                holder.invite.setVisibility(View.VISIBLE);
                                holder.remark.setText("Invite member in Taxi Vyapar");
                            }
                        }
                    });

            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getAdapterPosition();
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                    holder.more.startAnimation(animation);
                    type = postmodels.get(position).getUserPhoneNumber();
                    name.setText(postmodels.get(position).getUserName());
                    number.setText(postmodels.get(position).getUserPhoneNumber().replace("+91", ""));
                    number.setEnabled(false);
                    company.setText(postmodels.get(position).getUserCompany());
                    trip = postmodels.get(position).getUserTrip();
                    vehicle = postmodels.get(position).getUserVehicle();
                    oneway.setChecked(false);
                    round.setChecked(false);
                    vh1.setChecked(false);
                    vh2.setChecked(false);
                    vh3.setChecked(false);
                    vh4.setChecked(false);
                    vh5.setChecked(false);
                    vh6.setChecked(false);
                    vh7.setChecked(false);
                    vh8.setChecked(false);
                    vh9.setChecked(false);
                    vh10.setChecked(false);
                    if (trip.contains("oneWay")) {
                        oneway.setChecked(true);
                    }
                    if (trip.contains("roundWay")) {
                        round.setChecked(true);
                    }
                    if (vehicle.contains("Hatchback")) {
                        vh1.setChecked(true);
                    }
                    if (vehicle.contains("Sedan")) {
                        vh2.setChecked(true);
                    }
                    if (vehicle.contains("Ertiga")) {
                        vh3.setChecked(true);
                    }
                    if (vehicle.contains("Suv")) {
                        vh4.setChecked(true);
                    }
                    if (vehicle.contains("Kia Cerens")) {
                        vh9.setChecked(true);
                    }
                    if (vehicle.contains("Innova")) {
                        vh5.setChecked(true);
                    }
                    if (vehicle.contains("Innova Crysta")) {
                        vh6.setChecked(true);
                    }
                    if (vehicle.contains("Innova Hycross")) {
                        vh10.setChecked(true);
                    }
                    if (vehicle.contains("Force Traveller")) {
                        vh7.setChecked(true);
                    }
                    if (vehicle.contains("Bus")) {
                        vh8.setChecked(true);
                    }
                    pos = position;
                    delete.setVisibility(View.VISIBLE);
                    addpostPopup.show();
                }
            });
            holder.invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                    holder.invite.startAnimation(animation);
                }
            });

        }

        @Override
        public int getItemCount() {
            return postmodels.size();
        }

        public class postviewholder extends RecyclerView.ViewHolder {

            View more, invite;
            TextView name, mobilenumber, company, remark;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                remark = itemView.findViewById(R.id.remark);
                name = itemView.findViewById(R.id.name);
                more = itemView.findViewById(R.id.more);
                invite = itemView.findViewById(R.id.invite);
                mobilenumber = itemView.findViewById(R.id.mobilenumber);
                company = itemView.findViewById(R.id.company);

            }
        }

    }
}