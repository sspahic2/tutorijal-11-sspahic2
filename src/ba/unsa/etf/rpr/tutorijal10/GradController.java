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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradController implements Initializable {
    @FXML TextField fieldNaziv;
    @FXML TextField fieldBrojStanovnika;
    @FXML ChoiceBox<Drzava> choiceDrzava;
    GeografijaDAO model;
    ObservableList<Drzava> observableList = FXCollections.observableArrayList();
    private Grad imaZaIzmjenu;

    public GradController(Grad o, ArrayList<Drzava> drzave) {
            imaZaIzmjenu = o;
    }

    public Grad getGrad() {
        return imaZaIzmjenu;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GeografijaDAO.removeInstance();
        observableList.addAll((model = GeografijaDAO.getInstance()).drzave());
        choiceDrzava.setItems(observableList);
        choiceDrzava.getSelectionModel().selectFirst();

        fieldNaziv.getStyleClass().removeAll("nazivNijePrazan");
        fieldNaziv.getStyleClass().add("nazivPrazan");

        fieldBrojStanovnika.getStyleClass().removeAll("brojPozitivan");
        fieldBrojStanovnika.getStyleClass().add("brojNegativan");

        if(imaZaIzmjenu != null) {
            fieldNaziv.setText(String.valueOf(imaZaIzmjenu.getNaziv()));
            fieldBrojStanovnika.setText(String.valueOf(imaZaIzmjenu.getBrojStanovnika()));

            fieldNaziv.getStyleClass().removeAll("nazivPrazan");
            fieldNaziv.getStyleClass().add("nazivNijePrazan");

            fieldBrojStanovnika.getStyleClass().removeAll("brojNegativan");
            fieldBrojStanovnika.getStyleClass().add("brojPozitivan");
        }

        fieldNaziv.textProperty().addListener((obs, newIme, oldIme) -> {
            if(!fieldNaziv.getText().isEmpty()) {
                fieldNaziv.getStyleClass().removeAll("nazivPrazan");
                fieldNaziv.getStyleClass().add("nazivNijePrazan");
            }
            else if(!newIme.isEmpty()) {
                fieldNaziv.getStyleClass().removeAll("nazivPrazan", "nazivNijePrazan");
                fieldNaziv.getStyleClass().add("nazivNijePrazan");
            }
            else {
                fieldNaziv.getStyleClass().removeAll("nazivNijePrazan", "nazivPrazan");
                fieldNaziv.getStyleClass().add("nazivPrazan");
            }
        });

        fieldBrojStanovnika.textProperty().addListener((observable, newIme, oldIme ) -> {
            Pattern pattern = Pattern.compile("^\\d+$");
            Matcher matcher = pattern.matcher(fieldBrojStanovnika.getText());
            if(matcher.matches()) {
                fieldBrojStanovnika.getStyleClass().removeAll("brojNegativan");
                fieldBrojStanovnika.getStyleClass().add("brojPozitivan");
            }
            else {
                fieldBrojStanovnika.getStyleClass().removeAll("brojPozitivan");
                fieldBrojStanovnika.getStyleClass().add("brojNegativan");
            }
        });

    }

    public void pritisnutOkAction(ActionEvent actionEvent) {
        if(imaZaIzmjenu != null) {
            imaZaIzmjenu.setNaziv(fieldNaziv.getText());
            imaZaIzmjenu.setBrojStanovnika(Integer.parseInt(fieldBrojStanovnika.getText()));
            model.izmijeniGrad(imaZaIzmjenu);
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            stage.close();
        }
            if (!fieldNaziv.getText().isEmpty() && fieldBrojStanovnika.getStyleClass().contains("brojPozitivan")) {
                imaZaIzmjenu = new Grad(-1, fieldNaziv.getText(), Integer.parseInt(fieldBrojStanovnika.getText()), choiceDrzava.getSelectionModel().getSelectedItem());
                model.dodajGrad(imaZaIzmjenu);
                Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                stage.close();
            }
    }

    public void pritisnutCancelAction(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

}
