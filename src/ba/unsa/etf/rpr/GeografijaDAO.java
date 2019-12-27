package ba.unsa.etf.rpr;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class GeografijaDAO {

    private static GeografijaDAO instance = null;
    private PreparedStatement dajGlavniGradStatement, dajBrojStanovnikaStatement, dajNazivGradaStatement, dajGrad;
    private Connection conn;

    private GeografijaDAO(){

        try {
           conn = DriverManager.getConnection("jdbc:sqlite:C:/Users/Lenovo/IdeaProjects/tutorijal-9-sspahic2/baza");
            dajGlavniGradStatement = conn.prepareStatement("SELECT g.naziv FROM drzava d, grad g WHERE d.glavni_grad = g.gradID");
            dajBrojStanovnikaStatement = conn.prepareStatement("SELECT broj_stanovnika from grad");
            dajNazivGradaStatement = conn.prepareStatement("SELECT naziv FROM grad");
            dajGrad = conn.prepareStatement("SELECT id, naziv, drzava, broj_stanovnika FROM grad");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        ArrayList<Grad> sortiranaListaGradova = new ArrayList<>();

        try {
            ResultSet resultSet = dajGrad.executeQuery();
            while(resultSet.next()) {
                gradovi.add(new Grad(resultSet.getInt(1), resultSet.getString(2), (Drzava) resultSet.getObject(3),
                        resultSet.getInt(4)));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

         sortiranaListaGradova = gradovi.stream().sorted(Comparator.comparingInt(Grad::getBrojStanovnika)).collect(Collectors.toCollection(ArrayList::new));
        return sortiranaListaGradova;
    }
}

