package com.example.mapapplication.data;

import java.util.ArrayList;
import java.util.List;

public class Container {

    public int id;
    public List<Double> position;
    public String collection;
    public int fullness;

    public Container() {

    }

    public Container(int id, List<Double> position, String collection, int fullness) {
        this.id = id;
        this.position = position;
        this.collection = collection;
        this.fullness = fullness;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Double> getPosition() {
        return position;
    }

    public void setPosition(List<Double> position) {
        this.position = position;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public int getFullness() {
        return fullness;
    }

    public void setFullness(int fullness) {
        this.fullness = fullness;
    }
}
