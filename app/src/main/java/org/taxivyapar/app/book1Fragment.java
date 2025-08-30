package org.taxivyapar.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class book1Fragment extends Fragment {
    View view;
    ConstraintLayout barloading;
    EditText bookid;
    TextView sample;
    SwipeRefreshLayout homerefresh;
    NestedScrollView nested;
    RecyclerView homepostscreen;
    recycleradepter adeptro;
    ArrayList<ModelmyBook> postmodels, data, modelpass;
    BottomSheetDialog chatpopup;
    Dialog loading;
    View closechat;
    RecyclerView recyclerchat;
    recycleradepterchat adeptrochat;
    ArrayList<Modelinfo> modelchat;
    TextView chatno;
    ListenerRegistration transactionsrealtime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_book1, container, false);

        barloading = view.findViewById(R.id.loading);

        nested = view.findViewById(R.id.nested);

        sample = view.findViewById(R.id.sample);

        bookid = view.findViewById(R.id.searchcustomer);

        homerefresh = view.findViewById(R.id.refresher);

        homepostscreen = view.findViewById(R.id.recyclercutomer);
        homepostscreen.setLayoutManager(new LinearLayoutManager(getContext()));
        postmodels = new ArrayList<>();
        data = new ArrayList<>();
        modelpass = new ArrayList<>();
        adeptro = new recycleradepter(postmodels);
        homepostscreen.setAdapter(adeptro);

        loading = new Dialog(getContext());
        loading.setContentView(R.layout.ui_loading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);

        chatpopup = new BottomSheetDialog(getContext());
        chatpopup.setContentView(R.layout.ui_listchat);
        closechat = chatpopup.findViewById(R.id.viewmanu);
        chatno = chatpopup.findViewById(R.id.sample);
        recyclerchat = chatpopup.findViewById(R.id.recyclercutomer);

        recyclerchat.setLayoutManager(new LinearLayoutManager(getContext()));
        modelchat = new ArrayList<>();
        adeptrochat = new recycleradepterchat(modelchat);
        recyclerchat.setAdapter(adeptrochat);

        initial();

        homerefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initial();
                homerefresh.setRefreshing(false);
            }
        });

        nested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (postmodels.size() < modelpass.size()) {
                        Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
                        int run = 50;
                        int siz = postmodels.size();
                        if ((modelpass.size() - postmodels.size()) < 50) {
                            run = modelpass.size() - postmodels.size();
                        }
                        int k = postmodels.size() + run;
                        for (int i = postmodels.size(); i < k; i++) {
                            postmodels.add(modelpass.get(i));

                        }
                        adeptro.notifyItemRangeInserted(siz, k);
                    }
                }
            }
        });

        closechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                closechat.startAnimation(animation);
                chatpopup.dismiss();
            }
        });

        bookid.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {

                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(bookid.getWindowToken(), 0);

                    progress();

                    return true;
                }
                return false;
            }
        });

        return view;
    }

//    @Override
//    public void onStop() {
//        try {
//            transactionsrealtime.remove();
//        } catch (Exception e) {
//
//        }
//        super.onStop();
//    }
//
//    @Override
//    public void onDestroy() {
//        try {
//            transactionsrealtime.remove();
//        } catch (Exception e) {
//
//        }
//        super.onDestroy();
//
//    }

    @Override
    public void onResume() {
        super.onResume();
        initial();
    }

    private void initial() {
        barloading.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("users")
                .document(profileContainer.userMobileNo).collection("postBooking")
                .orderBy("TimeStamp", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot value) {
                        barloading.setVisibility(View.GONE);
                        data.clear();
                        List<DocumentSnapshot> list = value.getDocuments();
                        for (DocumentSnapshot d : list) {
                            ModelmyBook obj = d.toObject(ModelmyBook.class);
                            data.add(obj);
                        }
                        progress();
                    }
                });
//        try {
//            transactionsrealtime = FirebaseFirestore.getInstance().collection("users")
//                    .document(profileContainer.userMobileNo).collection("postBooking")
//                    .orderBy("TimeStamp", Query.Direction.DESCENDING)
//                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                            barloading.setVisibility(View.GONE);
//                            data.clear();
//                            List<DocumentSnapshot> list = value.getDocuments();
//                            for (DocumentSnapshot d : list) {
//                                ModelpostBook obj = d.toObject(ModelpostBook.class);
//                                data.add(obj);
//                            }
//                            progress();
//                        }
//                    });
//        } catch (Exception e) {
//
//        }

    }

    public void progress() {
        modelpass.clear();
        sample.setVisibility(View.VISIBLE);
        for (ModelmyBook doc : data) {
            if (bookid.getText().toString().trim().isEmpty()) {
                modelpass.add(doc);
            } else {
                if (doc.getBookingId().contains(bookid.getText().toString().trim())) {
                    modelpass.add(doc);
                }
            }
        }
        postmodels.clear();
        int run = 50;
        if (modelpass.size() < 50) {
            run = modelpass.size();
        }
        for (int i = 0; i < run; i++) {
            postmodels.add(modelpass.get(i));
        }
        adeptro.notifyDataSetChanged();
    }

    public void sendmsg(String custno, String custname, String msg, String str_bkid, String transId) {
        String timestmp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
        String id = profileContainer.userMobileNo + "_" + timestmp;
        String currentDate = new SimpleDateFormat("dd MMM, yyyy", Locale.CANADA).format(new Date());
        String ho = new SimpleDateFormat("KK", Locale.CANADA).format(new Date());
        String min = new SimpleDateFormat("mm", Locale.CANADA).format(new Date());
        String AM = new SimpleDateFormat("a", Locale.CANADA).format(new Date());
        String currentTime;
        if (ho.equals("00")) {
            currentTime = "12" + ":" + min + " " + AM;
        } else {
            currentTime = ho + ":" + min + " " + AM;
        }
        Map<String, Object> user = new HashMap<>();
        user.put("SenderMobileNo", profileContainer.userMobileNo);
        user.put("ReceiverMobileNo", custno);
        user.put("Description", msg);
        user.put("Date", currentDate);
        user.put("Time", currentTime);
        user.put("TimeStamp", timestmp);
        user.put("Attachment1", "");
        user.put("Attachment2", "");
        user.put("Attachment3", "");
        user.put("Attachment4", "");
        user.put("Attachment5", "");
        user.put("BookingId", str_bkid);
        user.put("SenderName", profileContainer.userName);
        user.put("ReceiverName", custname);
        user.put("TransactionId", transId);
        user.put("Id", id);

        Map<String, Object> user1 = new HashMap<>();
        user1.put("SenderMobileNo", profileContainer.userMobileNo);
        user1.put("SenderName", profileContainer.userName);
        user1.put("UserName", custname);
        user1.put("UserPhoneNumber", custno);
        user1.put("UserProfileImageUri", "");
        user1.put("Description", msg);
        user1.put("Date", currentDate);
        user1.put("Time", currentTime);
        user1.put("TimeStamp", timestmp);
        user1.put("BookingId", str_bkid);
        user1.put("Attachment1", "");
        user1.put("Attachment2", "");
        user1.put("Attachment3", "");
        user1.put("Attachment4", "");
        user1.put("Attachment5", "");
        user1.put("TransactionId", transId);
        user1.put("Id", id);

        Map<String, Object> user2 = new HashMap<>();
        user2.put("SenderMobileNo", profileContainer.userMobileNo);
        user2.put("SenderName", profileContainer.userName);
        user2.put("UserName", profileContainer.userName);
        user2.put("UserPhoneNumber", profileContainer.userMobileNo);
        user2.put("UserProfileImageUri", profileContainer.userProfileImageUrl);
        user2.put("Description", msg);
        user2.put("Date", currentDate);
        user2.put("Time", currentTime);
        user2.put("TimeStamp", timestmp);
        user2.put("BookingId", str_bkid);
        user2.put("Attachment1", "");
        user2.put("Attachment2", "");
        user2.put("Attachment3", "");
        user2.put("Attachment4", "");
        user2.put("Attachment5", "");
        user2.put("TransactionId", transId);
        user2.put("Id", id);

        FirebaseFirestore.getInstance().collection("postBooking")
                .document(transId).collection("message").document(custno)
                .collection("transection").document(id).set(user);

        FirebaseFirestore.getInstance().collection("message").document(id)
                .set(user);

        FirebaseFirestore.getInstance().collection("postBooking")
                .document(transId).collection("message").document(custno)
                .set(user1);

        FirebaseFirestore.getInstance().collection("userChat")
                .document(profileContainer.userMobileNo).collection("chatPost")
                .document(custno + "_" + str_bkid)
                .set(user1);

        FirebaseFirestore.getInstance().collection("userChat")
                .document(custno).collection("chatReceive")
                .document(profileContainer.userMobileNo + "_" + str_bkid)
                .set(user2);

        try {
            JSONObject body = new JSONObject();
            body.put("app_id", profileContainer.oneSignal);

            JSONArray externalUserIds = new JSONArray();
            externalUserIds.put(custno.replace("+91", ""));
            body.put("include_external_user_ids", externalUserIds);

            JSONObject headings = new JSONObject();
            headings.put("en", "Message From " + profileContainer.userName);
            body.put("headings", headings);

            JSONObject contents = new JSONObject();
            contents.put("en", msg);
            body.put("contents", contents);

            body.put("small_icon", "logo");
            body.put("android_channel_id", profileContainer.oneSignalChannelMsg);

            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(body,
                    getContext(), getActivity());
            notificationsSender.SendNotifications();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public class recycleradepter extends RecyclerView.Adapter<recycleradepter.postviewholder> {
        ArrayList<ModelmyBook> postmodels;

        public recycleradepter(ArrayList<ModelmyBook> postmodels) {
            this.postmodels = postmodels;
        }


        @NonNull
        @Override
        public recycleradepter.postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_booking_post, parent, false);
            return new recycleradepter.postviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull recycleradepter.postviewholder holder, @SuppressLint("RecyclerView") int position) {
            sample.setVisibility(View.GONE);
            holder.bookingid.setText("ID: " + postmodels.get(position).getBookingId()
                    + " (" + postmodels.get(position).getBookingPlatform() + ")");

            String date = postmodels.get(position).getStartTimeStamp().substring(0, 10);

            String timestmp = new SimpleDateFormat("yyyy_MM_dd", Locale.CANADA).format(new Date());

            if (date.equals(timestmp)) {
                holder.startdate.setText("Today, " + postmodels.get(position).getStartTime());
            } else {
                holder.startdate.setText(postmodels.get(position).getStartDate()
                        + ", " + postmodels.get(position).getStartTime());
            }

            holder.vehiclename.setText(postmodels.get(position).getVehicleName());

            if (postmodels.get(position).getVehicleName().equals("Hatchback")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_hatchback);
            } else if (postmodels.get(position).getVehicleName().equals("Sedan")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_sedan);
            } else if (postmodels.get(position).getVehicleName().equals("Ertiga")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_eartiga);
            } else if (postmodels.get(position).getVehicleName().equals("Suv")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_suv);
            } else if (postmodels.get(position).getVehicleName().equals("Kia Cerens")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_kia);
            } else if (postmodels.get(position).getVehicleName().equals("Innova")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_innova);
            } else if (postmodels.get(position).getVehicleName().equals("Innova Crysta")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_innova_crysta);
            } else if (postmodels.get(position).getVehicleName().equals("Innova Hycross")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_innova_crysta);
            } else if (postmodels.get(position).getVehicleName().equals("Force Traveller")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_force_traveller);
            } else if (postmodels.get(position).getVehicleName().equals("Bus")) {
                holder.vehicleimage.setImageResource(R.drawable.ic_bus);
            }

            if (postmodels.get(position).getProfileHide().equals("yes")) {
                holder.hidden.setVisibility(View.VISIBLE);
            } else {
                holder.hidden.setVisibility(View.GONE);
            }

            if (postmodels.get(position).getPaymentSystem().equals("booking")) {
                long amt = Long.valueOf(postmodels.get(position).getPaymentAmount());
                long com = 0;
                if (!postmodels.get(position).getPaymentCommission().equals("")) {
                    com = Long.valueOf(postmodels.get(position).getPaymentCommission());
                }
                NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                holder.amount.setText("₹" + numberFormat.format(amt));
                holder.commision.setText("₹" + numberFormat.format(com));
                holder.laycommision.setVisibility(View.VISIBLE);

                if (postmodels.get(position).getPaymentNegotiable().equals("yes")) {
                    holder.negotiable.setVisibility(View.VISIBLE);
                } else {
                    holder.negotiable.setVisibility(View.GONE);
                }
            } else {
                holder.negotiable.setVisibility(View.GONE);
                holder.laycommision.setVisibility(View.GONE);
                holder.amount.setText("Bid Best Price");
            }
            holder.location.setText(postmodels.get(position).getAddressCity());
            holder.droplocation.setText(postmodels.get(position).getDropAddressCity());

            if (postmodels.get(position).getBookingType().equals("oneWay")) {
                holder.trip.setText("One Way");
            } else {
                holder.trip.setText("Round Trip");
            }

            String msg = "";
            if (postmodels.get(position).getDiesel().equals("yes")) {
                msg += "only diesel, ";
            }
            if (postmodels.get(position).getCarrier().equals("yes")) {
                msg += "with carrier, ";
            }
            if (postmodels.get(position).getExtra().equals("include")) {
                if (!postmodels.get(position).getRemark().equals("")) {
                    msg += "All inclusive, " + postmodels.get(position).getRemark();
                } else {
                    msg += "All inclusive";
                }
            } else {
                if (!postmodels.get(position).getRemark().equals("")) {
                    msg += "All exclusive, " + postmodels.get(position).getRemark();
                } else {
                    msg += "All exclusive";
                }
            }
            holder.remark.setText(msg);

            if(postmodels.get(position).getBookingAssignNo()!=null){
                if(postmodels.get(position).getCommissionRequest()!=null){
                    if (postmodels.get(position).getCommissionRequest().contains(postmodels.get(position).getBookingAssignNo())) {

                        int ind = postmodels.get(position).getCommissionRequest().indexOf(postmodels.get(position).getBookingAssignNo());
                        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                        holder.amount.setText("₹" + numberFormat.format(Double.valueOf(postmodels.get(position).getCommissionAmount().get(ind))));
                        holder.commision.setText("₹" + numberFormat.format(Double.valueOf(postmodels.get(position).getCommissionPay().get(ind))));
                        holder.laycommision.setVisibility(View.VISIBLE);

                        holder.negotiable.setVisibility(View.GONE);
                    }
                }

            }

            holder.company = "";
            FirebaseFirestore.getInstance().collection("users")
                    .document(postmodels.get(position).getSenderMobileNo()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            holder.company = documentSnapshot.getString("UserName");
                            if (documentSnapshot.getString("userCompany") != null) {
                                if (!documentSnapshot.getString("userCompany").equals("")) {
                                    holder.company = documentSnapshot.getString("userCompany");
                                }
                            }
                        }
                    });

            holder.edit.setVisibility(View.VISIBLE);
            holder.chat.setVisibility(View.VISIBLE);
            holder.share.setVisibility(View.VISIBLE);
            holder.pickup.setVisibility(View.VISIBLE);
            holder.cancel.setVisibility(View.VISIBLE);
            holder.bookingid.setTextColor(ContextCompat.getColor(getContext(), R.color.black_color));
            if (postmodels.get(position).getStatus().equals("open")) {
                holder.pickup.setVisibility(View.GONE);
                holder.bookingid.setText("ID:" + postmodels.get(position).getBookingId() + " (Open)");
                String timestmp1 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
                if (timestmp1.compareTo(postmodels.get(position).getStartTimeStamp()) > 0) {
                    holder.bookingid.setText("ID:" + postmodels.get(position).getBookingId() + " (Expire)");
                    holder.bookingid.setTextColor(ContextCompat.getColor(getContext(), R.color.red_color));
                    holder.edit.setVisibility(View.GONE);
                    holder.cancel.setVisibility(View.GONE);
                }
            } else if (postmodels.get(position).getStatus().equals("assign")) {
                holder.bookingid.setText("ID:" + postmodels.get(position).getBookingId() + " (Assign)");
                holder.edit.setVisibility(View.GONE);
                holder.pickup.setVisibility(View.GONE);
                String timestmp1 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA).format(new Date());
                if (timestmp1.compareTo(postmodels.get(position).getStartTimeStamp()) > 0) {
                    holder.pickup.setVisibility(View.VISIBLE);
                }
            } else if (postmodels.get(position).getStatus().equals("pickup")) {
                holder.bookingid.setText("ID:" + postmodels.get(position).getBookingId() + " (Picked)");
                holder.edit.setVisibility(View.GONE);
                holder.pickup.setVisibility(View.GONE);
            } else if (postmodels.get(position).getStatus().equals("complete")) {
                holder.bookingid.setText("ID:" + postmodels.get(position).getBookingId() + " (Complete)");
                holder.edit.setVisibility(View.GONE);
                holder.pickup.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.GONE);
            } else if (postmodels.get(position).getStatus().equals("cancel")) {
                holder.bookingid.setText("ID:" + postmodels.get(position).getBookingId() + " (Cancel)");
                holder.edit.setVisibility(View.GONE);
                holder.pickup.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.GONE);
                holder.bookingid.setTextColor(ContextCompat.getColor(getContext(), R.color.red_color));
            }
            FirebaseFirestore.getInstance().collection("postBooking")
                    .document(postmodels.get(position).getTransactionId())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot.getString("Status").equals("complete")) {
                                if (documentSnapshot.getString("ReviewAgent") == null) {
                                }
                            }

                            if (documentSnapshot.getString("BookingAssignNo") != null) {
                                holder.custname = documentSnapshot.getString("BookingAssignName");
                                holder.custno = documentSnapshot.getString("BookingAssignNo");
                            }
                        }
                    });


            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                    holder.cancel.startAnimation(animation);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Cancel Booking");
                    builder.setMessage("Are you sure, you want to cancel the booking?");
                    builder.create();
                    builder.setPositiveButton("Cancel Booking", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            loading.show();
                            FirebaseFirestore.getInstance().collection("postBooking")
                                    .document(postmodels.get(position).getTransactionId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            loading.dismiss();

                                            if (documentSnapshot.getString("Status").equals("assign")
                                                    || documentSnapshot.getString("Status").equals("open")
                                                    || documentSnapshot.getString("Status").equals("pickup")) {

                                                Map<String, Object> updateBooking = new HashMap<>();
                                                updateBooking.put("Status", "cancel");

                                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                                db.collection("postBooking")
                                                        .document(postmodels.get(position).getTransactionId()).update(updateBooking);

                                                db.collection("users").document(postmodels.get(position).getSenderMobileNo())
                                                        .collection("postBooking")
                                                        .document(postmodels.get(position).getTransactionId()).update(updateBooking);

                                                Map<String, Object> updateTransaction = new HashMap<>();
                                                updateTransaction.put("Status", "refund");

                                                Map<String, Object> updateTransaction1 = new HashMap<>();
                                                updateTransaction1.put("Status", "cancel");

                                                if (documentSnapshot.getString("BookingAssignNo") != null) {
                                                    db.collection("users")
                                                            .document(documentSnapshot.getString("BookingAssignNo"))
                                                            .collection("transaction")
                                                            .document(documentSnapshot.getString("BookingAssignPayId"))
                                                            .update(updateTransaction);

                                                    db.collection("transaction")
                                                            .document(documentSnapshot.getString("BookingAssignPayId"))
                                                            .update(updateTransaction);

                                                    db.collection("users")
                                                            .document(profileContainer.userMobileNo)
                                                            .collection("transaction")
                                                            .document(documentSnapshot.getString("BookingAssignPayId"))
                                                            .update(updateTransaction1);

                                                    db.runTransaction(new Transaction.Function<Void>() {
                                                        @Override
                                                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                                            DocumentReference userRef = db.collection("users")
                                                                    .document(documentSnapshot.getString("BookingAssignNo"));
                                                            DocumentSnapshot snapshot = transaction.get(userRef);

                                                            double currentWallet = 0.0;
                                                            if (snapshot.contains("userWallet") && snapshot.getDouble("userWallet") != null) {
                                                                currentWallet = snapshot.getDouble("userWallet");
                                                            }

                                                            ArrayList<String> commissionRequest = (ArrayList<String>) documentSnapshot.get("CommissionRequest");
                                                            ArrayList<String> CommissionPay = (ArrayList<String>) documentSnapshot.get("CommissionPay");

                                                            if (commissionRequest.contains(documentSnapshot.getString("BookingAssignNo"))) {
                                                                int ind = commissionRequest.indexOf(documentSnapshot.getString("BookingAssignNo"));
                                                                double commissionValue = Double.parseDouble(CommissionPay.get(ind));
                                                                transaction.update(userRef, "userWallet", currentWallet + commissionValue);
                                                            }

                                                            sendmsg(documentSnapshot.getString("BookingAssignNo"), documentSnapshot.getString("BookingAssignName"),
                                                                    "Booking is Cancelled by Agent", postmodels.get(position).getBookingId(),
                                                                    postmodels.get(position).getTransactionId());

                                                            return null;
                                                        }
                                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            initial();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                                } else {
                                                    initial();
                                                }

                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loading.dismiss();
                                            Toast.makeText(getContext(), "Your internet is not working.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                    builder.show();
                }
            });

            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                    holder.share.startAnimation(animation);
                    String msg = "Booking Id: " + postmodels.get(position).getBookingId()
                            + "\nPickup Date & Time: " + postmodels.get(position).getStartDate() + " " + postmodels.get(position).getStartTime()
                            + "\nPickup Location: " + postmodels.get(position).getAddress()
                            + "\nDrop Location: " + postmodels.get(position).getDropAddress()
                            + "\nVehicle: " + postmodels.get(position).getVehicleName();
                    if (postmodels.get(position).getBookingType().equals("oneWay")) {
                        msg += "\nBooking Type: One Way";
                    } else {
                        msg += "\nBooking Type: Round Trip";
                    }

                    if (postmodels.get(position).getPaymentSystem().equals("booking")) {
                        long amt = Long.valueOf(postmodels.get(position).getPaymentAmount());
                        long com = 0;
                        if (!postmodels.get(position).getPaymentCommission().equals("")) {
                            com = Long.valueOf(postmodels.get(position).getPaymentCommission());
                        }
                        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
                        msg += "\nBooking Amount: ₹" + numberFormat.format(amt);
                        msg += "\nBooking Commission: ₹" + numberFormat.format(com);
                    } else {
                        msg += "\nBooking Amount: Bid Best Price";
                    }
                    msg += "\nBooking Posted By: " + holder.company;
                    msg += "\nContact Number: " + postmodels.get(position).getSenderMobileNo();
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                }
            });
            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                    holder.chat.startAnimation(animation);

                    loading.show();
                    FirebaseFirestore.getInstance().collection("postBooking")
                            .document(postmodels.get(position).getTransactionId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.getString("BookingAssignNo") != null) {
                                        loading.dismiss();
                                        Intent i = new Intent(getContext(), bookchatmy.class);
                                        i.putExtra("transId", postmodels.get(position).getTransactionId());
                                        i.putExtra("custno", documentSnapshot.getString("BookingAssignNo"));
                                        i.putExtra("custname", documentSnapshot.getString("BookingAssignName"));
                                        startActivity(i);
                                    } else {
                                        FirebaseFirestore.getInstance().collection("postBooking")
                                                .document(postmodels.get(position).getTransactionId())
                                                .collection("message")
                                                .orderBy("TimeStamp", Query.Direction.DESCENDING)
                                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        loading.dismiss();
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
                                    loading.dismiss();
                                    Toast.makeText(getContext(), "Your internet is not working.", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    profileContainer.productId = postmodels.get(position).getTransactionId();
                    Intent i = new Intent(getContext(), bookingdetail.class);
                    i.putExtra("transId", postmodels.get(position).getTransactionId());
                    i.putExtra("custno", postmodels.get(position).getSenderMobileNo());
                    i.putExtra("custname", postmodels.get(position).getSenderName());
                    startActivity(i);
                }
            });

            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                    holder.edit.startAnimation(animation);
                    profileContainer.productId = postmodels.get(position).getTransactionId();
                    startActivity(new Intent(getContext(), editbooking.class));
                }
            });

            holder.pickup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                    holder.pickup.startAnimation(animation);

                    loading.show();
                    FirebaseFirestore.getInstance().collection("postBooking")
                            .document(postmodels.get(position).getTransactionId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    loading.dismiss();
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("Status", "pickup");
                                    FirebaseFirestore.getInstance().collection("postBooking")
                                            .document(postmodels.get(position).getTransactionId()).update(user);
                                    FirebaseFirestore.getInstance().collection("users").document(profileContainer.userMobileNo)
                                            .collection("postBooking")
                                            .document(postmodels.get(position).getTransactionId()).update(user);

                                    sendmsg(documentSnapshot.getString("BookingAssignNo"), documentSnapshot.getString("BookingAssignName"),
                                            "Booking is Pickup by Agent", postmodels.get(position).getBookingId(),
                                            postmodels.get(position).getTransactionId());

                                    initial();


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading.dismiss();
                                    Toast.makeText(getContext(), "Your internet is not working.", Toast.LENGTH_SHORT).show();
                                }
                            });


                }
            });
        }

        @Override
        public int getItemCount() {
            return postmodels.size();
        }

        public class postviewholder extends RecyclerView.ViewHolder {
            TextView vehiclename, startdate, bookingid, trip, location, droplocation, remark, negotiable,
                    amount, commision;
            ImageView vehicleimage;
            ConstraintLayout cancel, chat, edit, share, pickup;
            LinearLayout laycommision;
            CardView hidden;
            String company, custno, custname;

            public postviewholder(@NonNull View itemView) {
                super(itemView);
                vehiclename = itemView.findViewById(R.id.vehiclename);
                vehicleimage = itemView.findViewById(R.id.vehicleimage);
                startdate = itemView.findViewById(R.id.startdate);
                location = itemView.findViewById(R.id.location);
                remark = itemView.findViewById(R.id.remark);
                cancel = itemView.findViewById(R.id.cancel);
                chat = itemView.findViewById(R.id.chat);
                bookingid = itemView.findViewById(R.id.bookingid);
                trip = itemView.findViewById(R.id.trip);
                droplocation = itemView.findViewById(R.id.droplocation);
                amount = itemView.findViewById(R.id.amount);
                commision = itemView.findViewById(R.id.commision);
                hidden = itemView.findViewById(R.id.securetag);
                laycommision = itemView.findViewById(R.id.laycommi);
                negotiable = itemView.findViewById(R.id.negotiable);
                edit = itemView.findViewById(R.id.edit);
                share = itemView.findViewById(R.id.share);
                pickup = itemView.findViewById(R.id.pickup);
            }
        }

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
            holder.verify.setVisibility(View.GONE);

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
                                Glide.with(getContext()).load(documentSnapshot.getString("UserProfileImageUri"))
                                        .into(holder.image);
                            } catch (Exception e) {
                            }

                        }
                    });

            holder.detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                    holder.detail.startAnimation(animation);
                    Intent i = new Intent(getContext(), bookchatmy.class);
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