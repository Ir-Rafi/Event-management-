import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

<<<<<<< HEAD
public class AssignRolesWindow {

    private final String[] members = {"Maheru", "Irfan", "Sajid", "Srishti", "Tuhin"};
    private final boolean[] taskAssigned = new boolean[members.length];
    private final Label[] statusLabels = new Label[members.length]; // Added to track each member’s status
=======
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignRolesWindow {

    private final int mainOrganizerId;
    private final String mainOrganizerName;
    private final List<EventTask> tasks = new ArrayList<>();
    private VBox subOrganizerBox;

    public AssignRolesWindow(String mainOrganizerName, int mainOrganizerId) {
        this.mainOrganizerId = mainOrganizerId;
        this.mainOrganizerName = mainOrganizerName;
        loadTasks();
    }
>>>>>>> 6025839 (Initial commit)

    public void show() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
<<<<<<< HEAD
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
=======
        window.setTitle("Assign Tasks to Sub-Organizers");

        VBox layout = new VBox(25);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        // Dark gradient background
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #1E1E2F, #2C2C3E);");

        Label title = new Label("Sub-Organizers & Assigned Tasks");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #FFD700;"); // Gold header

        subOrganizerBox = new VBox(20);
        subOrganizerBox.setAlignment(Pos.TOP_CENTER);
        refreshSubOrganizerCards();

        ScrollPane scrollPane = new ScrollPane(subOrganizerBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("""
            -fx-background-color: #444C5C;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
            -fx-padding: 8 20 8 20;
        """);
>>>>>>> 6025839 (Initial commit)
        closeBtn.setOnAction(e -> window.close());

        layout.getChildren().addAll(title, scrollPane, closeBtn);

<<<<<<< HEAD
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
=======
        Scene scene = new Scene(layout, 850, 700);
        window.setScene(scene);
        window.show();
    }

    private void refreshSubOrganizerCards() {
        subOrganizerBox.getChildren().clear();

        List<EventData> events = getSubOrganizerEvents();
        if (events.isEmpty()) {
            Label noEvents = new Label("No sub-organizers found for your events.");
            noEvents.setStyle("-fx-font-size: 18px; -fx-text-fill: #CCCCCC;");
            subOrganizerBox.getChildren().add(noEvents);
            return;
        }

        for (EventData event : events) {
            VBox eventContainer = new VBox(10);
            eventContainer.setPadding(new Insets(10));
            eventContainer.setStyle("-fx-background-color: #2F2F3F; -fx-background-radius: 10;");
            eventContainer.setEffect(new DropShadow(5, Color.BLACK));

            Label eventLabel = new Label(event.eventName + " (ID: " + event.eventId + ")");
            eventLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
            eventContainer.getChildren().add(eventLabel);

            List<SubOrganizer> subOrgs = getSubOrganizers(event.eventId);
            if (subOrgs.isEmpty()) {
                Label noSub = new Label("No sub-organizers for this event.");
                noSub.setStyle("-fx-text-fill: #BBBBBB;");
                eventContainer.getChildren().add(noSub);
            } else {
                for (SubOrganizer sub : subOrgs) {
                    HBox card = new HBox(15);
                    card.setPadding(new Insets(12));
                    card.setStyle("""
                        -fx-background-color: #3A3F55;
                        -fx-background-radius: 10;
                    """);
                    card.setAlignment(Pos.TOP_LEFT);
                    card.setEffect(new DropShadow(5, Color.BLACK));

                    VBox left = new VBox(5);
                    Label nameLabel = new Label(sub.name + " (ID: " + sub.id + ")");
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #FFFFFF;");

                    Button addTaskBtn = new Button("+");
                    addTaskBtn.setStyle("""
                        -fx-background-color: #FFD700;
                        -fx-text-fill: black;
                        -fx-font-weight: bold;
                        -fx-background-radius: 5;
                        -fx-pref-width: 30;
                        -fx-pref-height: 30;
                    """);
                    addTaskBtn.setOnAction(e -> openAddTaskDialog(event.eventId, sub));

                    left.getChildren().addAll(nameLabel, addTaskBtn);

                    VBox taskList = new VBox(5);
                    for (EventTask task : tasks) {
                        if (task.eventId == event.eventId && task.subOrganizerName.equals(sub.name)) {
                            HBox taskRow = new HBox(10);
                            taskRow.setAlignment(Pos.CENTER_LEFT);

                            Label taskLabel = new Label(task.taskDescription);
                            taskLabel.setStyle(task.completed
                                    ? "-fx-text-fill: #4CAF50;"   // Green for completed
                                    : "-fx-text-fill: #FFA000;"); // Orange for pending

                            Button completeBtn = new Button("✔");
                            completeBtn.setDisable(task.completed);
                            completeBtn.setStyle("""
                                -fx-background-color: #4CAF50;
                                -fx-text-fill: white;
                                -fx-background-radius: 5;
                            """);
                            completeBtn.setOnAction(ev -> {
                                task.completed = true;
                                saveTasks();
                                refreshSubOrganizerCards();
                            });

                            taskRow.getChildren().addAll(taskLabel, completeBtn);
                            taskList.getChildren().add(taskRow);
                        }
                    }

                    card.getChildren().addAll(left, taskList);
                    eventContainer.getChildren().add(card);
                }
            }

            subOrganizerBox.getChildren().add(eventContainer);
        }
    }

    private void openAddTaskDialog(int eventId, SubOrganizer sub) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Assign Task to " + sub.name);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2C2C3E; -fx-background-radius: 10;");

        TextArea taskArea = new TextArea();
        taskArea.setPromptText("Task Description");
        taskArea.setWrapText(true);

        Button assignBtn = new Button("Assign Task");
        assignBtn.setStyle("""
            -fx-background-color: #FFD700;
            -fx-text-fill: black;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
        """);
        assignBtn.setOnAction(e -> {
            String taskDesc = taskArea.getText().trim();
            if (!taskDesc.isEmpty()) {
                EventTask newTask = new EventTask(eventId, sub.name, taskDesc, false);
                tasks.add(newTask);
                saveTasks();
                dialog.close();
                refreshSubOrganizerCards();
            } else {
                new Alert(Alert.AlertType.WARNING, "Please enter a task description!").showAndWait();
            }
        });

        Label dialogTitle = new Label("Assign Task to: " + sub.name);
        dialogTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        layout.getChildren().addAll(dialogTitle, taskArea, assignBtn);

        Scene scene = new Scene(layout, 400, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void saveTasks() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("tasks.dat"))) {
            out.writeObject(tasks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadTasks() {
        File file = new File("tasks.dat");
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                tasks.addAll((List<EventTask>) in.readObject());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<EventData> getSubOrganizerEvents() {
        List<EventData> eventList = new ArrayList<>();
        String sql = """
            SELECT DISTINCT e.event_id, e.event_name 
            FROM events e 
            JOIN organizers o ON e.event_id = o.event_id
            WHERE e.organizer_id = ? AND o.organizer_id != ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mainOrganizerId);
            ps.setInt(2, mainOrganizerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                eventList.add(new EventData(rs.getInt("event_id"), rs.getString("event_name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventList;
    }

    private List<SubOrganizer> getSubOrganizers(int eventId) {
        List<SubOrganizer> list = new ArrayList<>();
        String sql = "SELECT organizer_id, organizer_name FROM organizers WHERE event_id = ? AND organizer_id != ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, mainOrganizerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new SubOrganizer(rs.getInt("organizer_id"), rs.getString("organizer_name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private Connection getConnection() throws Exception {
        String url = "jdbc:mysql://b1gtvncwynmgz6qozokc-mysql.services.clever-cloud.com:3306/b1gtvncwynmgz6qozokc";
        String user = "ununqd8usvy0wouy";
        String password = "GmDEehgTBjzyuPRuA8i8";
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    // Helper classes
    static class EventData {
        int eventId;
        String eventName;
        EventData(int id, String name) { this.eventId = id; this.eventName = name; }
    }

    static class SubOrganizer {
        int id;
        String name;
        SubOrganizer(int id, String name) { this.id = id; this.name = name; }
    }

    public static class EventTask implements Serializable {
        public int eventId;
        public String subOrganizerName;
        public String taskDescription;
        public boolean completed;
        public EventTask(int eventId, String subOrganizerName, String taskDescription, boolean completed) {
            this.eventId = eventId;
            this.subOrganizerName = subOrganizerName;
            this.taskDescription = taskDescription;
            this.completed = completed;
        }
    }
}



>>>>>>> 6025839 (Initial commit)
