import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("button.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);  

        String css = this.getClass().getResource("style.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        
        stage.setTitle("Login & Register");
        
        
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}