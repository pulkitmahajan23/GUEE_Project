package com.example.guee.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.guee.model.FirebaseDatabaseRepository;
import com.example.guee.model.Project;

import java.util.List;

public class FirebaseDatabaseViewModel extends AndroidViewModel {

    private FirebaseDatabaseRepository firebaseDatabaseRepository;
    private MutableLiveData<List<Project>> projectList;

    public FirebaseDatabaseViewModel(@NonNull Application application) {
        super(application);
        firebaseDatabaseRepository = new FirebaseDatabaseRepository();
        projectList = firebaseDatabaseRepository.getAllProjects();
    }

    public MutableLiveData<List<Project>> getAllProjects(){
        return projectList;
    }

    public void detachListeners(){
        firebaseDatabaseRepository.detachListeners();
    }

    public void refreshProjectList(SwipeRefreshLayout swipeRefreshLayout){
        firebaseDatabaseRepository.refreshProjectList(swipeRefreshLayout);
    }

}