// Defines the package where this class is located
package com.example.expensetracker;

// Importing required classes from Android, Firebase and Java libraries
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

// Public class HistoryActivity which extends AppCompatActivity and implements DatePickerDialog.OnDateSetListener
public class HistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    // Declaring variables for RecyclerView, Adapter, DataList, FirebaseAuth, Button, TextView and Strings
    private RecyclerView recyclerView;
    private TodayItemsAdapter todayItemsAdapter;
    private List<Data> myDataList;
    private FirebaseAuth mAuth;
    private String onLineUserId = "";
    private DatabaseReference expenseRef;
    private Button search;
    private TextView historyTotalAmountSpent;

    // onCreate method gets called when activity is started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting the layout of this activity to activity_history
        setContentView(R.layout.activity_history);

        // Set the title of the activity
        getSupportActionBar().setTitle("History");

        // Assign views from the layout to the variables
        search = findViewById(R.id.search);
        historyTotalAmountSpent = findViewById(R.id.historyTotalAmount);

        // Get instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Get the unique identifier of the currently authenticated user
        onLineUserId = mAuth.getCurrentUser().getUid();

        // Assign the RecyclerView from the layout to the variable
        recyclerView = findViewById(R.id.recyclerview_feed);

        // Setup LayoutManager for the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // Initialize the data list and adapter, and set the adapter to the RecyclerView
        myDataList = new ArrayList<>();
        todayItemsAdapter = new TodayItemsAdapter(HistoryActivity.this, myDataList);
        recyclerView.setAdapter(todayItemsAdapter);

        // Set onClickListener for the search button
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show date picker dialog
                showDatePickerDialog();
            }
        });
    }

    // Method to show DatePickerDialog
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Handling user selecting a date
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayofMonth) {
        // Convert selected date into string
        int months = month + 1;
        String date;
        if(dayofMonth<10)
            date= "0"+dayofMonth+ "-"+"0"+months+"-"+year;
        else
            date= dayofMonth+ "-"+"0"+months+"-"+year;

        // Fetch data from Firebase database based on selected date
        onLineUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onLineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear current data list
                myDataList.clear();

                // Loop through the data snapshot and add each item to the data list
                for (DataSnapshot ds:snapshot.getChildren()) {
                    Data data = ds.getValue(Data.class);
                    myDataList.add(data);
                }

                // Notify the adapter about data changes and make the RecyclerView visible
                todayItemsAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);

                // Compute and display total amount spent
                int totalAmount =0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                    if(totalAmount>=0){
                        historyTotalAmountSpent.setVisibility(View.VISIBLE);
                        historyTotalAmountSpent.setText("On this Day you spent $ "+totalAmount);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
            }
        });
    }
}
