import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller {

    @FXML
    private Text continueText;

    @FXML
    public void initialize() {
        // Fade animation for continueText
        FadeTransition ft = new FadeTransition(Duration.seconds(1), continueText);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(FadeTransition.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();

        // Listen for keyboard press after scene loads
        continueText.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleContinue);
            } else if (oldScene != null) {
                oldScene.setOnKeyPressed(null);
            }
        });
    }

    // For pressing any key
    private void handleContinue(KeyEvent event) {
        try {
            Stage stage = (Stage) continueText.getScene().getWindow();
            loadLoginPage(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // For button click (onAction="#handleGetStarted")
    @FXML
    private void handleGetStarted(ActionEvent event) {
        try {
            Stage stage = (Stage) continueText.getScene().getWindow();
            loadLoginPage(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Common function for loading login page
    public static void loadLoginPage(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Controller.class.getResource("button.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                Controller.class.getResource("login_style.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.setTitle("Login Page");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
