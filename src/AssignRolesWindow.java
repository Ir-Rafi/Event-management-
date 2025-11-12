import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AssignRolesWindow {

    private final String[] members = {"Maheru", "Irfan", "Sajid", "Srishti", "Tuhin"};
    private final boolean[] taskAssigned = new boolean[members.length];
    private final Label[] statusLabels = new Label[members.length]; // Added to track each member’s status

    public void show() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Assign Roles to Members");

        VBox layout = new VBox(30);
        layout.setPadding(new Insets(50, 100, 50, 100));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Assign Tasks to Members");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #0D47A1;");

        VBox membersList = new VBox(20);
        membersList.setAlignment(Pos.CENTER);
        membersList.setPadding(new Insets(30));

        for (int i = 0; i < members.length; i++) {
            HBox memberCard = new HBox(20);
            memberCard.setAlignment(Pos.CENTER_LEFT);
            memberCard.getStyleClass().add("member-card");

            Label nameLabel = new Label(members[i]);
            nameLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #212121; -fx-font-weight: bold;");

            Label statusLabel = new Label("Pending");
            statusLabel.getStyleClass().add("status-label");
            statusLabels[i] = statusLabel;

            Button assignBtn = new Button("Assign Task");
            assignBtn.getStyleClass().add("assign-btn");

            int index = i;
            assignBtn.setOnAction(e -> openCustomTaskDialog(nameLabel, assignBtn, statusLabel, index));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            memberCard.getChildren().addAll(nameLabel, spacer, assignBtn, statusLabel);
            membersList.getChildren().add(memberCard);
        }

        ScrollPane scrollPane = new ScrollPane(membersList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("close-btn");
        closeBtn.setOnAction(e -> window.close());

        layout.getChildren().addAll(title, scrollPane, closeBtn);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("assignroles.css").toExternalForm());
        window.setScene(scene);
        window.setMaximized(true);
        window.show();
    }

    // Custom task dialog
    private void openCustomTaskDialog(Label nameLabel, Button assignBtn, Label statusLabel, int index) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Assign Task");

        VBox dialogLayout = new VBox(20);
        dialogLayout.setPadding(new Insets(25));
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        dialogLayout.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.25)));

        Label header = new Label("Assign a task to " + nameLabel.getText());
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1565C0;");

        TextArea taskField = new TextArea();
        taskField.setPromptText("Type the task description here...");
        taskField.setWrapText(true);
        taskField.setPrefRowCount(4);
        taskField.setStyle("""
                -fx-background-color: #F7F9FB;
                -fx-border-color: #BBDEFB;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                -fx-padding: 10;
                -fx-font-size: 14px;
                """);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button assignButton = new Button("Assign");
        assignButton.setStyle("""
                -fx-background-color: #27AE60;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-padding: 8 20;
                """);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("""
                -fx-background-color: #E53935;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-padding: 8 20;
                """);

        assignButton.setOnAction(e -> {
            String task = taskField.getText().trim();
            if (!task.isEmpty()) {
                taskAssigned[index] = true;
                assignBtn.setText("✔ Task Assigned");
                assignBtn.setDisable(true);
                assignBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold;");
                statusLabel.setText("Completed");
                statusLabel.setStyle("-fx-text-fill: #2ECC71; -fx-font-weight: bold;");
                dialog.close();

                Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
                confirmation.setHeaderText("Task Assigned Successfully!");
                confirmation.setContentText(nameLabel.getText() + " has been assigned: " + task);
                confirmation.showAndWait();
            } else {
                Alert warn = new Alert(Alert.AlertType.WARNING);
                warn.setHeaderText("Empty Task");
                warn.setContentText("Please enter a task before assigning!");
                warn.showAndWait();
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(assignButton, cancelButton);
        dialogLayout.getChildren().addAll(header, taskField, buttonBox);

        Scene dialogScene = new Scene(dialogLayout, 400, 300);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }
}
