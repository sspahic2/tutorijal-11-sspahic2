package ba.unsa.etf.rpr.tutorijal10;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
    ObservableList<Grad> gradObservableList = FXCollections.observableArrayList();
    private ObservableList<Drzava> drzavaObservableList;
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
        File dbfile = new File("baza");
        dbfile.delete();
        GeografijaDAO dao = GeografijaDAO.getInstance();
    }

    @FXML
    public void initialize(URL location, ResourceBundle resourceBundle) {
        resetujBazu();
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
        try {
            GeografijaDAO.removeInstance();
            model = GeografijaDAO.getInstance();
            DrzavaController ctrl = new DrzavaController(null, model.gradovi());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/drzava.fxml"));
            loader.setController(ctrl);
        Parent root;
            root = loader.load();
        Stage stage = new Stage();
            stage.setTitle("Drzave");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void otvoriProzorGradoviAction(ActionEvent actionEvent) {
        try {
            GeografijaDAO.removeInstance();
            GradController controller = new GradController(null, (model = GeografijaDAO.getInstance()).drzave());
            FXMLLoader load = new FXMLLoader(getClass().getResource("/grad.fxml"));
            load.setController(controller);
            Parent root;
            root = load.load();
            Stage stage = new Stage();
            stage.setTitle("Gradovi");
            stage.setOnHiding(windowEvent -> {
                if(controller.getGrad() != null) {
                    tableViewGradovi.getItems().add(controller.getGrad());
                }
            });
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void izmijeniGradAction(ActionEvent actionEvent) {
        try {
            GeografijaDAO.removeInstance();
            GradController cont = new GradController(tableViewGradovi.getSelectionModel().getSelectedItem(), (model = GeografijaDAO.getInstance()).drzave());
            FXMLLoader l = new FXMLLoader(getClass().getResource("/grad.fxml"));
            l.setController(cont);
            Parent root;
            root = l.load();
            Stage stage = new Stage();
            stage.setTitle("Gradovi");
            stage.setOnHiding(windowEvent -> {
                for(Grad g: tableViewGradovi.getItems()) {
                    if(g.getNaziv().equals(cont.getGrad().getNaziv())) {
                        tableViewGradovi.getItems().remove(g);
                        tableViewGradovi.getItems().add(cont.getGrad());
                        break;
                    }
                }
            });
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void obrisiGradAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Obrisati " + tableViewGradovi.getSelectionModel().getSelectedItem().getNaziv() + "?",
                ButtonType.OK, ButtonType.CLOSE);
        alert.showAndWait();
        if(alert.getResult().getText().equals("OK")) {
            //Boze samo mi ovo objasni zasto izbacuje da ne moze izvrsiti statement
            //SKONTOOOOOOO jebeni getinstance
            GeografijaDAO.removeInstance();
            model = GeografijaDAO.getInstance();
            model.obrisiGrad(tableViewGradovi.getSelectionModel().getSelectedItem().getNaziv());
            tableViewGradovi.getItems().remove(tableViewGradovi.getSelectionModel().getSelectedItem());
        }
        else {
            alert.close();
        }
    }
}
