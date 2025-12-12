import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class chatWindows {

    // Server-side chat (Main Organizer)
    public static void openServerChat(String displayName) {
        try {
            FXMLLoader loader = new FXMLLoader(chatWindows.class.getResource("Chat.fxml"));
            Parent root = loader.load();

            ChatController controller = loader.getController();
            controller.setLoggedInUsername(displayName);
            controller.startServer();

            Stage stage = new Stage();
            stage.setTitle("Organizer Chat (Server) - " + displayName);
            stage.setScene(new Scene(root, 480, 600));
            stage.setOnCloseRequest(e -> {
                // Clean up server when window closes
                controller.stopServer();
            });
            stage.show();
            
            System.out.println("✅ Server chat window opened for: " + displayName);
        } catch (Exception e) {
            System.out.println("❌ Error opening server chat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Client-side chat (Sub Organizer / Viewer)
    public static void openClientChat(String displayName) {
        openClientChat(displayName, "localhost", 1234);
    }
    
    // Overloaded method with custom host and port
    public static void openClientChat(String displayName, String host, int port) {
        try {
            System.out.println("Opening client chat for: " + displayName);
            System.out.println("Connecting to: " + host + ":" + port);
            
            // Load FXML
            FXMLLoader loader = new FXMLLoader(
                    chatWindows.class.getResource("ChatClient.fxml")
            );

            Parent root = loader.load();

            // Get controller and set name + connect
            ClientController controller = loader.getController();
            controller.setDisplayName(displayName);
            controller.connectToServer(host, port);

            // Open window
            Stage stage = new Stage();
            stage.setTitle("Chat - " + displayName);
            stage.setScene(new Scene(root, 480, 600));
            stage.show();
            
            System.out.println("✅ Client chat window opened for: " + displayName);
        } catch (Exception e) {
            System.out.println("❌ Error opening client chat: " + e.getMessage());
            e.printStackTrace();
        }
    }
}