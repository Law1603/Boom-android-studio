package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MonthlyAnalyticsActivity extends AppCompatActivity {

    private Toolbar SettingsToolbar;
    private FirebaseAuth mAuth;

    private  String onLineUserId = "";
    private DatabaseReference expenseRef,personalRef;

    private  TextView monthSpentAmount,MonthRatioSpending;
    private TextView totalBudgetAmountTextView,analyticsTransportAmount,analyticsFoodAmount,analyticsEntertainmentAmount,analyticsHouseAmount,analyticsHealthAmount,analyticsCharityAmount,analyticsPersonalAmount,analyticsOtherAmount;

    private RelativeLayout linearLayoutAnalysis,relativeLayoutTransport,relativeLayoutFood,relativeLayoutEntertainment,relativeLayoutHouse,relativeLayoutHealth,relativeLayoutCharity,relativeLayoutPersonal,relativeLayoutOther;


    private AnyChartView anyChartView;
    private  TextView progress_ratio_Transport,progress_ratio_Food,progress_ratio_Entertainment,progress_ratio_House,progress_ratio_Health,progress_ratio_Charity,progress_ratio_Personal,progress_ratio_Other;
    private ImageView MonthRatioSpending_image, status_image__Transport,status_image__Food,status_image__Entertainment,status_image__House,status_image__Health,status_image__Charity,status_image__Personal,status_image__Other;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_analytics);

        getSupportActionBar().setTitle("Monthly Analytics");

        mAuth = FirebaseAuth.getInstance();
        onLineUserId = mAuth.getCurrentUser().getUid();
        expenseRef = FirebaseDatabase.getInstance().getReference("expenses").child(onLineUserId);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onLineUserId);


        totalBudgetAmountTextView = findViewById(R.id.totalAmountSpentOn);


        //Analytics
        monthSpentAmount  = findViewById(R.id.monthSpentAmount);
        linearLayoutAnalysis = findViewById(R.id.linearLayoutAnalysis);
        MonthRatioSpending = findViewById(R.id.monthRatioSpending);
        MonthRatioSpending_image = findViewById(R.id.monthRatioSpending_img);


        analyticsTransportAmount = findViewById(R.id.analyticsTransportAmount);
        analyticsFoodAmount = findViewById(R.id.analyticsFoodAmount);
        analyticsEntertainmentAmount = findViewById(R.id.analyticsEntertainmentAmount);
        analyticsHouseAmount = findViewById(R.id.analyticsHouseAmount);
        analyticsHealthAmount = findViewById(R.id.analyticsHealthAmount);
        analyticsCharityAmount = findViewById(R.id.analyticsCharityAmount);
        analyticsPersonalAmount = findViewById(R.id.analyticsPersonalAmount);
        analyticsOtherAmount= findViewById(R.id.analyticsOtherAmount);

        //     //Relative
        relativeLayoutTransport= findViewById(R.id.relativeLayoutTransport);
        relativeLayoutFood = findViewById(R.id.relativeLayoutFood);
        relativeLayoutEntertainment= findViewById(R.id.relativeLayoutEntertainment);
        relativeLayoutHouse = findViewById(R.id.relativeLayoutHouse);
        relativeLayoutHealth= findViewById(R.id.relativeLayoutHealth);
        relativeLayoutCharity= findViewById(R.id.relativeLayoutCharity);
        relativeLayoutPersonal= findViewById(R.id.relativeLayoutPersonal);
        relativeLayoutOther= findViewById(R.id.relativeLayoutOther);

        progress_ratio_Transport = findViewById(R.id.progress_ratio_transport);
        progress_ratio_Food = findViewById(R.id.progress_ratio_food);
        progress_ratio_Entertainment = findViewById(R.id.progress_ratio_entertainment);
        progress_ratio_House = findViewById(R.id.progress_ratio_house);
        progress_ratio_Health= findViewById(R.id.progress_ratio_health);
        progress_ratio_Charity= findViewById(R.id.progress_ratio_charity);
        progress_ratio_Personal= findViewById(R.id.progress_ratio_personal);
        progress_ratio_Other= findViewById(R.id.progress_ratio_other);
        MonthRatioSpending = findViewById(R.id.monthRatioSpending);


        //Image View

        status_image__Transport = findViewById(R.id.status_image__Transport);
        status_image__Food= findViewById(R.id.status_image__Food);
        status_image__Entertainment= findViewById(R.id.status_image__Entertainment);
        status_image__House= findViewById(R.id.status_image__House);
        status_image__Health= findViewById(R.id.status_image__Health);
        status_image__Charity= findViewById(R.id.status_image__Charity);
        status_image__Personal= findViewById(R.id.status_image__Personal);
        status_image__Other= findViewById(R.id.status_image__Other);
        MonthRatioSpending_image  = findViewById(R.id.monthRatioSpending_img);


        anyChartView = findViewById(R.id.anyChartView);


        getTotalMonthlyTransportExpenses();
        getTotalMonthlyFoodExpenses();
        getTotalMonthlyEntertainmentExpenses();
        getTotalMonthlyHouseExpenses();
        getTotalMonthlyHealthExpenses();
        getTotalMonthlyCharityExpenses();
        getTotalMonthlyPersonalExpenses();
        getTotalMonthlyOtherExpenses();
        getTotalMonthExpenses();


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                loadGraph();
                setStatusAndImageResource();
            }
        }, 4000);


    }

    private void getTotalMonthlyOtherExpenses() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch,now);
        String itemNday = "Other"+months.getMonths();



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(mAuth.getCurrentUser().getUid());
        Query query = reference.orderByChild("itemmonth").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    int totalAmount =0;
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsOtherAmount.setText("$" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthOther").setValue(totalAmount);
                }
                else{
                    relativeLayoutOther.setVisibility(View.GONE);
                    personalRef.child("monthOther").setValue(0);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthlyPersonalExpenses() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch,now);
        String itemNday = "Personal"+months.getMonths();



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(mAuth.getCurrentUser().getUid());
        Query query = reference.orderByChild("itemmonth").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    int totalAmount =0;
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsPersonalAmount.setText("$" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthPersonal").setValue(totalAmount);
                }
                else{
                    relativeLayoutPersonal.setVisibility(View.GONE);
                    personalRef.child("monthPersonal").setValue(0);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthlyCharityExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch,now);
        String itemNday = "Charity"+months.getMonths();



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(mAuth.getCurrentUser().getUid());
        Query query = reference.orderByChild("itemmonth").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    int totalAmount =0;
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsCharityAmount.setText("$" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthCharity").setValue(totalAmount);
                }
                else{
                    relativeLayoutCharity.setVisibility(View.GONE);
                    personalRef.child("monthCharity").setValue(0);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthlyHealthExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch,now);
        String itemNday = "Health"+months.getMonths();



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(mAuth.getCurrentUser().getUid());
        Query query = reference.orderByChild("itemmonth").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    int totalAmount =0;
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsHealthAmount.setText("$" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthHealth").setValue(totalAmount);
                }
                else{
                    relativeLayoutHealth.setVisibility(View.GONE);
                    personalRef.child("monthHealth").setValue(0);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthlyHouseExpenses() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch,now);
        String itemNday = "House"+months.getMonths();



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(mAuth.getCurrentUser().getUid());
        Query query = reference.orderByChild("itemmonth").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    int totalAmount =0;
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsHouseAmount.setText("$" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthHouse").setValue(totalAmount);
                }
                else{
                    relativeLayoutHouse.setVisibility(View.GONE);
                    personalRef.child("monthHouse").setValue(0);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthlyEntertainmentExpenses() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch,now);
        String itemNday = "Entertainment"+months.getMonths();



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(mAuth.getCurrentUser().getUid());
        Query query = reference.orderByChild("itemmonth").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    int totalAmount =0;
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsEntertainmentAmount.setText("$" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthEntertainment").setValue(totalAmount);
                }
                else{
                    relativeLayoutEntertainment.setVisibility(View.GONE);
                    personalRef.child("monthEntertainment").setValue(0);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthlyFoodExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch,now);
        String itemNday = "Food"+months.getMonths();



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(mAuth.getCurrentUser().getUid());
        Query query = reference.orderByChild("itemmonth").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    int totalAmount =0;
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsFoodAmount.setText("$" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthFood").setValue(totalAmount);
                }
                else{
                    relativeLayoutFood.setVisibility(View.GONE);
                    personalRef.child("monthFood").setValue(0);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getTotalMonthlyTransportExpenses() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch,now);
        String itemNday = "Transport"+months.getMonths();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+itemNday);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(mAuth.getCurrentUser().getUid());
        Query query = reference.orderByChild("itemmonth").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    int totalAmount =0;
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsTransportAmount.setText("$" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthTrans").setValue(totalAmount);
                }
                else{
                    relativeLayoutTransport.setVisibility(View.GONE);
//                    personalRef.child("monthTrans").setValue(0);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthExpenses() {


        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch,now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(mAuth.getCurrentUser().getUid());
        Query query = reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists() && snapshot.getChildrenCount()>0)  {
                    int totalAmount =0;
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    System.out.println("DEEEEEBBBBBBBBBBUGGGG   " +totalAmount);
                    totalBudgetAmountTextView.setText("Total Month's Spending $ "+totalAmount);
                    monthSpentAmount.setText("Total Spent $ "+totalAmount);
                }
                else{
                    totalBudgetAmountTextView.setText("You've not spent this Month!!!");
                    anyChartView.setVisibility(View.GONE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadGraph(){
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    int traTotal;
                    if (snapshot.hasChild("monthTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("monthTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }

                    int foodTotal;
                    if (snapshot.hasChild("monthFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("monthFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }
                    int EntertainmentTotal;
                    if (snapshot.hasChild("monthEntertainment")) {
                        EntertainmentTotal = Integer.parseInt(snapshot.child("monthEntertainment").getValue().toString());
                    } else {
                        EntertainmentTotal = 0;
                    }
                    int HealthTotal;
                    if (snapshot.hasChild("monthHealth")) {
                        HealthTotal = Integer.parseInt(snapshot.child("monthHealth").getValue().toString());
                    } else {
                        HealthTotal = 0;
                    }
                    int HouseTotal;
                    if (snapshot.hasChild("monthHouse")) {
                        HouseTotal = Integer.parseInt(snapshot.child("monthHouse").getValue().toString());
                    } else {
                        HouseTotal = 0;
                    }
                    int CharityTotal;
                    if (snapshot.hasChild("monthCharity")) {
                        CharityTotal = Integer.parseInt(snapshot.child("monthCharity").getValue().toString());
                    } else {
                        CharityTotal = 0;
                    }
                    int PersonalTotal;
                    if (snapshot.hasChild("monthPersonal")) {
                        PersonalTotal = Integer.parseInt(snapshot.child("monthPersonal").getValue().toString());
                    } else {
                        PersonalTotal = 0;
                    }
                    int OtherTotal;
                    if (snapshot.hasChild("monthOther")) {
                        OtherTotal = Integer.parseInt(snapshot.child("monthOther").getValue().toString());
                    } else {
                        OtherTotal = 0;
                    }

                    Pie pie = AnyChart.pie();
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Transport", traTotal));
                    data.add(new ValueDataEntry("Food", foodTotal));
                    data.add(new ValueDataEntry("Entertainment", EntertainmentTotal));
                    data.add(new ValueDataEntry("House", HouseTotal));
                    System.out.println("Health"+HealthTotal);
                    data.add(new ValueDataEntry("Health", HealthTotal));
                    data.add(new ValueDataEntry("Charity", CharityTotal));
                    data.add(new ValueDataEntry("Personal", PersonalTotal));
                    data.add(new ValueDataEntry("Other", OtherTotal));

//                    data.add(new ValueDataEntry("Transport", 1000));
//                    data.add(new ValueDataEntry("Food", 2000));
//                    data.add(new ValueDataEntry("Entertainment", 400));
//                    data.add(new ValueDataEntry("House", 300));
//                    data.add(new ValueDataEntry("Health", 850));
//                    data.add(new ValueDataEntry("Charity", 750));
//                    data.add(new ValueDataEntry("Personal", 900));
//                    data.add(new ValueDataEntry("Other", 2500));

                    pie.data(data);

                    pie.title("Monthly Analytics");
                    pie.labels().position("outside");
                    pie.legend().title().enabled(true);
                    pie.legend().title().text("Items Spent On").padding(0d, 0d, 10d, 0d);
                    pie.legend().position("center-bottom").itemsLayout(LegendLayout.HORIZONTAL).align(Align.CENTER);
                    anyChartView.setChart(pie);

                }
                else{
                    Toast.makeText(MonthlyAnalyticsActivity.this,"Child Doesnt Exist",Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setStatusAndImageResource(){
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {

                    float traTotal;
                    if (snapshot.hasChild("monthTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("monthTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }

                    float foodTotal;
                    if (snapshot.hasChild("monthFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("monthFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }
                    float EntertainmentTotal;
                    if (snapshot.hasChild("monthEntertainment")) {
                        EntertainmentTotal = Integer.parseInt(snapshot.child("monthEntertainment").getValue().toString());
                    } else {
                        EntertainmentTotal = 0;
                    }
                    float HealthTotal;
                    if (snapshot.hasChild("monthHealth")) {
                        HealthTotal = Integer.parseInt(snapshot.child("monthHealth").getValue().toString());
                    } else {
                        HealthTotal = 0;
                    }
                    float HouseTotal;
                    if (snapshot.hasChild("monthHouse")) {
                        HouseTotal = Integer.parseInt(snapshot.child("monthHouse").getValue().toString());
                    } else {
                        HouseTotal = 0;
                    }
                    float CharityTotal;
                    if (snapshot.hasChild("monthCharity")) {
                        CharityTotal = Integer.parseInt(snapshot.child("monthCharity").getValue().toString());
                    } else {
                        CharityTotal = 0;
                    }
                    float PersonalTotal;
                    if (snapshot.hasChild("monthPersonal")) {
                        PersonalTotal = Integer.parseInt(snapshot.child("monthPersonal").getValue().toString());
                        System.out.println("PersonalTotal "+PersonalTotal);
                    } else {
                        PersonalTotal = 0;
                    }
                    float OtherTotal;
                    if (snapshot.hasChild("monthOther")) {
                        OtherTotal = Integer.parseInt(snapshot.child("monthOther").getValue().toString());
                    } else {
                        OtherTotal = 0;
                    }

                    float monthTotalSpentAmount;
                    if (snapshot.hasChild("month")) {
                        monthTotalSpentAmount = Integer.parseInt(snapshot.child("month").getValue().toString());
                    } else {
                        monthTotalSpentAmount = 0;
                    }


                    ///Getting Ratios


                    float traRatio;
                    if (snapshot.hasChild("monthTransRatio")) {
                        traRatio = Integer.parseInt(snapshot.child("monthTransRatio").getValue().toString());
                    } else {
                        traRatio = 0;
                    }

                    float foodRatio;
                    if (snapshot.hasChild("monthFood")) {
                        foodRatio = Integer.parseInt(snapshot.child("monthFoodRatio").getValue().toString());
                    } else {
                        foodRatio = 0;
                    }
                    float EntertainmentRatio;
                    if (snapshot.hasChild("monthEntertainmentRatio")) {
                        EntertainmentRatio = Integer.parseInt(snapshot.child("monthEntertainmentRatio").getValue().toString());
                    } else {
                        EntertainmentRatio = 0;
                    }
                    float HealthRatio;
                    if (snapshot.hasChild("monthHealthRatio")) {
                        HealthRatio = Integer.parseInt(snapshot.child("monthHealthRatio").getValue().toString());
                    } else {
                        HealthRatio = 0;
                    }
                    float HouseRatio;
                    if (snapshot.hasChild("monthHouseRatio")) {
                        HouseRatio = Integer.parseInt(snapshot.child("monthHouseRatio").getValue().toString());
                    } else {
                        HouseRatio = 0;
                    }
                    float CharityRatio;
                    if (snapshot.hasChild("monthCharityRatio")) {
                        CharityRatio = Integer.parseInt(snapshot.child("monthCharityRatio").getValue().toString());
                    } else {
                        CharityRatio = 0;
                    }
                    float PersonalRatio;
                    if (snapshot.hasChild("monthPersonalRatio")) {
                        PersonalRatio = Integer.parseInt(snapshot.child("monthPersonalRatio").getValue().toString());
                    } else {
                        PersonalRatio = 0;
                    }
                    float OtherRatio;
                    if (snapshot.hasChild("monthOtherRatio")) {
                        OtherRatio = Integer.parseInt(snapshot.child("monthOtherRatio").getValue().toString());
                    } else {
                        OtherRatio = 0;
                    }

                    float monthTotalSpentAmountRatio;
                    if (snapshot.hasChild("budget")) {
                        monthTotalSpentAmountRatio = Integer.parseInt(snapshot.child("budget").getValue().toString());
                    } else {
                        monthTotalSpentAmountRatio = 0;
                    }


                    float monthPercent = (monthTotalSpentAmount/monthTotalSpentAmountRatio)*100;

                    if(monthPercent<50){
                        MonthRatioSpending.setText(monthPercent+" %"+" used of "+monthTotalSpentAmountRatio+" $");
                        MonthRatioSpending_image.setImageResource(R.drawable.green);

                    }
                    else if(monthPercent>=50 && monthPercent<100 ){
                        MonthRatioSpending.setText(monthPercent+" %"+" used of "+monthTotalSpentAmountRatio+" $");
                        MonthRatioSpending_image.setImageResource(R.drawable.brown);

                    }else{
                        MonthRatioSpending.setText(monthPercent+" %"+" used of "+monthTotalSpentAmountRatio+" $");
                        MonthRatioSpending_image.setImageResource(R.drawable.red);
                    }


                    float TransPercent = (traTotal/traRatio)*100;

                    if(TransPercent<50){
                        progress_ratio_Transport.setText(TransPercent+"%"+"used of "+traRatio+" $");
                        status_image__Transport.setImageResource(R.drawable.green);

                    }
                    else if(TransPercent>=50 && TransPercent<100 ){
                        progress_ratio_Transport.setText(TransPercent+"%"+"used of "+traRatio+" $");
                        status_image__Transport.setImageResource(R.drawable.brown);

                    }else{
                        progress_ratio_Transport.setText(TransPercent+"%"+"used of "+traRatio+" $");
                        status_image__Transport.setImageResource(R.drawable.red);
                    }


                    float FoodPercent = (foodTotal/foodRatio)*100;

                    if(FoodPercent<50){
                        progress_ratio_Food.setText(FoodPercent+" %"+" used of "+foodRatio+" $");
                        status_image__Food.setImageResource(R.drawable.green);

                    }
                    else if(FoodPercent>=50 && FoodPercent<100 ){
                        progress_ratio_Food.setText(FoodPercent+" %"+" used of "+foodRatio+" $");
                        status_image__Food.setImageResource(R.drawable.brown);

                    }else{
                        progress_ratio_Food.setText(FoodPercent+" %"+" used of "+foodRatio+" $");
                        status_image__Food.setImageResource(R.drawable.red);
                    }


                    float entPercent = (EntertainmentTotal/EntertainmentRatio)*100;

                    if(entPercent<50){
                        progress_ratio_Entertainment.setText(entPercent+" %"+" used of "+EntertainmentRatio+" $");
                        status_image__Entertainment.setImageResource(R.drawable.green);

                    }
                    else if(entPercent>=50 && entPercent<100 ){
                        progress_ratio_Entertainment.setText(entPercent+" %"+" used of "+EntertainmentRatio+" $");
                        status_image__Entertainment.setImageResource(R.drawable.brown);

                    }else{
                        progress_ratio_Entertainment.setText(entPercent+" %"+" used of "+EntertainmentRatio+" $");
                        status_image__Entertainment.setImageResource(R.drawable.red);
                    }

                    float HousePercent = (HouseTotal/HouseRatio)*100;

                    if(HousePercent<50){
                        progress_ratio_House.setText(HousePercent+" %"+" used of "+HouseRatio+" $. Status");
                        status_image__House.setImageResource(R.drawable.green);

                    }
                    else if(HousePercent>=50 && HousePercent<100 ){
                        progress_ratio_House.setText(HousePercent+" %"+" used of "+HouseRatio+" $");
                        status_image__House.setImageResource(R.drawable.brown);

                    }else{
                        progress_ratio_House.setText(HousePercent+" %"+" used of "+HouseRatio+" $");
                        status_image__House.setImageResource(R.drawable.red);
                    }

                    float HealthPercent = (HealthTotal/HealthRatio)*100;

                    if(HealthPercent<50){
                        progress_ratio_Health.setText(HealthPercent+" %"+" used of "+HealthRatio+" $");
                        status_image__Health.setImageResource(R.drawable.green);

                    }
                    else if(HealthPercent>=50 && HealthPercent<100 ){
                        progress_ratio_Health.setText(HealthPercent+" %"+" used of "+HealthRatio+" $");
                        status_image__Health.setImageResource(R.drawable.brown);

                    }else{
                        progress_ratio_Health.setText(HealthPercent+" %"+" used of "+HealthRatio+" $");
                        status_image__Health.setImageResource(R.drawable.red);
                    }


                    float CharityPercent = (CharityTotal/CharityRatio)*100;

                    if(CharityPercent<50){
                        progress_ratio_Charity.setText(CharityPercent+" %"+" used of "+CharityRatio+" $");
                        status_image__Charity.setImageResource(R.drawable.green);

                    }
                    else if(CharityPercent>=50 && CharityPercent<100 ){
                        progress_ratio_Charity.setText(CharityPercent+" %"+" used of "+CharityRatio+" $");
                        status_image__Charity.setImageResource(R.drawable.brown);

                    }else{
                        progress_ratio_Charity.setText(CharityPercent+" %"+" used of "+CharityRatio+" $");
                        status_image__Charity.setImageResource(R.drawable.red);
                    }

                    float PersonalPercent = (PersonalTotal/PersonalRatio)*100;

                    if(PersonalPercent<50){
                        progress_ratio_Personal.setText(PersonalPercent+" %"+" used of "+PersonalRatio+" $");
                        status_image__Personal.setImageResource(R.drawable.green);

                    }
                    else if(PersonalPercent>=50 && PersonalPercent<100 ){
                        progress_ratio_Personal.setText(PersonalPercent+" %"+" used of "+PersonalRatio+" $");
                        status_image__Personal.setImageResource(R.drawable.brown);

                    }else{
                        progress_ratio_Personal.setText(PersonalPercent+" %"+" used of "+PersonalRatio+" $");
                        status_image__Personal.setImageResource(R.drawable.red);
                    }


                    float OtherPercent = (OtherTotal/OtherRatio)*100;

                    if(OtherPercent<50){
                        progress_ratio_Other.setText(OtherPercent+" $"+" used of "+OtherRatio+" $");
                        status_image__Other.setImageResource(R.drawable.green);

                    }
                    else if(OtherPercent>=50 && OtherPercent<100 ){
                        progress_ratio_Other.setText(OtherPercent+" $"+" used of "+OtherRatio+" $");
                        status_image__Other.setImageResource(R.drawable.brown);

                    }else{
                        progress_ratio_Other.setText(OtherPercent+" $"+" used of "+OtherRatio+" $");
                        status_image__Other.setImageResource(R.drawable.red);
                    }








                }
                else{
                    Toast.makeText(MonthlyAnalyticsActivity.this,"Child Doesnt Exist",Toast.LENGTH_SHORT);
                }







            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}