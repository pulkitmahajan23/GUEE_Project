package com.example.guee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<Float> energyConsumption=new ArrayList<Float>();
    float energy;
    LocalTime getSetTimeOff=LocalTime.parse("21:00:00");
    LocalTime getSetTimeOn=LocalTime.parse("08:00:00");

    public static final String PREFS_NAME="MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView currentAc=findViewById(R.id.Current);
        TextView power=findViewById(R.id.Power);
        TextView energyText=findViewById(R.id.Energy);
        Switch status=(Switch) findViewById(R.id.switch1);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        energy=settings.getFloat("energy",energy);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference current_ac = database.getReference().child("Current_AC");

        status.setChecked(true);

        current_ac.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                float value = dataSnapshot.getValue(float.class);
               // Log.d(TAG, "Value is: " + value);
                if(value<0)
                    value=Math.abs(value);
                currentAc.setText("Ac Current: "+value+" A");
                power.setText("Power consumed: " +(float)value*230*0.97 +" Watts");
                energy= (float) (energy+value*(2.05/60/60/1000));
                Log.d("energy",""+energy);
                DatabaseReference energySent=database.getReference("energy");
                energySent.setValue(energy);
                energyText.setText("Energy Consumed: "+energy+" kWh");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
             //   Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

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
        editor.putFloat("energy",energy);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor=settings.edit();
        editor.putFloat("energy",energy);
        editor.commit();
    }
}