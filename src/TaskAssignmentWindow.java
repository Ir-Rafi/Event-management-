import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskAssignmentWindow {

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://ununqd8usvy0wouy:GmDEehgTBjzyuPRuA8i8@b1gtvncwynmgz6qozokc-mysql.services.clever-cloud.com:3306/b1gtvncwynmgz6qozokc";
        String user = "ununqd8usvy0wouy";
        String password = "GmDEehgTBjzyuPRuA8i8";
        return DriverManager.getConnection(url, user, password);
    }

    public static class Task implements Serializable {
        String description;
        int subOrganizerId;
        boolean completed;

        public Task(String description, int subOrganizerId) {
            this.description = description;
            this.subOrganizerId = subOrganizerId;
            this.completed = false;
        }
    }

    private static final String TASK_FILE = "tasks.dat";
    private List<Task> tasks;

    public TaskAssignmentWindow(Stage stage) {
        tasks = loadTasksFromFile();

        VBox layout = new VBox(25);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #1f1c2c, #928dab);");

        Label title = new Label("Assign Tasks to Sub-Organizers");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        ComboBox<String> subOrganizerCombo = new ComboBox<>();
        ObservableList<String> subOrganizerList = FXCollections.observableArrayList();

        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT id, username FROM sub_organizers WHERE organizer_id = ?");
            ps.setInt(1, Session.getOrganizerId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("username");
                subOrganizerList.add(id + ": " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(subOrganizerList.isEmpty()) {
            subOrganizerList.add("No sub-organizers found");
        }

        subOrganizerCombo.setItems(subOrganizerList);
        subOrganizerCombo.setPrefWidth(300);

        TextArea taskArea = new TextArea();
        taskArea.setPromptText("Enter task description");
        taskArea.setPrefRowCount(3);
        taskArea.setWrapText(true);
        taskArea.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-font-size: 14px; -fx-padding: 10;");

        Button assignBtn = new Button("Assign Task");
        assignBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 12; -fx-padding: 10 20;");

        VBox tasksDisplay = new VBox(10);
        tasksDisplay.setAlignment(Pos.TOP_LEFT);
        tasksDisplay.setPadding(new Insets(10));
        tasksDisplay.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 10;");

        refreshTasksDisplay(tasksDisplay);

        assignBtn.setOnAction(e -> {
            String selected = subOrganizerCombo.getValue();
            if (selected == null || selected.equals("No sub-organizers found") || taskArea.getText().trim().isEmpty()) {
                showAlert("Error", "Select a sub-organizer and enter a task description.");
                return;
            }
            int selectedId = Integer.parseInt(selected.split(":")[0]);
            Task task = new Task(taskArea.getText().trim(), selectedId);
            tasks.add(task);
            saveTasksToFile();
            showAlert("Success", "Task assigned to " + selected.split(":")[1]);
            taskArea.clear();
            refreshTasksDisplay(tasksDisplay);
        });

        HBox inputContainer = new HBox(20, subOrganizerCombo, taskArea, assignBtn);
        inputContainer.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(tasksDisplay);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(250);

        layout.getChildren().addAll(title, inputContainer, scrollPane);

        Scene scene = new Scene(layout, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void refreshTasksDisplay(VBox tasksDisplay) {
        tasksDisplay.getChildren().clear();
        for (Task t : tasks) {
            Label lbl = new Label("Sub-Organizer ID: " + t.subOrganizerId + " | Task: " + t.description + " | Completed: " + t.completed);
            lbl.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px;");
            tasksDisplay.getChildren().add(lbl);
        }
    }

    private void saveTasksToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TASK_FILE))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Task> loadTasksFromFile() {
        File file = new File(TASK_FILE);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Task>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void showAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Task Assignment");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}