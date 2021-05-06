package com.example.guee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guee.model.Project;
import com.example.guee.model.ProjectParams;
import com.example.guee.viewmodel.FirebaseDatabaseViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabaseViewModel firebaseDatabaseViewModel;
    float energy;
    LocalTime getSetTimeOff=LocalTime.parse("21:00:00");
    LocalTime getSetTimeOn=LocalTime.parse("08:00:00");

    public static final String PREFS_NAME="MyPrefsFile";

    private TextView currentAc;
    private TextView power;
    private TextView energyText;
    private Switch status;
    private LineChart energyLineChart;
    private Project thisProject;
    private boolean isEnergyPresent=true;
    private SwipeRefreshLayout swipeRefreshLayout;

    ArrayList<Entry> energyChartValues=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentAc=findViewById(R.id.Current);
        power=findViewById(R.id.Power);
        energyText=findViewById(R.id.Energy);
        energyLineChart=findViewById(R.id.energy_graph);
        status=(Switch) findViewById(R.id.switch1);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_description);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        energy=settings.getFloat("energy",energy);

        status.setChecked(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
       final DatabaseReference current_ac = database.getReference("GUEE").child("Current_Ac");


        current_ac.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
               /* float value = dataSnapshot.getValue(float.class);
               // Log.d(TAG, "Value is: " + value);
                if(value<0)
                    value=Math.abs(value);
                currentAc.setText("Ac Current: "+value+" A");
                power.setText("Power consumed: " +(float)value*230*0.97 +" Watts");
                energy= (float) (energy+value*(2.05/60/60/1000));
                Log.d("energy",""+energy);
                DatabaseReference energySent=database.getReference("energy");
                energyText.setText("Energy Consumed: "+energy+" kWh");
               // setUpChart(energy);*/
                firebaseDatabaseViewModel.refreshProjectList(swipeRefreshLayout);
                loadActivity();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
             //   Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

       // FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference Toggle=database.getReference("Status");

        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    Toggle.setValue(1);
                else
                    Toggle.setValue(0);
            }
        });

        LocalTime currentTime = LocalTime.now();

        if(currentTime.isAfter(getSetTimeOff) || currentTime.isBefore(getSetTimeOn))
        {
            Toggle.setValue(0);
            status.setChecked(false);
        }
        else
        {
            Toggle.setValue(1);
            status.setChecked(true);
        }

        loadActivity();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            firebaseDatabaseViewModel.refreshProjectList(swipeRefreshLayout);
            loadActivity();
        });

    }

    public void loadActivity() {

        firebaseDatabaseViewModel = ViewModelProviders.of(MainActivity.this).get(FirebaseDatabaseViewModel.class);
        firebaseDatabaseViewModel.getAllProjects().observe(this, projects -> {
            for (Project project : projects) {
                thisProject = project;
                setValues();
                break;
            }
        });
    }

    private void setValues()
    {
        currentAc.setText("Ac Current: "+thisProject.getLatestCurrent()+" A");
        power.setText("Power consumed: " +thisProject.getLatestPower() +" Watts");
        energyText.setText("Energy Consumed: "+thisProject.getLatestEnergy()+" kWh");
        setUpChart(thisProject.getProjectParamsList());
      //  Log.println(Log.ASSERT,"Project params list size", String.valueOf(thisProject.getProjectParamsList()));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor=settings.edit();
        /*editor.putFloat("energy",energy);
        editor.commit();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor=settings.edit();
        /*editor.putFloat("energy",energy);
        editor.commit();*/
    }

    private void createChart(ArrayList<Entry> entries, String label, int color, LineChart lineChart) {
        float textSize = 9;

        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setValueTextColor(color);
        dataSet.setValueTextSize(textSize);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setTextColor(Color.WHITE);

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setTextColor(Color.WHITE);
        yAxisLeft.setGranularity(1f);

        LineData data = new LineData(dataSet);

        lineChart.getLegend().setTextColor(Color.WHITE);
        lineChart.getDescription().setEnabled(false);
        lineChart.setData(data);
        lineChart.invalidate();
    }

    int i=0;
    private void setUpChart(List<ProjectParams> projectParamsList) {

        final int[] colors = new int[]{
                ColorTemplate.VORDIPLOM_COLORS[0],
                ColorTemplate.VORDIPLOM_COLORS[1],
                ColorTemplate.VORDIPLOM_COLORS[2],
                ColorTemplate.VORDIPLOM_COLORS[3],
        };

        ArrayList<Entry> energyGraphEntries = new ArrayList<>();
        //final DatabaseReference current_ac = database.getReference().child("Current_AC");

        for (ProjectParams projectParams : projectParamsList) {
            Log.println(Log.ASSERT,"Graph values "+i,String.valueOf(projectParams.getPower()));
            if(projectParams.getPower()>0) {
                energyGraphEntries.add(new Entry(i, (float) projectParams.getPower()));
                i++;
            }
        }


            shouldChartExist(projectParamsList);
        createChart(energyGraphEntries, "Energy", colors[0], energyLineChart);
       /* if (isEnergyPresent) {
            createChart(energyGraphEntries, "Energy", colors[0], energyLineChart);
        } else {
            energyLineChart.setVisibility(View.GONE);
            findViewById(R.id.energy_graph).setVisibility(View.GONE);
        }*/


    }

    private void shouldChartExist(List<ProjectParams> projectParamsArrayList) {
        double energySum=0;
        for (ProjectParams projectParams : projectParamsArrayList) {
            energySum += projectParams.getEnergy();
        }
        if(energySum==0)
            isEnergyPresent=false;

    }
}