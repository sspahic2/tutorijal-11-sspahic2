package ba.unsa.etf.rpr.tutorijal10;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
public class GlavnaController implements Initializable {

    private GeografijaDAO model;
    ObservableList<Grad> gradObservableList;
    @FXML TableView<Grad> tableViewGradovi;
    @FXML TableColumn<Grad, Integer> colGradId;
    @FXML TableColumn<Grad, String> colGradNaziv;
    @FXML TableColumn<Grad, Integer> colGradStanovnika;
    @FXML TableColumn<Grad, String> colGradDrzava;


    public GlavnaController(GeografijaDAO model) {
        this.model = model;
    }

    public GlavnaController() {
        GeografijaDAO.removeInstance();
        model = GeografijaDAO.getInstance();
    }

    // Metoda za potrebe testova, vraća bazu u polazno stanje
    public void resetujBazu() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();
        GeografijaDAO dao = GeografijaDAO.getInstance();
    }

    @FXML
    public void initialize(URL location, ResourceBundle resourceBundle) {
        GeografijaDAO.removeInstance();
        model = GeografijaDAO.getInstance();
        colGradId.setCellValueFactory(new PropertyValueFactory<>("gradID"));
        colGradNaziv.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        colGradStanovnika.setCellValueFactory(new PropertyValueFactory<>("brojStanovnika"));
        colGradDrzava.setCellValueFactory(new PropertyValueFactory<>("drzava"));
        gradObservableList = FXCollections.observableArrayList(model.gradovi());
       // tableViewGradovi.getColumns().setAll(colGradId, colGradNaziv, colGradStanovnika, colGradDrzava);
        tableViewGradovi.setItems(gradObservableList);
    }

    public void otvoriProzorDrzaveAction(ActionEvent actionEvent) {
            GeografijaDAO.removeInstance();
            model = GeografijaDAO.getInstance();
            DrzavaController ctrl = new DrzavaController(null, model.gradovi());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/drzava.fxml"));
            loader.setController(ctrl);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
            stage.setTitle("Drzave");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.show();
    }

    public void otvoriProzorGradoviAction(ActionEvent actionEvent) {
        GeografijaDAO.removeInstance();
        GradController controller = new GradController(null, model.drzave());
        FXMLLoader load = new FXMLLoader(getClass().getResource("/grad.fxml"));
        load.setController(controller);
        Parent root = null;
        try {
            root = load.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("Gradovi");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.show();
    }

}
