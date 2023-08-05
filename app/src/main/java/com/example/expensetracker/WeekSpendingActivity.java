package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeekSpendingActivity extends AppCompatActivity {


    private androidx.appcompat.widget.Toolbar toolbar;
    private TextView totalWeekAmountTextView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private WeekSpendingAdapter weekSpendingAdapter;
    private List<Data> myDataList;
    private FirebaseAuth mAuth;
    private String onLineUserId = "";
    private DatabaseReference expensesRef;

    private String type = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_spending);

        // Set the title of the activity
        getSupportActionBar().setTitle("Weekly Expenditure");

        // Reference UI elements from the layout
        totalWeekAmountTextView = findViewById(R.id.totalWeekAmountTextView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerview);

        // Initialize RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase Authentication and get current user's id
        mAuth = FirebaseAuth.getInstance();
        onLineUserId = mAuth.getCurrentUser().getUid();

        // Initialize data list and adapter
        myDataList = new ArrayList<>();
        weekSpendingAdapter = new WeekSpendingAdapter(WeekSpendingActivity.this, myDataList);

        // Set adapter to RecyclerView
        recyclerView.setAdapter(weekSpendingAdapter);

        // Check if the intent has extras
        if (getIntent().getExtras() != null) {
            // Get the type of data to be fetched ("week" or "month")
            type = getIntent().getStringExtra("type");
            if (type.equals("week"))
                getWeekly();
            else if (type.equals("month"))
                getMonthly();
        }
    }

    // Method to fetch monthly expenditure data
    private void getMonthly() {
        getSupportActionBar().setTitle("Monthly Expenditure");
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);
        Months months = Months.monthsBetween(epoch, now);

        // Get reference to 'expenses' node in Firebase Database
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onLineUserId);

        // Query for expenses of current month
        Query query = expensesRef.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the data list
                myDataList.clear();

                // Populate data list with results from the query
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Data data = ds.getValue(Data.class);
                    myDataList.add(data);
                }

                // Notify adapter that data set has changed
                weekSpendingAdapter.notifyDataSetChanged();

                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Calculate total expenditure for the month
                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    // Display total month's expenditure
                    totalWeekAmountTextView.setText("Total Month's Spending: $" + totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    // Method to fetch weekly expenditure data
    private void getWeekly() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);
        Months months = Months.monthsBetween(epoch, now);

        // Get reference to 'expenses' node in Firebase Database
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onLineUserId);

        // Query for expenses of current week
        Query query = expensesRef.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the data list
                myDataList.clear();

                // Populate data list with results from the query
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Data data = ds.getValue(Data.class);
                    myDataList.add(data);
                }

                // Notify adapter that data set has changed
                weekSpendingAdapter.notifyDataSetChanged();

                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Calculate total expenditure for the week
                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    // Display total week's expenditure
                    totalWeekAmountTextView.setText("Total Week's Spending: $" + totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
