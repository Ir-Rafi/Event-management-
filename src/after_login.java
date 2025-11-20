// Updated after_login.java with back button at top-left and header with glow

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class after_login {

    private Stage stage;
    private Scene dashboardScene;

    private static final String DARK_BG = "linear-gradient(to bottom, #0a0c19 0%, #1e213d 50%, #0a0c19 100%)";
    private static final String CARD_BG = "rgba(20, 25, 45, 0.7)";
    private static final String BUTTON_GRADIENT = "linear-gradient(to bottom, rgba(90,110,255,0.9), rgba(60,80,220,0.9))";
    private static final String BUTTON_HOVER = "linear-gradient(to bottom, rgba(110,130,255,1), rgba(80,100,240,1))";

    public void applyHoverEffect(Button btn, String normalColor, String hoverColor) {
        String baseStyle = "-fx-background-color: " + normalColor + "; " +
                "-fx-text-fill: white; -fx-background-radius: 10; -fx-font-family: 'Segoe UI', Arial;" +
                "-fx-effect: dropshadow(gaussian, rgba(90,120,255,0.6), 25, 0.6, 0, 4);";

        String hoverStyle = "-fx-background-color: " + hoverColor + "; " +
                "-fx-text-fill: white; -fx-background-radius: 10; -fx-font-family: 'Segoe UI', Arial;" +
                "-fx-effect: dropshadow(gaussian, rgba(110,140,255,0.8), 35, 0.7, 0, 6);";

        btn.setStyle(baseStyle);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            btn.setCursor(javafx.scene.Cursor.HAND);
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
    }

    public void openEventPortal(Stage stage, Scene dashboardScene) {
        this.stage = stage;
        this.dashboardScene = dashboardScene;
        showHomeScreen();
    }

    private void showHomeScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + DARK_BG + ";");
        root.setPadding(new Insets(30));

        // ---------- TOP BAR WITH BACK BUTTON + HEADER ----------
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button dashboardBack = new Button("← Back to Dashboard");
        dashboardBack.setFont(Font.font("Segoe UI", 16));
        dashboardBack.setStyle(
                "-fx-background-color: rgba(40,45,70,0.8); -fx-text-fill: rgba(220,230,255,0.9);" +
                        "-fx-background-radius: 10; -fx-padding: 10 20;" +
                        "-fx-border-color: rgba(120,150,255,0.3); -fx-border-width: 1.5;"
        );
        applyHoverEffect(dashboardBack, "rgba(40,45,70,0.8)", "rgba(60,80,180,0.6)");

        dashboardBack.setOnAction(e -> {
            stage.setScene(dashboardScene);
            stage.setMaximized(true);
        });

        // ---- NEW HEADER TEXT ----
        Text header = new Text("Work with User Friendly Interface");
        header.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 28));
        header.setFill(Color.rgb(220, 230, 255));

        DropShadow glow = new DropShadow();
        glow.setColor(Color.rgb(120, 150, 255, 0.6));
        glow.setRadius(35);
        glow.setSpread(0.6);
        header.setEffect(glow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(dashboardBack, spacer, header);
        root.setTop(topBar);

        // ------------ CENTER CONTENT (TITLE, SUBTITLE, BUTTONS) -----------
        VBox centerBox = new VBox(35);
        centerBox.setAlignment(Pos.CENTER);

        Text title = new Text("Welcome to Event Portal");
        title.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 48));
        title.setFill(Color.WHITE);

        DropShadow titleGlow = new DropShadow();
        titleGlow.setColor(Color.rgb(100, 140, 255, 0.5));
        titleGlow.setRadius(30);
        titleGlow.setSpread(0.5);
        title.setEffect(titleGlow);

        Text subtitle = new Text("Manage your events with ease");
        subtitle.setFont(Font.font("Segoe UI Light", 18));
        subtitle.setFill(Color.rgb(180, 190, 220, 0.75));

        Button eventsButton = new Button("Your Events →");
        eventsButton.setFont(Font.font("Segoe UI Semibold", 18));
        eventsButton.setPrefSize(250, 50);
        eventsButton.setStyle(
                "-fx-background-color: " + BUTTON_GRADIENT + "; -fx-text-fill: white;" +
                        "-fx-background-radius: 12; -fx-padding: 14 35; -fx-font-weight: 600;"
        );
        applyHoverEffect(eventsButton, BUTTON_GRADIENT, BUTTON_HOVER);

        Text instructionTitle = new Text("Instruction Set");
        instructionTitle.setFont(Font.font("Segoe UI Semibold", 26));
        instructionTitle.setFill(Color.rgb(210, 220, 255));
        instructionTitle.setEffect(glow);

        Text instructionBody = new Text(
                "You can create events using the Event Creation Dashboard.\n" +
                "You can check your assigned tasks in Sub Organizer's Dashboard.\n" +
                "You can view all events via the Viewer's Dashboard."
        );
        instructionBody.setFont(Font.font("Segoe UI", 16));
        instructionBody.setFill(Color.rgb(180, 190, 220, 0.8));
        instructionBody.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox instructionBox = new VBox(10, instructionTitle, instructionBody);
        instructionBox.setAlignment(Pos.CENTER);

        centerBox.getChildren().addAll(title, subtitle, eventsButton, instructionBox);
        root.setCenter(centerBox);

        Scene homeScene = new Scene(root, 1000, 700);
        Scene eventsScene = createEventsPage(homeScene);

        eventsButton.setOnAction(e -> {
            FadeTransition fade = new FadeTransition(Duration.seconds(0.5), eventsScene.getRoot());
            fade.setFromValue(0);
            fade.setToValue(1);
            stage.setScene(eventsScene);
            stage.setMaximized(true);
            fade.play();
        });

        stage.setScene(homeScene);
        stage.setMaximized(true);
    }

    // -------- OTHER METHODS (createEventsPage, createEventCard) REMAIN SAME --------

}
