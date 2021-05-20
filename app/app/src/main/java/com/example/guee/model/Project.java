package com.example.guee.model;

import android.util.Log;

import java.util.List;

public class Project {

    private List<ProjectParams> projectParamsList;


    public Project(){
    }

    public Project(List<ProjectParams> projectParamsList) {
        this.projectParamsList = projectParamsList;
    }

    public List<ProjectParams> getProjectParamsList() {
        return projectParamsList;
    }

    public void setProjectParamsList(List<ProjectParams> projectParamsList) {
        this.projectParamsList = projectParamsList;
    }

    public double getLatestCurrent(){

        return projectParamsList.get(projectParamsList.size()-1).getCurrentAc();
       // return  projectParamsList.get().getCurrentAc();
    }

    public double getLatestEnergy(){
        return projectParamsList.get(projectParamsList.size()-1).getEnergy();
       // return  projectParamsList.get(2).getEnergy();
    }

    public double getLatestPower(){
      return projectParamsList.get(projectParamsList.size()-1).getPower();
    //    return  projectParamsList.get(2).getPower();
    }

    public double getLatestTariff(){
        return projectParamsList.get(projectParamsList.size()-1).getTariff();
        //return projectParamsList.get(2).getTariff();
    }
}

