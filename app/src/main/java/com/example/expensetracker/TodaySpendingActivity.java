package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class TodaySpendingActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private TextView totalAmountSpentOn;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private  ProgressDialog loader;

    private FirebaseAuth mAuth;
    private  String onLineUserId = "";
    private DatabaseReference expensesRef;

    private  TodayItemsAdapter todayItemsAdapter;
    private List<Data> myDataList;



    @Override
    // This function is the first to be called when this activity is invoked.
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_spending);// Set the layout for this activity.

        // Initialization of the toolbar and title.
        toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setTitle("Today's Spending");

        totalAmountSpentOn = findViewById(R.id.totalAmountSpentOn);
        progressBar = findViewById(R.id.progressBar);

        // Initialization of UI components.
        fab = findViewById(R.id.fab);
        // Initialize a progress dialog box.
        loader = new ProgressDialog(this);
        // Get an instance of Firebase Authentication and the current user's ID.
        mAuth = FirebaseAuth.getInstance();
        onLineUserId = mAuth.getCurrentUser().getUid();
        // Reference to the 'expenses' node in Firebase Database for the current user.
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onLineUserId);
        // Initialize Recycler view and its layout manager.
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Initialize the data list and the adapter for RecyclerView.
        myDataList = new ArrayList<>();
        todayItemsAdapter = new TodayItemsAdapter(TodaySpendingActivity.this,myDataList);
        recyclerView.setAdapter(todayItemsAdapter);
        readItems();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemSpentOn();
            }
        });

    }
    // Function to read items from Firebase Database for today's spending.
    private void readItems() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        // Create a reference to Firebase Database for the current user's expenses.
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onLineUserId);


        // Query to fetch data for the current date.
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myDataList.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    Data data = snap.getValue(Data.class);
                    myDataList.add(data);
                }


                todayItemsAdapter.notifyDataSetChanged();
//                progressBar.setVisibility(View.GONE);

                int totalAmount = 0;
                for(DataSnapshot ds : snapshot.getChildren()){
                    Map<String,Object>map = (Map<String, Object>)ds.getValue();
                    Object total =  map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalAmountSpentOn.setText("Total Day's Spending: $"+totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    // Function to add a new spending item.
    private void addItemSpentOn() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myview = inflater.inflate(R.layout.input_layout,null);
        myDialog.setView(myview);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);
        // Get the reference of the UI components.
        final Spinner itemspinner = myview.findViewById(R.id.itemsspinner);
        final EditText amount = myview.findViewById(R.id.amount);
        final EditText note = myview.findViewById(R.id.note);
        final Button cancel =  myview.findViewById(R.id.cancel);
        final  Button save = myview.findViewById(R.id.save);
       note.setVisibility(View.VISIBLE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Amount = amount.getText().toString();
                String Item = itemspinner.getSelectedItem().toString();
                String notes = note.getText().toString();

                if(TextUtils.isEmpty(Amount)){
                    amount.setError("Amount is Required!!: ");
                    return;
                }

                if(Item.equals("Select Item")){
                    Toast.makeText(TodaySpendingActivity.this,"Select a Valid Item",Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(notes)){
                    note.setText("Note Required");
                    return;
                }
                else{
                    loader.setMessage("Adding the Budget Item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = expensesRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks = Weeks.weeksBetween(epoch,now);
                    Months months = Months.monthsBetween(epoch,now);

                    String itemday = Item + date;
                    String itemWeek = Item+weeks.getWeeks();
                    String itemMonth = Item+months.getMonths();
                    Data data = new Data(Item,date,id,notes,itemday,itemWeek,itemMonth,Integer.parseInt(Amount),months.getMonths(),weeks.getWeeks());
                    expensesRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(TodaySpendingActivity.this,"Budget Item Added Successfully",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(TodaySpendingActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                }
                dialog.dismiss();
            }

        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}