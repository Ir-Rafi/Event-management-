import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MysceneController {
   @FXML
private void handleGetStarted(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("button.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        // Load CSS
        String cssPath = getClass().getResource("login_style.css") != null
                ? getClass().getResource("login_style.css").toExternalForm()
                : null;

        if (cssPath != null) scene.getStylesheets().add(cssPath);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    @FXML
    private void handleNavigation(ActionEvent event) {
        System.out.println("Navigation clicked!");
    }
}
