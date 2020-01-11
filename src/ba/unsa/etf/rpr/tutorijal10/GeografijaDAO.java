package ba.unsa.etf.rpr.tutorijal10;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GeografijaDAO {

    private static GeografijaDAO instance = null;
    private PreparedStatement glavniGradUpit, dajDrzavuUpit, obrisiDrzavuUpit, obrisiGradoveZaDrzavuUpit, nadjiDrzavuUpit,
            dajGradoveUpit, dodajGradUpit, odrediIdGradUpit, dodajDrzavuUpit, odrediIdDrzavaUpit, promijeniGradUpit,
            dajGradUpit, dajDrzaveUpit, dajGradPomocuImenaUpit, obrisiGradUpit, obrisiDrzaveZaGradUpit;
    private Connection conn;

    public static void removeInstance() {
        if(instance == null) {
            return;
        }
        instance.close();
        instance = null;
    }

    private void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static GeografijaDAO getInstance() {
        if (instance == null) instance = new GeografijaDAO();
        return instance;
    }

    private GeografijaDAO() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:baza");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            glavniGradUpit = conn.prepareStatement("SELECT grad.gradID, grad.naziv, grad.broj_stanovnika, grad.drzava" +
                    " FROM grad, drzava WHERE grad.drzava = drzava.drzavaID AND drzava.naziv = ?");
        } catch (SQLException e) {
            regenerisiBazu();
            try {
                glavniGradUpit = conn.prepareStatement("SELECT grad.gradID, grad.naziv, grad.broj_stanovnika, grad.drzava" +
                        " FROM grad, drzava WHERE grad.drzava = drzava.drzavaID AND drzava.naziv = ?");
            } catch (SQLException el) {
                el.printStackTrace();
            }
        }
        try {
            dajDrzavuUpit = conn.prepareStatement("SELECT drzavaID, naziv, glavni_grad FROM drzava WHERE drzavaID = ?");
            dajGradUpit = conn.prepareStatement("SELECT gradID, naziv, broj_stanovnika, drzava FROM grad WHERE gradID = ?");
            dajGradPomocuImenaUpit = conn.prepareStatement("SELECT gradID, naziv, broj_stanovnika, drzava FROM grad WHERE naziv = ?");
            obrisiDrzavuUpit = conn.prepareStatement("DELETE FROM drzava WHERE naziv = ?");
            obrisiGradUpit = conn.prepareStatement("DELETE FROM grad WHERE naziv = ?");
            obrisiGradoveZaDrzavuUpit = conn.prepareStatement("DELETE FROM grad WHERE drzava = ?");
            obrisiDrzaveZaGradUpit = conn.prepareStatement("DELETE FROM drzava WHERE glavni_grad = ?");
            nadjiDrzavuUpit = conn.prepareStatement("SELECT * FROM drzava WHERE naziv = ?");
            dajGradoveUpit = conn.prepareStatement("SELECT * from grad ORDER BY broj_stanovnika DESC");
            dajDrzaveUpit = conn.prepareStatement("SELECT drzavaID, naziv, glavni_grad FROM drzava");
            dodajGradUpit = conn.prepareStatement("INSERT INTO grad VALUES(?, ?, ?, ?)");
            odrediIdGradUpit = conn.prepareStatement("SELECT MAX(gradID) + 1 FROM grad");
            odrediIdDrzavaUpit = conn.prepareStatement("SELECT MAX(drzavaID) + 1 FROM drzava");
            dodajDrzavuUpit = conn.prepareStatement("INSERT INTO drzava VALUES(?, ?, ?)");
            promijeniGradUpit = conn.prepareStatement("UPDATE grad SET naziv = ?, broj_stanovnika = ?, drzava = ? WHERE gradID = ?");
        } catch (SQLException e) {
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
                Statement stmt = conn.createStatement();
                stmt.execute(sqlupit);
                sqlupit = "";
            }
        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        ulaz.close();
    }


    public ArrayList<Grad> gradovi() {
        ArrayList<Grad> gradoviRezultat = new ArrayList<>();
        try {
            ResultSet rs = dajGradoveUpit.executeQuery();
            while(rs.next()) {
                Grad grad = dajGradIzResultSeta(rs);
                gradoviRezultat.add(grad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gradoviRezultat;
    }

    public Grad glavniGrad(String drzava) {
        try {
            glavniGradUpit.setString(1, drzava);
            ResultSet rs = glavniGradUpit.executeQuery();
            if(!rs.next()) {
                return null;
            }
            return dajGradIzResultSeta(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Grad dajGradIzResultSeta(ResultSet rs) throws SQLException {
        Grad grad = new Grad(rs.getInt(1), rs.getString(2), rs.getInt(3), null);
        grad.setDrzava(dajDrzavu(rs.getInt(4), grad));
        return grad;
    }

    private Drzava dajDrzavu(int idDrzave, Grad g) {
        try {
            dajDrzavuUpit.setInt(1, idDrzave);
            ResultSet rs = dajDrzavuUpit.executeQuery();
            if(!rs.next()) {
                return null;
            }
            return dajDrzavuIzResultSeta(rs, g);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Drzava dajDrzavuIzResultSeta(ResultSet rs, Grad g) throws SQLException {
        return new Drzava(rs.getInt(1), rs.getString(2), g);
    }

    public void obrisiDrzavu(String drzava) {
        try {
            nadjiDrzavuUpit.setString(1, drzava);
            ResultSet rs = nadjiDrzavuUpit.executeQuery();
            if(!rs.next()) {
                return;
            }
            Drzava d = dajDrzavuIzResultSeta(rs, null);
            obrisiGradoveZaDrzavuUpit.setInt(1, d.getDrzavaID());
            obrisiGradoveZaDrzavuUpit.executeUpdate();

            obrisiDrzavuUpit.setInt(1, d.getDrzavaID());
            obrisiDrzavuUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Drzava nadjiDrzavu(String drzava) {
        try {
            nadjiDrzavuUpit.setString(1, drzava);
            ResultSet rs = nadjiDrzavuUpit.executeQuery();
            if(!rs.next()) {
                return null;
            }
            return dajDrzavuIzResultSeta(rs, dajGrad(rs.getInt(3)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Grad dajGrad(int idGrada) {
        try {
            dajGradUpit.setInt(1, idGrada);
            ResultSet rs = dajGradUpit.executeQuery();
            if(!rs.next()) {
                return null;
            }
            return dajGradIzResultSeta(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void dodajGrad(Grad grad) {
        try {
            ResultSet rs = odrediIdGradUpit.executeQuery();
            int id = 1;
            if(rs.next()) {
                id = rs.getInt(1);
            }
            dodajGradUpit.setInt(1, id);
            dodajGradUpit.setString(2, grad.getNaziv());
            dodajGradUpit.setInt(3, grad.getBrojStanovnika());
            dodajGradUpit.setInt(4, grad.getDrzava().getDrzavaID());
            dodajGradUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dodajDrzavu(Drzava drzava) {
        try {
            ResultSet rs = odrediIdDrzavaUpit.executeQuery();
            int id = 1;
            if(rs.next()) {
                id = rs.getInt(1);
            }
            dodajDrzavuUpit.setInt(1, id);
            dodajDrzavuUpit.setString(2, drzava.getNaziv());
            dodajDrzavuUpit.setInt(3, drzava.getGlavniGrad().getGradID());
            dodajDrzavuUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void izmijeniGrad(Grad grad) {
        try {
            promijeniGradUpit.setString(1, grad.getNaziv());
            promijeniGradUpit.setInt(2, grad.getBrojStanovnika());
            promijeniGradUpit.setInt(3, grad.getDrzava().getDrzavaID());
            promijeniGradUpit.setInt(4, grad.getGradID());
            promijeniGradUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Drzava> drzave() {
        ArrayList<Drzava> rezultatDrzava = new ArrayList<>();
        try {
            ResultSet rs = dajDrzaveUpit.executeQuery();
            while(rs.next()) {
                Drzava drzava = new Drzava(rs.getInt(1), rs.getString(2), null);
                drzava.setGlavniGrad(dajGrad(rs.getInt(3)));
                rezultatDrzava.add(drzava);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultatDrzava;
    }

    public Grad nadjiGrad(String grad) {
        Grad rezultat = new Grad();
        try {
            dajGradPomocuImenaUpit.setString(1, grad);
            ResultSet rs = dajGradPomocuImenaUpit.executeQuery();
            rezultat = dajGradIzResultSeta(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultat;
    }

    public void obrisiGrad(String nazivGrada) {
        try {
            dajGradPomocuImenaUpit.setString(1, nazivGrada);
            ResultSet rs = dajGradPomocuImenaUpit.executeQuery();
            if(!rs.next()) {
                return;
            }
            Grad g = dajGradIzResultSeta(rs);
            obrisiDrzaveZaGradUpit.setInt(1, g.getGradID());
            obrisiDrzaveZaGradUpit.executeUpdate();

            obrisiGradUpit.setString(1, nazivGrada);
            obrisiGradUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

