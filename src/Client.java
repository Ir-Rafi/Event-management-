import javafx.scene.layout.VBox;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private String chatLogFile;
    private boolean isConnected = false;

    public Client(String host, int port, String clientUsername) {
        this.clientUsername = clientUsername;
        
        // Initialize chat log file path
        String[] names = DatabaseUtility.getServerAndClientNames(clientUsername);
        String serverName = names[0];
        String clientName = names[1];
        
        String chatDir = "Chat";
        File dir = new File(chatDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.chatLogFile = "Chat/" + serverName + "_" + clientName + "_chat_log.dat";
        
        try {
            this.socket = new Socket(host, port);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bufferedWriter.write(clientUsername);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            isConnected = true;

            // Receive the server's logged-in message
            String serverMessage = bufferedReader.readLine();
            System.out.println(serverMessage);

        } catch (IOException e) {
            System.out.println("Server is offline. Messages will be saved locally.");
            isConnected = false;
        }
    }

    public void sendMessageToServer(String message) {
        // Always save to file first (this ensures persistence)
        saveMessageToFile(message, "Client");
        
        // Try to send to server if connected
        if (isConnected && socket != null && socket.isConnected()) {
            try {
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                System.out.println("✅ Message sent to server and saved");
            } catch (IOException e) {
                System.out.println("⚠️ Failed to send to server. Message saved locally.");
                isConnected = false;
            }
        } else {
            System.out.println("📝 Server offline. Message saved to file.");
        }
    }

    private void saveMessageToFile(String message, String sender) {
        try (FileWriter fw = new FileWriter(chatLogFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            bw.write(sender + " [" + timestamp + "]: " + message);
            bw.newLine();
            bw.flush();
            
        } catch (IOException e) {
            System.out.println("❌ Error saving message to file");
            e.printStackTrace();
        }
    }

    public void receiveMessages(VBox vbox) {
        if (!isConnected) {
            System.out.println("Not connected to server. Cannot receive live messages.");
            return;
        }
        
        Thread receiverThread = new Thread(() -> {
            while (socket != null && socket.isConnected()) {
                try {
                    String msg = bufferedReader.readLine();
                    if (msg == null) break;
                    ClientController.addLabel(msg, vbox);
                } catch (IOException e) {
                    System.out.println("Connection lost");
                    isConnected = false;
                    break;
                }
            }
        });
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    public void closeEverything() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isConnected() {
        return isConnected && socket != null && socket.isConnected();
    }
}