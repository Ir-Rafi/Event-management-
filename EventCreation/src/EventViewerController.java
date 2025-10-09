import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.List;

public class EventViewerController {

    @FXML private TextArea eventsTextArea;
    @FXML private Button closeButton;

    @FXML
    private void initialize() {
        loadEvents();
    }

    private void loadEvents() {
        List<Event> events = EventStore.getSubmittedEvents();

        if(events.isEmpty()) {
            eventsTextArea.setText("No events have been created yet!");
            return;
        }

        StringBuilder sb = new StringBuilder("📋 All Created Events:\n\n");
        for(Event e : events) {
            sb.append("📌 Event Name: ").append(e.getName()).append("\n")
              .append("📅 Date: ").append(e.getDate()).append("\n")
              .append("👤 Main Organizer: ").append(e.getMainOrganizerName())
              .append(" (").append(e.getMainOrganizerPhone()).append(")\n");
            if(!e.getOrganizers().isEmpty())
                sb.append("👥 Additional Organizers: ").append(e.getOrganizers()).append("\n");
            sb.append("\n--------------------\n\n");
        }
        eventsTextArea.setText(sb.toString());
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
