import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.List;

public class DashboardController {

    // ======= FXML LINKS =======
    @FXML private BorderPane rootPane;

    // slideMenuPane is the StackPane overlay sibling of rootPane in the FXML StackPane root
    @FXML private StackPane slideMenuPane;
    @FXML private Region menuOverlay;
    @FXML private VBox slideMenuBox;

    @FXML private Label nameLabel, emailLabel, deptLabel, sessionLabel;
    @FXML private StackPane profilePicStack;

    @FXML private VBox eventsListBox;
    @FXML private VBox rightPane;

    private boolean menuVisible = false;
    private final int MENU_WIDTH = 280; // must match translateX initial in FXML

    private String loggedInUsername;
    private boolean editingProfile = false;

    private TextField nameField, emailField, deptField, sessionField;
    private Button saveBtn;
    private Label profileInitialLabel;

    // ---------------- INITIALIZE ----------------
    public void initialize() {

        // ensure slideMenuPane covers the rootPane area
        // some controls may not be ready until layout pass - use Platform.runLater to bind safely
        Platform.runLater(() -> {
            if (rootPane != null && slideMenuPane != null) {
                // bind overlay size to rootPane so overlay covers the whole scene
                slideMenuPane.prefWidthProperty().bind(rootPane.widthProperty());
                slideMenuPane.prefHeightProperty().bind(rootPane.heightProperty());

                // ensure overlay region covers everything
                menuOverlay.prefWidthProperty().bind(slideMenuPane.widthProperty());
                menuOverlay.prefHeightProperty().bind(slideMenuPane.heightProperty());

                // align slideMenuBox to left of the StackPane so translateX works nicely
                StackPane.setAlignment(slideMenuBox, Pos.CENTER_LEFT);

                // set menu box width explicitly
                slideMenuBox.setPrefWidth(MENU_WIDTH);
                slideMenuBox.setMaxWidth(MENU_WIDTH);

                // start hidden to the left
                slideMenuBox.setTranslateX(-MENU_WIDTH);

                // hide overlay visuals until needed
                slideMenuPane.setVisible(false);
                slideMenuPane.setMouseTransparent(true);
                menuOverlay.setVisible(false);
            }

            // wire profile pic click
            if (profilePicStack != null) profilePicStack.setOnMouseClicked(this::onProfilePicClicked);

            buildMenuButtons();

            if (loggedInUsername != null) {
                loadUserProfile();
                buildEventList();
            }
        });
    }

    // ---------------- SET LOGGED IN USER ----------------
    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
        Platform.runLater(() -> {
            loadUserProfile();
            buildEventList();
        });
    }

    // ---------------- BUILD MENU BUTTONS ----------------
    private void buildMenuButtons() {

        if (slideMenuBox == null) return;

        // remove old buttons but keep static top sections (first children until Region or end)
        // simpler: remove any Buttons previously added
        slideMenuBox.getChildren().removeIf(node -> node instanceof Button);

        String[] options = {
                "Dashboard", "Edit Profile", "Event Portal",
                "Change Password", "Sign Out", "Help & Support"
        };

        for (String opt : options) {
            Button btn = new Button(opt);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 10; -fx-alignment: CENTER_LEFT;");
            btn.setOnMouseEntered(ev -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 10; -fx-alignment: CENTER_LEFT;"));
            btn.setOnMouseExited(ev -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 10; -fx-alignment: CENTER_LEFT;"));

            btn.setOnAction(e -> {
                handleMenuAction(opt);
                // DO NOT toggle here if you prefer menu to remain on some actions. We close for now:
                toggleMenu();
            });

            // insert before the spacer region if exists
            int insertIdx = slideMenuBox.getChildren().size();
            // try to find the last Region with VBox.vgrow=ALWAYS (the spacer)
            for (int i = 0; i < slideMenuBox.getChildren().size(); i++) {
                if (slideMenuBox.getChildren().get(i) instanceof Region) {
                    // keep buttons above spacer; so insert before that region
                    insertIdx = i;
                    break;
                }
            }
            slideMenuBox.getChildren().add(insertIdx, btn);
        }
    }

    private void handleMenuAction(String opt) {
        Stage stage = (Stage) rootPane.getScene().getWindow();

        switch (opt) {
            case "Dashboard" -> buildEventList();
            case "Edit Profile" -> { if (!editingProfile) enableProfileEditing(); }
            case "Event Portal" -> {
                try { new after_login().openEventPortal(stage, stage.getScene()); }
                catch (Exception e) { e.printStackTrace(); }
            }
            case "Change Password" -> showPasswordChangeDialog(stage);
            case "Sign Out" -> DashboardController.loadLoginPage(stage);
            default -> showAlert(opt + " clicked");
        }
    }

    // ---------------- TOGGLE MENU ----------------
    @FXML private void toggleMenu() {

        if (slideMenuBox == null || slideMenuPane == null) return;

        TranslateTransition tt = new TranslateTransition(Duration.millis(240), slideMenuBox);

        if (!menuVisible) {
            // show overlay and slide in
            slideMenuPane.setVisible(true);
            slideMenuPane.setMouseTransparent(false);
            menuOverlay.setVisible(true);

            // ensure starting off-screen
            slideMenuBox.setTranslateX(-MENU_WIDTH);
            tt.setToX(0);
            menuVisible = true;
        } else {
            // slide out to left and hide overlay when finished
            tt.setToX(-MENU_WIDTH);
            tt.setOnFinished(e -> {
                slideMenuPane.setVisible(false);
                slideMenuPane.setMouseTransparent(true);
                menuOverlay.setVisible(false);
            });
            menuVisible = false;
        }

        tt.play();
    }

    // ---------------- PROFILE LOADING ----------------
    private void loadUserProfile() {
        if (loggedInUsername == null) return;
        String[] d = DatabaseUtility.getUserDetails(loggedInUsername);
        if (d != null) setProfile(d[0], d[1], d[2], d[3]);
    }

    public void setProfile(String name, String email, String dept, String session) {

        if (nameLabel != null) nameLabel.setText(name);
        if (emailLabel != null) emailLabel.setText(email);
        if (deptLabel != null) deptLabel.setText("🏫 " + dept);
        if (sessionLabel != null) sessionLabel.setText("📚 " + session);

        if (profileInitialLabel == null && profilePicStack != null) {
            profileInitialLabel = new Label();
            profileInitialLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: white;");
            profilePicStack.getChildren().clear();
            profilePicStack.getChildren().add(profileInitialLabel);
        }
        if (profileInitialLabel != null && name != null && !name.isEmpty())
            profileInitialLabel.setText(name.substring(0, 1).toUpperCase());
    }

    // ---------------- PROFILE EDITING ----------------
    private void enableProfileEditing() {

        editingProfile = true;

        nameField = new TextField(nameLabel.getText());
        emailField = new TextField(emailLabel.getText());
        deptField = new TextField(deptLabel.getText().replace("🏫 ", ""));
        sessionField = new TextField(sessionLabel.getText().replace("📚 ", ""));

        replace(nameLabel, nameField);
        replace(emailLabel, emailField);
        replace(deptLabel, deptField);
        replace(sessionLabel, sessionField);

        saveBtn = new Button("💾 Save");
        saveBtn.setStyle("-fx-padding: 6 12; -fx-font-size: 13px;");
        saveBtn.setOnAction(e -> saveProfile());

        // add save button to rightPane bottom
        if (rightPane != null) rightPane.getChildren().add(saveBtn);
    }

    private void saveProfile() {

        boolean ok = DatabaseUtility.updateUserProfile(
                loggedInUsername,
                nameField.getText(),
                emailField.getText(),
                deptField.getText(),
                sessionField.getText()
        );

        if (ok) {
            loggedInUsername = nameField.getText();
            setProfile(nameField.getText(), emailField.getText(), deptField.getText(), sessionField.getText());
            showAlert("Profile updated!");
        } else {
            showAlert("Update failed!");
        }

        restore(nameLabel, nameField);
        restore(emailLabel, emailField);
        restore(deptLabel, deptField);
        restore(sessionLabel, sessionField);

        if (rightPane != null) rightPane.getChildren().remove(saveBtn);
        editingProfile = false;
    }

    private void replace(Label lbl, TextField tf) {
        if (lbl == null || tf == null) return;
        Pane parent = (Pane) lbl.getParent();
        if (parent == null) return;
        int index = parent.getChildren().indexOf(lbl);
        if (index >= 0) parent.getChildren().set(index, tf);
    }

    private void restore(Label lbl, TextField tf) {
        if (lbl == null || tf == null) return;
        lbl.setText(tf.getText());
        Pane parent = (Pane) tf.getParent();
        if (parent == null) return;
        int index = parent.getChildren().indexOf(tf);
        if (index >= 0) parent.getChildren().set(index, lbl);
    }

    // ---------------- PASSWORD CHANGE ----------------
    private void showPasswordChangeDialog(Stage stage) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.initOwner(stage);
        dlg.setTitle("Change Password");

        GridPane gp = new GridPane();
        gp.setHgap(8);
        gp.setVgap(8);

        PasswordField oldPass = new PasswordField();
        PasswordField newPass = new PasswordField();

        gp.addRow(0, new Label("Old Password:"), oldPass);
        gp.addRow(1, new Label("New Password:"), newPass);

        dlg.getDialogPane().setContent(gp);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                if (DatabaseUtility.checkUserExists(loggedInUsername, oldPass.getText())) {
                    if (DatabaseUtility.updatePassword(loggedInUsername, newPass.getText())) showAlert("Password updated!");
                    else showAlert("Update failed!");
                } else showAlert("Incorrect old password!");
            }
        });
    }

    // ---------------- PROFILE PICTURE ----------------
    private void onProfilePicClicked(MouseEvent e) {
        if (rootPane == null) return;
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose Profile Picture");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        File f = fc.showOpenDialog(rootPane.getScene().getWindow());
        if (f == null) return;

        Image img = new Image(f.toURI().toString(), 120, 120, true, true);
        ImageView iv = new ImageView(img);
        iv.setFitWidth(120);
        iv.setFitHeight(120);

        Rectangle clip = new Rectangle(120, 120);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        iv.setClip(clip);

        profilePicStack.getChildren().setAll(iv);
    }

    // ---------------- EVENTS ----------------
    private void buildEventList() {

        if (eventsListBox == null) return;

        eventsListBox.getChildren().clear();
        List<EventController.EventData> list = EventController.loadUserEvents(loggedInUsername);

        if (list == null || list.isEmpty()) {
            Label empty = new Label("No events found");
            empty.setStyle("-fx-text-fill: #ccc; -fx-font-size: 14px;");
            eventsListBox.getChildren().add(empty);
            return;
        }

        for (EventController.EventData ev : list) eventsListBox.getChildren().add(createEventCard(ev));
    }

    private VBox createEventCard(EventController.EventData ev) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(15));

        String bg = ev.color != null ? ev.color : "#333";

        card.setStyle(String.format("-fx-background-color: %sAA; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 12, 0, 0, 6);", bg));

        if (ev.eventImagePath != null && ev.eventImagePath.exists()) {
            Image img = new Image(ev.eventImagePath.toURI().toString(), 360, 160, true, true);
            ImageView iv = new ImageView(img);
            iv.setFitWidth(330);
            iv.setFitHeight(130);

            Rectangle clip = new Rectangle(330, 130);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            iv.setClip(clip);

            card.getChildren().add(iv);
        }

        Label title = new Label("📌 " + ev.name);
        title.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
        card.getChildren().add(title);

        card.setOnMouseClicked(e -> System.out.println("Clicked: " + ev.name));
        return card;
    }

    // ---------------- UTIL ----------------
    private void showAlert(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).show(); }

    public static void loadLoginPage(Stage stage) {
        // implement as needed by your app
    }
}
