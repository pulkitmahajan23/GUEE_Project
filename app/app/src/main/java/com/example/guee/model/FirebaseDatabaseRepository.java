package com.example.guee.model;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FirebaseDatabaseRepository {

    private static final String TAG = "FirebaseDatabaseRepository";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Project> projectList;
    private MutableLiveData<List<Project>> projectMutableLiveData;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ChildEventListener projectsChildEventListener;
    private ValueEventListener projectsValueEventListener;

    public FirebaseDatabaseRepository() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        projectList = new ArrayList<>();
        projectMutableLiveData = new MutableLiveData<>();
        setUpListener();
        getProjects();
    }

    private void getProjects() {
        projectList.clear();
        attachListeners();
    }

    public void attachListeners(){
     //   databaseReference.addChildEventListener(projectsChildEventListener);
        databaseReference.addValueEventListener(projectsValueEventListener);
    }

    public void detachListeners(){
        if(projectsChildEventListener != null){
            databaseReference.removeEventListener(projectsChildEventListener);
        }
    }

    public MutableLiveData<List<Project>> getAllProjects() {
        return projectMutableLiveData;
    }

    public void refreshProjectList(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
        getProjects();
    }

    private double getEnergy(DataSnapshot dataSnapShot) {
         double energy =0;
        try {
            energy = Double.parseDouble(dataSnapShot.child("Energy").getValue().toString());
            if(energy<0)
                energy=Math.abs(energy);
          //  Log.println(Log.ASSERT,"Get energy", String.valueOf(energy));
        } catch (Exception ignored) {
        }

        return energy;
    }

    private double getCurrentAc(DataSnapshot dataSnapShot) {
        double energy = 0;
        try {
            energy = Double.parseDouble(dataSnapShot.child("Current_Ac").getValue().toString());
            if(energy<0)
                energy=Math.abs(energy);
        } catch (Exception ignored) {
        }
        return energy;
    }

    private double getPower(DataSnapshot dataSnapShot) {
        double energy = 0;
        try {
            energy = Double.parseDouble(dataSnapShot.child("Power").getValue().toString());
            if(energy<0)
                energy=Math.abs(energy);
        } catch (Exception ignored) {
        }
        return energy;
    }

    private double getTariff(DataSnapshot dataSnapShot) {
        double energy = 0;
        try {
            energy = Double.parseDouble(dataSnapShot.child("Tariff").getValue().toString());
            if(energy<0)
                energy=Math.abs(energy);
        } catch (Exception ignored) {
        }
        return energy;
    }
    private void setUpListener() {
        /*projectsChildEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot projectDataSnapshot, @Nullable String s) {
                Iterator<DataSnapshot> Iterator = projectDataSnapshot.getChildren().iterator();

                List<ProjectParams> projectParamsList = new ArrayList<>();
                while (Iterator.hasNext()) {
                    DataSnapshot dataSnapShot = Iterator.next();

                    double energy = getEnergy(dataSnapShot);
                    double currentAc = getCurrentAc(dataSnapShot);
                    double power= getPower(dataSnapShot);
                    //Toast.makeText(,""+energy,Toast.LENGTH_SHORT).show();

                    Log.println(Log.ASSERT,"Inside onchild added- Energy", String.valueOf(energy));
                    Log.println(Log.ASSERT,"Inside onchild added - current", String.valueOf(currentAc));
                    Log.println(Log.ASSERT,"Inside onchild added - power", String.valueOf(power));
                    projectParamsList.add(new ProjectParams(energy,currentAc,power));
                }
                if(projectParamsList.isEmpty()){
                    projectParamsList.add(new ProjectParams(5,5,5));
                }
                projectList.add(new Project(projectParamsList));
                projectMutableLiveData.setValue(projectList);
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
*/
        projectsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterator<DataSnapshot> Iterator = snapshot.getChildren().iterator();

                List<ProjectParams> projectParamsList = new ArrayList<>();
                while (Iterator.hasNext()) {
                    DataSnapshot dataSnapShot = Iterator.next();
                    double energy = getEnergy(dataSnapShot);
                    double currentAc = getCurrentAc(dataSnapShot);
                    double power= getPower(dataSnapShot);
                    double tariff= getTariff(dataSnapShot);
                   // Log.println(Log.ASSERT,"Inside on data change - Energy", String.valueOf(energy));
                    //Log.println(Log.ASSERT,"Inside on data change - current", String.valueOf(currentAc));
                    //Log.println(Log.ASSERT,"Inside on data change - power", String.valueOf(power));
                   // Log.println(Log.ASSERT,"Inside on data change - power", String.valueOf(tariff));
                    projectParamsList.add(new ProjectParams(energy,currentAc,power,tariff));
                }
                if(projectParamsList.isEmpty()){
                    projectParamsList.add(new ProjectParams(10,12,17,500));
                }
                projectList.add(new Project(projectParamsList));
                projectMutableLiveData.setValue(projectList);
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

}