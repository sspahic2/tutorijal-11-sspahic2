package ba.unsa.etf.rpr.tutorijal10;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DrzavaController implements Initializable {

    @FXML TextField fieldNaziv;
    @FXML ChoiceBox<Grad> choiceGrad;
    ObservableList<Grad> gradovi = FXCollections.observableArrayList();
    GeografijaDAO model;
    Drzava pritisnutOk = null;

    public DrzavaController(Object o, ArrayList<Grad> gradovi) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GeografijaDAO.removeInstance();
        gradovi.addAll((model = GeografijaDAO.getInstance()).gradovi());
        choiceGrad.setItems(gradovi);
        choiceGrad.getSelectionModel().selectFirst();
        fieldNaziv.getStyleClass().removeAll("poljeNijePrazno");
        fieldNaziv.getStyleClass().add("poljePrazno");

        fieldNaziv.textProperty().addListener((obs, oldIme, newIme) -> {
        if(!newIme.isEmpty()) {
            fieldNaziv.getStyleClass().removeAll("poljePrazno", "poljeNijePrazno");
            fieldNaziv.getStyleClass().add("poljeNijePrazno");
        }
        else {
            fieldNaziv.getStyleClass().removeAll("poljeNijePrazno", "poljePrazno");
            fieldNaziv.getStyleClass().add("poljePrazno");
        }
    });
    }

    public void buttonOkAction(ActionEvent actionEvent) {
        if(fieldNaziv.getStyleClass().contains("poljeNijePrazno")) {
            GeografijaDAO.removeInstance();
            model = GeografijaDAO.getInstance();
            Drzava drzava = new Drzava(-1, fieldNaziv.getText(), choiceGrad.getSelectionModel().getSelectedItem());
            model.dodajDrzavu(drzava);
            pritisnutOk = drzava;
            Stage stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    public void buttonCancelAction(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public Drzava getPritisnutOk() {
        return pritisnutOk;
    }
}
