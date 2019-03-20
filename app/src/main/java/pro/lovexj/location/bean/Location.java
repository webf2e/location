package pro.lovexj.location.bean;

/**
 * Created by liuwenbin on 18-12-4.
 */

public class Location {
    //lon
    private double l;
    //lat
    private double b;
    //radius
    private float r;
    //errorCode
    private int e;
    //height
    private double h;

    public double getL() {
        return l;
    }

    public void setL(double l) {
        this.l = l;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }

    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }
}
