package com.example.medicalappv1;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class Analyses {
    String name;
    ArrayList<Entry> coord;

    public void setCoord(ArrayList<Entry> coord) {
        this.coord = coord;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Entry> getCoord() {
        return coord;
    }

    public String getName() {
        return name;
    }

    public Analyses(String name, ArrayList<Entry> coord) {
        this.name = name;
        this.coord = coord;
    }
}
