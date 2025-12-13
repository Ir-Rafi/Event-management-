import javafx.application.Platform;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private FileWriter fileWriter;

    private String serverName;
    private String clientName;
    private String ChatLogFile;
    private VBox chatVBox;

    public Server(ServerSocket serverSocket, VBox vbox) {
        this.chatVBox = vbox;
        this.serverSocket = serverSocket;
        
        try {
            // ✅ FIRST: Initialize file paths and load previous messages BEFORE accepting client
            initializeFileSystem();
            loadPreviousMessagesToUI();
            
            System.out.println("🔄 Server waiting for client...");
            
            // NOW wait for client connection
            this.socket = serverSocket.accept();
            System.out.println("✅ Client connected!");

            // Initialize IO streams
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Read username sent from client
            String clientUsername = bufferedReader.readLine();
            System.out.println("Client username: " + clientUsername);

            // Update names based on client
            String[] names = DatabaseUtility.getServerAndClientNames(clientUsername);
            this.serverName = names[0];
            this.clientName = names[1];

            System.out.println("Updated -> ServerName: " + serverName + " | ClientName: " + clientName);

            // Initialize file writer for new messages
            this.fileWriter = new FileWriter(ChatLogFile, true);

            // Send previous messages to client
            sendPreviousMessagesToClient();

            // Send welcome message to client
            bufferedWriter.write("Server logged in as: " + serverName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            System.out.println("Error in server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeFileSystem() {
        // Create chat directory if it doesn't exist
        String chatDir = "Chat";
        File dir = new File(chatDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // For now, use a default file name until client connects
        // This will be updated once we know the client name
        this.ChatLogFile = "Chat/chat_log.dat";
        
        // Check if there are any existing chat files to load
        File[] chatFiles = dir.listFiles((d, name) -> name.endsWith("_chat_log.dat"));
        if (chatFiles != null && chatFiles.length > 0) {
            // Use the most recent chat file
            this.ChatLogFile = chatFiles[0].getAbsolutePath();
            System.out.println("📂 Found existing chat log: " + ChatLogFile);
        }
    }

    private void loadPreviousMessagesToUI() {
    try {
        File chatFile = new File(ChatLogFile);
        if (chatFile.exists()) {
            System.out.println("📂 Loading previous messages to server UI...");
            
            BufferedReader fileReader = new BufferedReader(new FileReader(chatFile));
            String line;
            int messageCount = 0;
            
            while ((line = fileReader.readLine()) != null) {
                final String msg = line;
                Platform.runLater(() -> {
                    ChatController.addLabel(msg, chatVBox);
                });
                messageCount++;
            }
            fileReader.close();
            
            System.out.println("✅ Loaded " + messageCount + " previous messages to server UI");
            
            // ✅ Make messageCount final for lambda
            final int finalMessageCount = messageCount;
            Platform.runLater(() -> {
                ChatController.addLabel("📜 Loaded " + finalMessageCount + " previous messages", chatVBox);
            });
        } else {
            System.out.println("📝 No previous chat log found. Starting fresh.");
        }
    } catch (IOException e) {
        System.out.println("❌ Error loading previous messages to UI");
        e.printStackTrace();
    }
}

    private void sendPreviousMessagesToClient() {
        try {
            File chatFile = new File(ChatLogFile);
            if (chatFile.exists()) {
                System.out.println("📤 Sending previous messages to client...");

                BufferedReader fileReader = new BufferedReader(new FileReader(chatFile));
                String line;
                int messageCount = 0;
                
                while ((line = fileReader.readLine()) != null) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    messageCount++;
                }
                fileReader.close();

                System.out.println("✅ Sent " + messageCount + " previous messages to client");
            }
        } catch (IOException e) {
            System.out.println("❌ Error sending previous messages to client");
            e.printStackTrace();
        }
    }

    public void sendMessageToClient(String messageToClient) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String formattedMessage = "Server [" + timestamp + "]: " + messageToClient;
            
            bufferedWriter.write(formattedMessage);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            fileWriter.write(formattedMessage + "\n");
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error sending message to client");
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void receiveMessageFromClient(VBox vbox) {
        Thread receiverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket != null && socket.isConnected()) {
                    try {
                        String messageFromClient = bufferedReader.readLine();
                        if (messageFromClient == null) break;
                        
                        ChatController.addLabel(messageFromClient, vbox);

                        // Save with timestamp if not already formatted
                        if (!messageFromClient.contains("[")) {
                            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                            messageFromClient = "Client [" + timestamp + "]: " + messageFromClient;
                        }
                        
                        fileWriter.write(messageFromClient + "\n");
                        fileWriter.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("❌ Error receiving message from the client");
                        closeEverything(socket, bufferedReader, bufferedWriter);
                        break;
                    }
                }
            }
        });
        receiverThread.setDaemon(true);
        receiverThread.setName("ServerReceiverThread");
        receiverThread.start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
            if (fileWriter != null) fileWriter.close();
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}