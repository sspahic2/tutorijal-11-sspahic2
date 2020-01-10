package ba.unsa.etf.rpr.tutorijal10;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GeografijaDAO {

    private static GeografijaDAO instance = null;
    private PreparedStatement dajSveGradoveStatement, dajSveDrzaveStatement, izmijeniGradStatement, obrisiDrzavuStatement;
    private PreparedStatement glavniGradUpit, dajNoviIDGrad, dajNoviIDDrzava, dodajNoviGradStatement, dodajNovuDrzavuStatement;
    private Connection conn;

    private GeografijaDAO() {

        try {
           conn = DriverManager.getConnection("jdbc:sqlite:baza");
            obrisiBazu();
            regenerisiBazu();
            dajSveGradoveStatement = conn.prepareStatement("SELECT gradID, naziv, broj_stanovnika, drzava FROM grad ORDER BY broj_stanovnika DESC");
            dajSveDrzaveStatement = conn.prepareStatement("SELECT d.drzavaID, d.naziv, d.glavni_grad FROM drzava d");
            izmijeniGradStatement = conn.prepareStatement("UPDATE grad SET naziv = ?, broj_stanovnika = ? WHERE drzava = ?");
            obrisiDrzavuStatement = conn.prepareStatement("DELETE FROM drzava WHERE naziv = ?");
            dajNoviIDGrad = conn.prepareStatement("SELECT MAX(gradID) + 1 FROM grad");
            dajNoviIDDrzava = conn.prepareStatement("SELECT MAX(drzavaID) + 1 FROM drzava");
            dodajNoviGradStatement = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, ?, ?)");
            dodajNovuDrzavuStatement = conn.prepareStatement("INSERT INTO drzava VALUES (?, ?, ?)");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            glavniGradUpit = conn.prepareStatement("SELECT * FROM grad, drzava WHERE grad.drzava = drzava.drzavaID and drzava.glavni_grad = ?");
        } catch (SQLException e) {
                obrisiBazu();
                regenerisiBazu();
            e.printStackTrace();
        }
    }

    private void obrisiBazu() {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("DROP TABLE drzava;");
            stmt.execute("DROP TABLE grad;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void regenerisiBazu() {
        Scanner ulaz = null;
        try {
            ulaz = new Scanner(new FileInputStream("baza.sql"));
            String sqlupit = "";
            while(ulaz.hasNext()) {
                sqlupit += ulaz.nextLine();
                if(sqlupit.charAt(sqlupit.length() - 1) == ';') {
                    Statement stmt = conn.createStatement();
                    stmt.execute(sqlupit);
                    sqlupit = "";
                }
            }
        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        ulaz.close();
    }

    public static void removeInstance() {
        if (instance != null) {
            try {
                instance.conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        instance = null;
    }

    public static GeografijaDAO getInstance() {
        if (instance == null) instance = new GeografijaDAO();
        return instance;
    }

    public ArrayList<Grad> gradovi() {
        ArrayList<Grad> gradovi = new ArrayList<>();
        ArrayList<Drzava> drzave = new ArrayList<>();
        try {
            ResultSet rDrzave = dajSveDrzaveStatement.executeQuery();
            ResultSet rGradovi = dajSveGradoveStatement.executeQuery();

            Drzava d = null;

            while(rDrzave.next()) {
                d = new Drzava(rDrzave.getInt(1), rDrzave.getString(2), null);
                dajGrad(rGradovi, rDrzave.getInt(3), d);
                drzave.add(d);
            }
            ResultSet rrGradovi = dajSveGradoveStatement.executeQuery();
            while(rrGradovi.next()) {
                Grad temp = new Grad(rrGradovi.getInt(1), rrGradovi.getString(2),
                        rrGradovi.getInt(3), null);
                for(Drzava t : drzave) {
                    if(rrGradovi.getInt(4) == t.getDrzavaID()) {
                        temp.setDrzava(t);
                        gradovi.add(temp);
                    }
                }
            }
            return gradovi;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Grad dajGrad(ResultSet rGradovi, int ggID, Drzava d) {
        try {
            while(rGradovi.next()) {
                if(rGradovi.getInt(1) == ggID) {
                    Grad t = new Grad(rGradovi.getInt(1), rGradovi.getString(2), rGradovi.getInt(3), d);
                    d.setGlavniGrad(t);
                    return t;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Grad glavniGrad(String drzava) {

        try {
            ResultSet rDrzave = dajSveDrzaveStatement.executeQuery();
            ResultSet rGradovi = dajSveGradoveStatement.executeQuery();
            Drzava d;
            while(rDrzave.next()) {
                if(rDrzave.getString(2).equals(drzava)) {
                    d = new Drzava(rDrzave.getInt(1), rDrzave.getString(2), null);
                    return dajGrad(rGradovi, rDrzave.getInt(3), d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void obrisiDrzavu(String drzava) {
        try {
            ResultSet r = dajSveDrzaveStatement.executeQuery();
            while(r.next()) {
                if(r.getString(2).equals(drzava)) {
                    obrisiDrzavuStatement.setString(1, drzava);
                    obrisiDrzavuStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Drzava nadjiDrzavu(String drzava) {
        try {
            ResultSet rDrzave = dajSveDrzaveStatement.executeQuery();
            ResultSet rGradovi = dajSveGradoveStatement.executeQuery();

            Drzava d = null;

            while(rDrzave.next()) {
                if(rDrzave.getString(2).equals(drzava)) {
                d = new Drzava(rDrzave.getInt(1), rDrzave.getString(2), null);
                dajGrad(rGradovi, rDrzave.getInt(3), d);
                return d;
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void dodajGrad(Grad grad) {
        try {
            ResultSet rDrzave = dajSveDrzaveStatement.executeQuery();
            ResultSet novi = dajNoviIDGrad.executeQuery();
            novi.next();
            grad.setGradID(novi.getInt(1));

            while(rDrzave.next()) {
                if(rDrzave.getString(2).equals(grad.getDrzava().getNaziv())) {
                    dodajNoviGradStatement.setInt(4, rDrzave.getInt(1));
                    dodajNoviGradStatement.setInt(1, grad.getGradID());
                    dodajNoviGradStatement.setInt(3, grad.getBrojStanovnika());
                    dodajNoviGradStatement.setString(2, grad.getNaziv());
                    dodajNoviGradStatement.executeUpdate();
                    return;
                }
            }
            grad.getDrzava().setGlavniGrad(new Grad());
            dodajNoviGradStatement.setInt(1, grad.getGradID());
            dodajNoviGradStatement.setString(2, grad.getNaziv());
            dodajNoviGradStatement.setInt(3, grad.getBrojStanovnika());
            dodajNoviGradStatement.setInt(4, grad.getDrzava().getDrzavaID());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dodajDrzavu(Drzava drzava) {
        try {
            ResultSet novi = dajNoviIDDrzava.executeQuery();
            ResultSet rGrad = dajSveGradoveStatement.executeQuery();
            novi.next();
            drzava.setDrzavaID(novi.getInt(1));

            dodajGrad(drzava.getGlavniGrad());
            dodajNovuDrzavuStatement.setInt(1, drzava.getDrzavaID());
            dodajNovuDrzavuStatement.setString(2, drzava.getNaziv());
            dodajNovuDrzavuStatement.setInt(3, drzava.getGlavniGrad().getGradID());
            return;

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void izmijeniGrad(Grad grad) {
        try {
            ResultSet r = dajSveGradoveStatement.executeQuery();
            while(r.next()) {
                if(r.getInt(1) == grad.getGradID()) {
                    ResultSet re = dajSveDrzaveStatement.executeQuery();
                    while(re.next()) {
                        if (r.getInt(4) == re.getInt(1)) {
                            izmijeniGradStatement.setInt(3, re.getInt(1));
                            izmijeniGradStatement.setString(1, grad.getNaziv());
                            izmijeniGradStatement.setInt(2, grad.getBrojStanovnika());
                            izmijeniGradStatement.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Grad nadjiGrad(String imeGrada) {
        return null;
    }

    public ArrayList<Drzava> drzave() {
        return null;
    }
}

