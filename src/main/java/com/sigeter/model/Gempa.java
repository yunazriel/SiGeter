package com.sigeter.model;

public class Gempa {
    protected String magnitude;
    protected String kedalaman;
    protected String potensi;
    protected String cordinate;
    
    public Gempa(String magnitude, String kedalaman, String potensi, String cordinate) {
        this.magnitude = magnitude;
        this.kedalaman = kedalaman;
        this.potensi = potensi;
        this.cordinate = cordinate;
    }

    public String getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(String magnitude) {
        this.magnitude = magnitude;
    }

    public String getKedalaman() {
        return kedalaman;
    }

    public void setKedalaman(String kedalaman) {
        this.kedalaman = kedalaman;
    }

    public String getPotensi() {
        return potensi;
    }

    public void setPotensi(String potensi) {
        this.potensi = potensi;
    }

    public String getCordinate() {
        return cordinate;
    }

    public void setCordinate(String cordinate) {
        this.cordinate = cordinate;
    }
}
