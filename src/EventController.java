import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EventController implements Initializable {

    @FXML private TextField eventNameField;
    @FXML private DatePicker eventDatePicker;
    @FXML private ComboBox<String> endsAfterCombo;
    @FXML private ComboBox<String> startTimeCombo;
    @FXML private ComboBox<String> endTimeCombo;
    @FXML private ComboBox<String> locationCombo;
    @FXML private TextArea eventDescriptionArea;
    @FXML private VBox organizersContainer;
    @FXML private HBox colorContainer;
    @FXML private Button closeButton;
    @FXML private Button attachFileButton;
    @FXML private Button addImageButton;
    @FXML private Button addOrganizerButton;
    @FXML private Button cancelButton;
    @FXML private Button createButton;
    @FXML private ToggleGroup showMeGroup;
    @FXML private ToggleGroup visibilityGroup;

    private String selectedColor = "#6366F1";
    private List<OrganizerData> organizersList = new ArrayList<>();
    private File attachedFile = null;
    private File eventImage = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeLocations();
        initializeTimes();
        initializeEndsAfter();
        initializeColorPicker();
    }

    private void initializeLocations() {
        locationCombo.getItems().addAll("TSC", "CURZON", "SENATE");
    }

    private void initializeTimes() {
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String time = String.format("%02d:%02d", hour, minute);
                startTimeCombo.getItems().add(time);
                endTimeCombo.getItems().add(time);
            }
        }
    }

    private void initializeEndsAfter() {
        endsAfterCombo.getItems().addAll(
            "Same Day", "1 Day", "2 Days", "3 Days", "1 Week", "2 Weeks", "Custom"
        );
        endsAfterCombo.setValue("Same Day");
    }

    private void initializeColorPicker() {
        String[] colors = {
            "#6366F1", "#06B6D4", "#10B981", "#F59E0B", 
            "#F97316", "#EF4444", "#EC4899", "#8B5CF6"
        };

        for (String color : colors) {
            Circle colorCircle = new Circle(15);
            colorCircle.setFill(Color.web(color));
            colorCircle.setStroke(Color.web("#E5E7EB"));
            colorCircle.setStrokeWidth(2);
            colorCircle.getStyleClass().add("color-circle");
            
            colorCircle.setOnMouseClicked(e -> {
                selectedColor = color;
                updateColorSelection(colorCircle);
            });
            
            colorContainer.getChildren().add(colorCircle);
            
            if (color.equals("#6366F1")) {
                colorCircle.setStrokeWidth(3);
                colorCircle.setStroke(Color.web("#4F46E5"));
            }
        }
    }

    private void updateColorSelection(Circle selectedCircle) {
        colorContainer.getChildren().forEach(node -> {
            if (node instanceof Circle) {
                Circle circle = (Circle) node;
                circle.setStrokeWidth(2);
                circle.setStroke(Color.web("#E5E7EB"));
            }
        });
        
        selectedCircle.setStrokeWidth(3);
        selectedCircle.setStroke(Color.web("#4F46E5"));
    }

    @FXML
    private void handleAddOrganizer() {
        VBox organizerCard = new VBox(10);
        organizerCard.getStyleClass().add("organizer-card");
        organizerCard.setPadding(new Insets(15));
        organizerCard.setSpacing(10);
        
        // Organizer Name
        VBox nameBox = new VBox(5);
        Label nameLabel = new Label("Organizer Name");
        nameLabel.getStyleClass().add("organizer-field-label");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter organizer name");
        nameField.getStyleClass().add("organizer-field");
        nameBox.getChildren().addAll(nameLabel, nameField);
        
        // Registration Code
        VBox codeBox = new VBox(5);
        Label codeLabel = new Label("Registration Code");
        codeLabel.getStyleClass().add("organizer-field-label");
        TextField codeField = new TextField();
        codeField.setPromptText("Enter registration code");
        codeField.getStyleClass().add("organizer-field");
        codeBox.getChildren().addAll(codeLabel, codeField);
        
        // Remove Button
        Button removeBtn = new Button("✕ Remove");
        removeBtn.getStyleClass().add("remove-organizer-btn");
        removeBtn.setOnAction(e -> {
            organizersContainer.getChildren().remove(organizerCard);
            organizersList.removeIf(org -> org.nameField == nameField);
        });
        
        organizerCard.getChildren().addAll(nameBox, codeBox, removeBtn);
        organizersContainer.getChildren().add(organizerCard);
        
        organizersList.add(new OrganizerData(nameField, codeField));
    }

    @FXML
    private void handleAttachFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Attach File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"),
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("Documents", "*.doc", "*.docx")
        );
        
        attachedFile = fileChooser.showOpenDialog(attachFileButton.getScene().getWindow());
        
        if (attachedFile != null) {
            showAlert("File Attached", "File: " + attachedFile.getName(), Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleAddImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Event Image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        eventImage = fileChooser.showOpenDialog(addImageButton.getScene().getWindow());
        
        if (eventImage != null) {
            showAlert("Image Added", "Image: " + eventImage.getName(), Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleCreate() {
        // Validate basic fields
        if (eventNameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter an event name", Alert.AlertType.ERROR);
            return;
        }
        
        if (eventDatePicker.getValue() == null) {
            showAlert("Validation Error", "Please select an event date", Alert.AlertType.ERROR);
            return;
        }
        
        if (locationCombo.getValue() == null) {
            showAlert("Validation Error", "Please select a location", Alert.AlertType.ERROR);
            return;
        }
        
        // Collect and validate organizers
        List<Organizer> organizers = new ArrayList<>();
        for (OrganizerData orgData : organizersList) {
            String name = orgData.nameField.getText().trim();
            String code = orgData.codeField.getText().trim();
            
            if (name.isEmpty() || code.isEmpty()) {
                showAlert("Validation Error", "Please fill in all organizer fields or remove empty organizers", Alert.AlertType.ERROR);
                return;
            }
            
            organizers.add(new Organizer(name, code));
        }
        
        // Create event
        EventData event = new EventData(
            eventNameField.getText().trim(),
            eventDatePicker.getValue(),
            startTimeCombo.getValue(),
            endTimeCombo.getValue(),
            locationCombo.getValue(),
            eventDescriptionArea.getText().trim(),
            organizers,
            selectedColor,
            ((RadioButton) showMeGroup.getSelectedToggle()).getText(),
            ((RadioButton) visibilityGroup.getSelectedToggle()).getText(),
            attachedFile,
            eventImage
        );
        
        // Print event details
        System.out.println("\n========== EVENT CREATED ==========");
        System.out.println("Event Name: " + event.name);
        System.out.println("Date: " + event.date);
        System.out.println("Time: " + event.startTime + " - " + event.endTime);
        System.out.println("Location: " + event.location);
        System.out.println("Description: " + event.description);
        System.out.println("Color: " + event.color);
        System.out.println("Total Organizers: " + organizers.size());
        System.out.println("\n--- Organizers List ---");
        for (int i = 0; i < organizers.size(); i++) {
            Organizer org = organizers.get(i);
            System.out.println((i + 1) + ". Name: " + org.name + " | Code: " + org.registrationCode);
        }
        System.out.println("===================================\n");
        
        String message = organizers.size() > 0 
            ? "Event created successfully with " + organizers.size() + " organizer(s)!"
            : "Event created successfully!";
            
        showAlert("Success", message, Alert.AlertType.INFORMATION);
        handleClose();
    }

    @FXML
    private void handleCancel() {
        handleClose();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    public static void openEventForm(Stage parentStage) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                EventController.class.getResource("CreateEventForm.fxml")
            );
            VBox root = loader.load();
            
            Stage formStage = new Stage();
            formStage.setTitle("Create New Event");
            formStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            formStage.initOwner(parentStage);
            formStage.setScene(new javafx.scene.Scene(root));
            formStage.setResizable(false);
            formStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showStaticAlert("Error", "Could not load event form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private static void showStaticAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Organizer field reference holder
    private static class OrganizerData {
        TextField nameField;
        TextField codeField;

        public OrganizerData(TextField nameField, TextField codeField) {
            this.nameField = nameField;
            this.codeField = codeField;
        }
    }

    // Organizer info holder
    private static class Organizer {
        String name;
        String registrationCode;

        public Organizer(String name, String registrationCode) {
            this.name = name;
            this.registrationCode = registrationCode;
        }
    }

    // Event data holder
    private static class EventData {
        String name, startTime, endTime, location, description, color, showMe, visibility;
        LocalDate date;
        List<Organizer> organizers;
        File attachedFile, eventImage;

        public EventData(String name, LocalDate date, String startTime, String endTime, 
                        String location, String description, List<Organizer> organizers,
                        String color, String showMe, String visibility, File attachedFile, File eventImage) {
            this.name = name;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.location = location;
            this.description = description;
            this.organizers = new ArrayList<>(organizers);
            this.color = color;
            this.showMe = showMe;
            this.visibility = visibility;
            this.attachedFile = attachedFile;
            this.eventImage = eventImage;
        }
    }
}
