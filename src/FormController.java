import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FormController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private VBox page1, page2, page3;

    @FXML
    private Button continue1, continue2, continue3;

    @FXML
    public void initialize() {
        // Show page 1 initially
        showPage(1);

        // Button actions
        continue1.setOnAction(e -> showPage(2));
        continue2.setOnAction(e -> showPage(3));
        continue3.setOnAction(e -> finishForm());
    }

    private void showPage(int page) {
        // Hide all pages first
        page1.setVisible(false);
        page2.setVisible(false);
        page3.setVisible(false);
        
        // Show selected page
        if (page == 1) {
            page1.setVisible(true);
        } else if (page == 2) {
            page2.setVisible(true);
        } else if (page == 3) {
            page3.setVisible(true);
        }
    }

    private void finishForm() {
        try {
            // Dashboard load koro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Parent root = loader.load();

            // Get current stage
            Stage stage = (Stage) continue3.getScene().getWindow();
            
            // Create new scene with dashboard
            Scene scene = new Scene(root);
            
            // Dashboard er CSS load koro (jodi thake)
            // scene.getStylesheets().add(getClass().getResource("dashboard.css").toExternalForm());
            
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.setMaximized(true);  // Maximize
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: Dashboard load korte parche na!");
        }
    }
    
    // Static method to load login page
    public static void loadLoginPage(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(FormController.class.getResource("login.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(FormController.class.getResource("login.css").toExternalForm());
            
            stage.setScene(scene);
            stage.setMaximized(true);  // Maximize
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: Login page load korte parche na!");
        }
    }
}