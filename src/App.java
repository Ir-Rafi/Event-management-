import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("button.fxml"));
        Scene scene = new Scene(root);
 

        String css = this.getClass().getResource("style.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        
        stage.setTitle("Login & Register");
        
        
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}