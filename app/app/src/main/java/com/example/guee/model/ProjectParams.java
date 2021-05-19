package com.example.guee.model;

public class ProjectParams {

    private double energy;
    private double currentAc, power, tariff;
    public ProjectParams()
    {}

    public ProjectParams(double energy, double currentAc, double power,double tariff)
    {
        this.currentAc=currentAc;
        this.energy=energy;
        this.power=power;
        this.tariff=tariff;
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

    public double getTariff(){
        return tariff;
    }
}
