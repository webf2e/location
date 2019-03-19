package pro.lovexj.location.bean;

/**
 * Created by liuwenbin on 18-12-4.
 */

public class Location {
    //time
    private String t;
    //lon
    private double l;
    //lat
    private double b;
    //radius
    private float r;
    //addr
    private String a;
    //street
    private String s;
    //errorCode
    private int e;
    //locationDescribe
    private String ld;
    //height
    private double h;

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

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

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }

    public String getLd() {
        return ld;
    }

    public void setLd(String ld) {
        this.ld = ld;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }
}
