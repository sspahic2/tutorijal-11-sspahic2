package ba.unsa.etf.rpr.tutorijal10;


public class Drzava {
    private int drzavaID;
    private String naziv;
    private Grad glavniGrad = null;

    public Drzava(int drzavaID, String naziv, Grad glavniGrad) {
        this.drzavaID = drzavaID;
        this.naziv = naziv;
        this.glavniGrad = glavniGrad;
    }

    public Drzava() {}

    public int getDrzavaID() {
        return drzavaID;
    }

    public void setDrzavaID(int drzavaID) {
        this.drzavaID = drzavaID;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public Grad getGlavniGrad() {
        return glavniGrad;
    }

    public void setGlavniGrad(Grad glavniGrad) {
        this.glavniGrad = glavniGrad;
    }

    @Override
    public String toString() {
        return  naziv;
    }
}

