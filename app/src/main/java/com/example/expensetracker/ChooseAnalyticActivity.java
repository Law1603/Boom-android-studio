// The package where our activity resides
package com.example.expensetracker;

// Necessary imports for the activity
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

// The activity class, extending AppCompatActivity
public class ChooseAnalyticActivity extends AppCompatActivity {

    // CardView UI elements for Today, Week, and Month views
    private CardView TodayCardView,WeekCardView,MonthCardView;

    // Overriding onCreate method which is called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call the superclass method first
        super.onCreate(savedInstanceState);
        // Set the user interface layout for this Activity
        setContentView(R.layout.activity_choose_analytic);

        // Find the CardView elements from the layout
        TodayCardView = findViewById(R.id.TodayCardView);
        WeekCardView = findViewById(R.id.WeekCardView);
        MonthCardView = findViewById(R.id.MonthCardView);

        // Setting an OnClickListener for TodayCardView
        // This will execute when the TodayCardView is clicked
        TodayCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // An Intent to start the DailyAnalyticsActivity
                Intent intent = new Intent(ChooseAnalyticActivity.this,DailyAnlayticsActivity.class);
                // Starting the Activity
                startActivity(intent);

            }
        });
        // Setting an OnClickListener for WeekCardView
        // This will execute when the WeekCardView is clicked
        WeekCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // An Intent to start the WeeklyAnalyticsActivity
                Intent intent = new Intent(ChooseAnalyticActivity.this,WeeklyAnalyticsActivity.class);
                // Starting the Activity
                startActivity(intent);

            }
        });
        // Setting an OnClickListener for MonthCardView
        // This will execute when the MonthCardView is clicked
        MonthCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // An Intent to start the MonthlyAnalyticsActivity
                Intent intent = new Intent(ChooseAnalyticActivity.this,MonthlyAnalyticsActivity.class);
                // Starting the Activity
                startActivity(intent);

            }
        });
    }
}
