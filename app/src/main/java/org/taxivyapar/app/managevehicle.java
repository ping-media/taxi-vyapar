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
import android.widget.ImageView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.content.res.ColorStateList;
import androidx.core.content.ContextCompat;

public class managevehicle extends AppCompatActivity {
    View back;
    CardView addbutton;
    ConstraintLayout loading;
    TextView sample;
    RecyclerView recyler;
    recycleradepter adeptro;
    ArrayList<Modelinfo> postmodels;
    Dialog deletepopup;
    ConstraintLayout yesdelete, nodelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managevehicle);

        back = findViewById(R.id.view);
        addbutton = findViewById(R.id.login);
        loading = findViewById(R.id.loading);

        sample = findViewById(R.id.sample);
        recyler = findViewById(R.id.recycler);

        recyler.setLayoutManager(new LinearLayoutManager(this));
        postmodels = new ArrayList<>();
        adeptro = new recycleradepter(postmodels);
        recyler.setAdapter(adeptro);

        deletepopup = new Dialog(this);
        deletepopup.setContentView(R.layout.popup_vehicle_delete);
        deletepopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        yesdelete = deletepopup.findViewById(R.id.yeslogout);
        nodelete = deletepopup.findViewById(R.id.nologout);

        initiate();

        nodelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletepopup.dismiss();
            }
        });
        yesdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore.getInstance().collection("users")
                        .document(profileContainer.userMobileNo).collection("vehicles")
                        .document(profileContainer.productId).delete();
                deletepopup.dismiss();
                Toast.makeText(managevehicle.this, "Deleted successfully.", Toast.LENGTH_SHORT).show();
                initiate();
            }
        });

        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                addbutton.startAnimation(animation);
                startActivity(new Intent(getApplicationContext(), addvehicle.class));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                back.startAnimation(animation);
                managevehicle.super.onBackPressed();
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initiate();
    }

    private void initiate() {
        loading.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("users")
                .document(profileContainer.userMobileNo).collection("vehicles")
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
                        Toast.makeText(managevehicle.this, "Your internet is not working", Toast.LENGTH_SHORT).show();
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
                    .inflate(R.layout.list_vehicle, parent, false);
            return new recycleradepter.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull recycleradepter.postviewholder holder, @SuppressLint("RecyclerView") int position) {
            sample.setVisibility(View.GONE);
            holder.vehno.setText(postmodels.get(position).getVehicleNumber());
            holder.vehyear.setText(postmodels.get(position).getVehicleYear());
            holder.vehname.setText(postmodels.get(position).getVehicleName());
            
            // Check insurance expiry and set color
            String insuranceExpiry = postmodels.get(position).getInsuranceExpiry();
            if (insuranceExpiry != null && !insuranceExpiry.isEmpty()) {
                try {
                    // Parse the date format "01 Jan, 2025"
                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
                    Date expiryDate = inputFormat.parse(insuranceExpiry);
                    Date currentDate = new Date();
                    
                    // If insurance is expired, show red color
                    if (expiryDate != null && expiryDate.before(currentDate)) {
                        holder.vehno.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red_color));
                    } else {
                        holder.vehno.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black_color));
                    }
                } catch (ParseException e) {
                    // If parsing fails, keep default color
                    holder.vehno.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black_color));
                }
            } else {
                // If no insurance expiry date, keep default color
                holder.vehno.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black_color));
            }
            
            String image = "";
            if (!postmodels.get(position).getAttachment1().equals("")) {
                image = postmodels.get(position).getAttachment1();
            } else if (!postmodels.get(position).getAttachment2().equals("")) {
                image = postmodels.get(position).getAttachment2();
            } else if (!postmodels.get(position).getAttachment3().equals("")) {
                image = postmodels.get(position).getAttachment3();
            } else if (!postmodels.get(position).getAttachment4().equals("")) {
                image = postmodels.get(position).getAttachment4();
            } else if (!postmodels.get(position).getAttachment5().equals("")) {
                image = postmodels.get(position).getAttachment5();
            }
            Glide.with(getApplicationContext()).load(image).into(holder.image);

            holder.detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                    holder.detail.startAnimation(animation);
                    profileContainer.productId = postmodels.get(position).getId();
                    startActivity(new Intent(getApplicationContext(), vehicledetail.class));
                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                    holder.delete.startAnimation(animation);
                    profileContainer.productId = postmodels.get(position).getId();
                    deletepopup.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return postmodels.size();
        }

        public class postviewholder extends RecyclerView.ViewHolder {

            View delete, detail;
            ImageView image;
            TextView vehno, vehyear, vehname;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                detail = itemView.findViewById(R.id.more);
                vehno = itemView.findViewById(R.id.name);
                vehyear = itemView.findViewById(R.id.city);
                vehname = itemView.findViewById(R.id.mobilenumber);
                delete = itemView.findViewById(R.id.delete);
                image = itemView.findViewById(R.id.profileimage);

            }
        }

    }
}