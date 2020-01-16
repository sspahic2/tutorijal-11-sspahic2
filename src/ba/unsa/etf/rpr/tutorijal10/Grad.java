package ba.unsa.etf.rpr.tutorijal10;


public class Grad {
    private int gradID;
    private String naziv;
    private Drzava drzava = null;
    private int brojStanovnika;
    private String slikaPath = "";

    public Grad(int gradID, String naziv, int brojStanovnika, Drzava drzava, String path) {
        this.gradID = gradID;
        this.naziv = naziv;
        this.drzava = drzava;
        this.brojStanovnika = brojStanovnika;
        this.slikaPath = path;
    }

    public Grad() {}

    public int getGradID() {
        return gradID;
    }

    public void setGradID(int gradID) {
        this.gradID = gradID;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public Drzava getDrzava() {
        return drzava;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava = drzava;
    }

    public int getBrojStanovnika() {
        return brojStanovnika;
    }

    public void setBrojStanovnika(int brojStanovnika) {
        this.brojStanovnika = brojStanovnika;
    }

    public String getSlikaPath() {
        return slikaPath;
    }

    public void setSlikaPath(String slikaPath) {
        this.slikaPath = slikaPath;
    }

    @Override
    public String toString() {
        return naziv;
    }
}
