package com.example.deepspaceimageeditor;

public class Star {
    private int id;
    private int size;
    private double sulphur;
    private double hydrogen;
    private double oxygen;

    public Star(int id, int size, double sulphur, double hydrogen, double oxygen){
        this.id = id;
        this.size = size;
        this.sulphur = sulphur;
        this.hydrogen = hydrogen;
        this.oxygen = oxygen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getSulphur() {
        return sulphur;
    }

    public void setSulphur(double sulphur) {
        this.sulphur = sulphur;
    }

    public double getHydrogen() {
        return hydrogen;
    }

    public void setHydrogen(double hydrogen) {
        this.hydrogen = hydrogen;
    }

    public double getOxygen() {
        return oxygen;
    }

    public void setOxygen(double oxygen) {
        this.oxygen = oxygen;
    }
}
