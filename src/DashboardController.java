import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Pos;
import java.io.File;

import java.util.List;





public class DashboardController extends Controller {

    @FXML private BorderPane rootPane;
    @FXML private VBox leftPane;
    @FXML private Label nameLabel, emailLabel, deptLabel, sessionLabel;
    @FXML private StackPane profilePicStack;
    @FXML private Pane slideMenuPane;
    @FXML private VBox eventsListBox;

    private final int MENU_WIDTH = 260;
    private boolean menuVisible = false;
    private boolean editingProfile = false;

    private TextField nameField, emailField, deptField, sessionField;
    private Button saveBtn;
    private Label profileInitialLabel;
    private String loggedInUsername;

    // ---------------- INITIALIZATION ----------------
    public void initialize() {
        buildSlideMenu();
        profilePicStack.setOnMouseClicked(this::onProfilePicClicked);

        Platform.runLater(() -> {
            if (loggedInUsername != null && !loggedInUsername.isEmpty()) {
                loadUserProfile();
                buildEventList();
            }
        });
    }

    // ---------------- PROFILE DATA ----------------
    public void setProfile(String name, String email, String dept, String session) {
        nameLabel.setText(Session.getUserName() + " (ID: " + Session.getUserId() + ")");
        emailLabel.setText(email);
        deptLabel.setText("" + dept);
        sessionLabel.setText("" + session);

        if (profileInitialLabel == null) {
            profileInitialLabel = new Label();
            profileInitialLabel.getStyleClass().add("profile-initial");
            profilePicStack.getChildren().clear();
            profilePicStack.getChildren().add(profileInitialLabel);
        }

        if (name != null && !name.isEmpty()) {
            profileInitialLabel.setText(name.substring(0, 1).toUpperCase());
        }
    }

    // ---------------- SIDE MENU ----------------
    private void buildSlideMenu() {
        slideMenuPane.setPrefWidth(MENU_WIDTH);
        slideMenuPane.setTranslateX(-MENU_WIDTH - 5);
        slideMenuPane.setManaged(false);
        slideMenuPane.setVisible(false);
        slideMenuPane.setStyle("-fx-background-color: transparent;");

        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setFillWidth(true);
        box.setPrefWidth(MENU_WIDTH);
        box.setStyle("-fx-background-color: #222;");

        String[] options = {"Dashboard","Edit Profile","Event Portal","Change Password","Sign Out","Help & Support"};

        for (String opt : options) {
            Button btn = new Button(opt);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setMinWidth(MENU_WIDTH - 40);
            btn.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20 10 20; -fx-alignment: CENTER_LEFT;");
            btn.setOnMouseEntered(ev -> btn.setStyle("-fx-background-color: #555; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20 10 20; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;"));
            btn.setOnMouseExited(ev -> btn.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20 10 20; -fx-alignment: CENTER_LEFT;"));
            btn.setOnAction(e -> { handleMenuAction(opt); toggleMenu(); });
            box.getChildren().add(btn);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        box.getChildren().add(spacer);

        Button closeBtn = new Button("❌ Close Menu");
        closeBtn.setMaxWidth(Double.MAX_VALUE);
        closeBtn.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-font-size: 14px;");
        closeBtn.setOnAction(e -> toggleMenu());
        box.getChildren().add(closeBtn);

        slideMenuPane.getChildren().clear();
        slideMenuPane.getChildren().add(box);
    }

    private void handleMenuAction(String opt) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        switch (opt) {
            case "Dashboard":
                buildEventList(); break;
            case "Edit Profile": if(!editingProfile) enableProfileEditing(); break;
            case "Event Portal":
                try { new after_login().openEventPortal(stage, stage.getScene()); } 
                catch(Exception e){ e.printStackTrace(); } break;
            case "Change Password": showPasswordChangeDialog(stage); break;
            case "Sign Out": DashboardController.loadingLoginPage(stage); break;
            default: showAlert(opt + " clicked");
        }
    }

    // ---------------- PROFILE EDITING ----------------
    private void enableProfileEditing() {
        nameField = new TextField(nameLabel.getText());
        emailField = new TextField(emailLabel.getText());
        deptField = new TextField(deptLabel.getText());
        sessionField = new TextField(sessionLabel.getText());

        replaceLabelWithField(nameLabel,nameField);
        replaceLabelWithField(emailLabel,emailField);
        replaceLabelWithField(deptLabel,deptField);
        replaceLabelWithField(sessionLabel,sessionField);

        saveBtn = new Button("💾 Save");
        saveBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 10;");
        saveBtn.setOnAction(ev -> {
            String newUsername=nameField.getText(), newEmail=emailField.getText(),
                   newDept=deptField.getText(), newSession=sessionField.getText();
            boolean updated=DatabaseUtility.updateUserProfile(loggedInUsername,newUsername,newEmail,newDept,newSession);
            if(updated){ loggedInUsername=newUsername; setProfile(newUsername,newEmail,newDept,newSession); showAlert("Profile updated!"); }
            else showAlert("Update failed!");
            saveInlineField(nameLabel,nameField);
            saveInlineField(emailLabel,emailField);
            saveInlineField(deptLabel,deptField);
            saveInlineField(sessionLabel,sessionField);
            ((Pane) saveBtn.getParent()).getChildren().remove(saveBtn); editingProfile=false;
        });
        leftPane.getChildren().add(saveBtn); editingProfile=true;
    }

private void showPasswordChangeDialog(Stage stage) {
    Dialog<ButtonType> pwdDialog = new Dialog<>();
    pwdDialog.initOwner(stage);
    pwdDialog.setTitle("Change Password");

    GridPane grid = new GridPane();
    grid.setHgap(8);
    grid.setVgap(8);

    PasswordField oldPasswordField = new PasswordField();
    PasswordField newPasswordField = new PasswordField();

    grid.addRow(0, new Label("Old Password:"), oldPasswordField);
    grid.addRow(1, new Label("New Password:"), newPasswordField);

    pwdDialog.getDialogPane().setContent(grid);
    pwdDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    // Wait for user input
    pwdDialog.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            String oldPass = oldPasswordField.getText();
            String newPass = newPasswordField.getText();

            // Verify old password first
            if (DatabaseUtility.checkUserExists(loggedInUsername, oldPass)) {
                boolean updated = DatabaseUtility.updatePassword(loggedInUsername, newPass);
                if (updated) {
                    showAlert("Password updated successfully!");
                } else {
                    showAlert("Failed to update password. Try again.");
                }
            } else {
                showAlert("Old password is incorrect!");
            }
        }
    });
}


    @FXML private void toggleMenu() {
        TranslateTransition tt=new TranslateTransition(Duration.millis(250),slideMenuPane);
        if(!menuVisible){ slideMenuPane.setVisible(true); tt.setToX(0); menuVisible=true; }
        else{ tt.setToX(-MENU_WIDTH-5); tt.setOnFinished(e->slideMenuPane.setVisible(false)); menuVisible=false; }
        tt.play();
    }

    private void onProfilePicClicked(MouseEvent e){
        FileChooser fc=new FileChooser(); fc.setTitle("Choose profile picture");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        File file=fc.showOpenDialog(rootPane.getScene().getWindow());
        if(file!=null){
            Image img=new Image(file.toURI().toString(),120,120,true,true);
            ImageView iv=new ImageView(img); iv.setFitWidth(120); iv.setFitHeight(120);
            profilePicStack.getChildren().clear(); profilePicStack.getChildren().add(iv);
        }
    }

    // ---------------- INLINE EDITING ----------------
    private void replaceLabelWithField(Label lbl, TextField tf){
        Pane parent=(Pane) lbl.getParent(); int idx=parent.getChildren().indexOf(lbl);
        parent.getChildren().remove(lbl); parent.getChildren().add(idx,tf);
    }
    private void saveInlineField(Label lbl, TextField tf){
        lbl.setText(tf.getText()); Pane parent=(Pane) tf.getParent(); int idx=parent.getChildren().indexOf(tf);
        parent.getChildren().remove(tf); parent.getChildren().add(idx,lbl);
    }

    private void showAlert(String msg){ new Alert(Alert.AlertType.INFORMATION,msg,ButtonType.OK).show(); }

    // ---------------- LOAD USER PROFILE ----------------
    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;

        if (nameLabel != null) {

            // STEP 1 — Immediately show profile placeholder
            nameLabel.setText("Loading...");
            emailLabel.setText("");
            deptLabel.setText("");
            sessionLabel.setText("");

            // STEP 2 — Smooth fade animation
            FadeTransition ft = new FadeTransition(Duration.millis(400), nameLabel);
            ft.setFromValue(0.3);
            ft.setToValue(1.0);
            ft.play();

            // STEP 3 — Load profile and events in background
            new Thread(() -> {

                // Heavy DB work here (off UI thread)
                String[] details = DatabaseUtility.getUserDetails(username);
                List<EventController.EventData> userEvents =
                        EventController.loadUserEvents(username);

                // STEP 4 — Update UI step-by-step with animations
                Platform.runLater(() -> {

                    // Update actual profile
                    if (details != null)
                        setProfile(details[0], details[1], details[2], details[3]);

                    // STEP 4A — Smoothly show event list placeholder
                    eventsListBox.getChildren().clear();
                    Label loading = new Label("Loading your events...");
                    loading.setStyle("-fx-text-fill: #ccc; -fx-padding: 5;");
                    eventsListBox.getChildren().add(loading);

                    FadeTransition ft2 = new FadeTransition(Duration.millis(400), eventsListBox);
                    ft2.setFromValue(0);
                    ft2.setToValue(1);
                    ft2.play();

                    // STEP 5 — After a small delay, load event cards
                    PauseTransition pause = new PauseTransition(Duration.millis(300));
                    pause.setOnFinished(ev -> {

                        eventsListBox.getChildren().clear();

                        if (userEvents.isEmpty()) {
                            Label empty = new Label("No events assigned.");
                            empty.setStyle("-fx-text-fill: #ccc; -fx-padding: 10;");
                            eventsListBox.getChildren().add(empty);
                            return;
                        }

                        // Add events one by one with a slight delay → feels interactive
                        new Thread(() -> {
                            for (EventController.EventData evData : userEvents) {

                                Platform.runLater(() ->
                                        eventsListBox.getChildren().add(createEventCard(evData))
                                );

                                try { Thread.sleep(120); } catch (Exception ignored) {}
                            }
                        }).start();
                    });
                    pause.play();
                });
            }).start();
        }
    }

    private void loadUserProfile(){
        if(loggedInUsername==null||loggedInUsername.isEmpty()) return;
        String[] details=DatabaseUtility.getUserDetails(loggedInUsername);
        if(details!=null) setProfile(details[0],details[1],details[2],details[3]);
    }

    // ---------------- LOAD EVENTS ----------------
private void buildEventList() {
    eventsListBox.getChildren().clear();

    Label loading = new Label("Loading your events...");
    loading.setStyle("-fx-text-fill: #cccccc; -fx-padding: 10;");
    eventsListBox.getChildren().add(loading);

    new Thread(() -> {
        List<EventController.EventData> userEvents = EventController.loadUserEvents(loggedInUsername);

        Platform.runLater(() -> {
            eventsListBox.getChildren().clear();

            if (userEvents.isEmpty()) {
                Label empty = new Label("No events assigned.");
                empty.setStyle("-fx-text-fill: #ccc; -fx-padding: 10;");
                eventsListBox.getChildren().add(empty);
                return;
            }

            for (EventController.EventData event : userEvents) {
                eventsListBox.getChildren().add(createEventCard(event));
            }
        });

    }).start();
}


private VBox createEventCard(EventController.EventData event) {
    VBox card = new VBox(8);
    card.setPadding(new Insets(15));
    card.setAlignment(Pos.TOP_CENTER);
    card.setPrefWidth(150);  // Ensure consistent width for square cards
    card.setMaxWidth(150);   // Prevent it from resizing beyond this

    // Set background color of card itself
    String bgColor = event.color != null ? event.color : "#444";  // Set background color

    // Style the card with a background color and border
    card.setStyle("-fx-background-color: " + bgColor + "AA;" +
                  "-fx-background-radius: 12;" +
                  "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 12,0,0,6);");

    // Add event image if exists
    if (event.eventImagePath != null && event.eventImagePath.exists()) {
        try {
            Image img = new Image(event.eventImagePath.toURI().toString(), 150, 150, true, true);
            ImageView iv = new ImageView(img);
            iv.setFitWidth(150);   // Ensure image is square
            iv.setFitHeight(150);  // Ensure image is square
            Rectangle clip = new Rectangle(150, 150);
            clip.setArcWidth(20);  // Rounded corners
            clip.setArcHeight(20); // Rounded corners
            iv.setClip(clip);
            card.getChildren().add(iv); // Add image directly to card
        } catch (Exception e) {
            // If image fails to load, fallback to colored background
        }
    }

    // Add event name
    Label name = new Label("📌 " + event.name);
    name.setStyle("-fx-text-fill:white;-fx-font-size:16px;-fx-font-weight:bold;");
    card.getChildren().add(name);

    // Add mouse hover effects for interactivity
    card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: " + bgColor + "CC; -fx-background-radius:12;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.9),18,0,0,8); -fx-scale-x:1.03; -fx-scale-y:1.03;"));
    card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + bgColor + "AA; -fx-background-radius:12;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.7),12,0,0,6); -fx-scale-x:1; -fx-scale-y:1;"));
    card.setOnMouseClicked(e -> System.out.println("Clicked event: " + event.name));

    return card;
}


}


