package org.taxivyapar.app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class managedriver extends AppCompatActivity {
    View back;
    CardView addbutton;
    TextView sample;
    ConstraintLayout loading;
    Dialog load, deletepopup;
    RecyclerView recyler;
    recycleradepter adeptro;
    ArrayList<Modelinfo> postmodels;
    ConstraintLayout yesdelete, nodelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managedriver);

        back = findViewById(R.id.view);
        addbutton = findViewById(R.id.login);
        loading = findViewById(R.id.loading);

        sample = findViewById(R.id.sample);
        recyler = findViewById(R.id.recycler);

        recyler.setLayoutManager(new LinearLayoutManager(this));
        postmodels = new ArrayList<>();
        adeptro = new recycleradepter(postmodels);
        recyler.setAdapter(adeptro);

        load = new Dialog(this);
        load.setContentView(R.layout.ui_loading);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        load.setCancelable(false);

        deletepopup = new Dialog(this);
        deletepopup.setContentView(R.layout.popup_driver_delete);
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
                FirebaseFirestore.getInstance().collection("drivers")
                        .document(profileContainer.productId).delete();
                deletepopup.dismiss();
                Toast.makeText(managedriver.this, "Deleted successfully.", Toast.LENGTH_SHORT).show();
                initiate();

            }
        });

        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                addbutton.startAnimation(animation);
                startActivity(new Intent(getApplicationContext(), adddriver.class));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                back.startAnimation(animation);
                managedriver.super.onBackPressed();
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
        FirebaseFirestore.getInstance().collection("drivers")
                .whereEqualTo("UserPhoneNumber", profileContainer.userMobileNo)
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
                        Toast.makeText(managedriver.this, "Your internet is not working", Toast.LENGTH_SHORT).show();
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
                    .inflate(R.layout.list_driver, parent, false);
            return new recycleradepter.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull recycleradepter.postviewholder holder, @SuppressLint("RecyclerView") int position) {
            sample.setVisibility(View.GONE);
            holder.name.setText(postmodels.get(position).getSenderName());
            holder.number.setText(postmodels.get(position).getSenderMobileNo());
            holder.city.setText(postmodels.get(position).getAddressCity());
            try {
                holder.avatar.setText(postmodels.get(position).getSenderName().substring(0, 1).toUpperCase());
            } catch (Exception e) {
                holder.avatar.setText("A");
            }
            Glide.with(getApplicationContext()).load(postmodels.get(position).getAttachment1()).into(holder.image);

            holder.detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                    holder.detail.startAnimation(animation);
                    profileContainer.productId = postmodels.get(position).getSenderMobileNo();
                    startActivity(new Intent(getApplicationContext(), driverdetail.class));
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

            View delete,detail;
            ImageView image;
            TextView name, number, city, avatar;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                city = itemView.findViewById(R.id.city);
                number = itemView.findViewById(R.id.mobilenumber);
                delete = itemView.findViewById(R.id.delete);
                image = itemView.findViewById(R.id.profileimage);
                avatar = itemView.findViewById(R.id.avatar);
                detail = itemView.findViewById(R.id.more);

            }
        }

    }
}