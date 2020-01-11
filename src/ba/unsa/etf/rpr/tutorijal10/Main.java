package ba.unsa.etf.rpr.tutorijal10;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class Main extends Application {

//   public static void main(String[] args) {
////        System.out.println("Gradovi su:\n" + ispisiGradove());
//  //      glavniGrad();
//    GeografijaDAO dao = GeografijaDAO.getInstance();
//    }
    private GeografijaDAO model;
    @Override
    public void start(Stage primaryStage) throws Exception{
        model = GeografijaDAO.getInstance();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/glavna.fxml"));
        loader.setController(new GlavnaController(model));
        Parent root = loader.load();
        primaryStage.setTitle("Korisnici");
        primaryStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        primaryStage.show();
        //primaryStage.setResizable(false);
    }


    public static void main(String[] args) {
        launch(args);
        /*Grad g = new Grad(1, "Sarajevo", 123, null);
        Drzava d = new Drzava(1, "Bosna i Hercegovina", g);
        g.setDrzava(d);

        GeografijaDAO.getInstance().dodajDrzavu(d);
        System.out.println("Kraj");

        for(Grad grad : GeografijaDAO.getInstance().gradovi())
            System.out.println(grad);*/
    }
}
