package com.example.guee.model;

public class ProjectParams {

    private double energy;
    private double currentAc, power;
    public ProjectParams()
    {}

    public ProjectParams(double energy, double currentAc, double power)
    {
        this.currentAc=currentAc;
        this.energy=energy;
        this.power=power;
    }

    public double getEnergy()
    {
        return energy;
    }

    public double getCurrentAc()
    {
        return currentAc;
    }

    public double getPower()
    {
        return power;
    }
}
