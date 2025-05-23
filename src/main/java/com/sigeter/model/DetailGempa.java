package com.sigeter.model;

public class DetailGempa extends Gempa {
    private String tanggal;
    private String jam;
    private String wilayah;
    private String map;

    public DetailGempa(String magnitude, String kedalaman, String potensi, String cordinate, String tanggal, String jam, String wilayah, String map) {
        super(magnitude, kedalaman, potensi, cordinate);
        this.tanggal = tanggal;
        this.jam = jam;
        this.wilayah = wilayah;
        this.map = map;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getWilayah() {
        return wilayah;
    }

    public void setWilayah(String wilayah) {
        this.wilayah = wilayah;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }
}
