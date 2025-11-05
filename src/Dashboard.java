import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.regex.*;

// -------------------- BACKGROUND PANEL --------------------
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String cssFilePath) {
        setLayout(new BorderLayout());
        backgroundImage = loadBackgroundFromCSS(cssFilePath);
    }

    private Image loadBackgroundFromCSS(String cssFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(cssFilePath))) {
            String line;
            Pattern pattern = Pattern.compile("background-image:\\s*url\\(\"?([^\"]+)\"?\\);");
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line.trim());
                if (matcher.find()) {
                    String imagePath = matcher.group(1);
                    return new ImageIcon(imagePath).getImage();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

// -------------------- DASHBOARD --------------------
public class Dashboard extends JFrame {

    private JPanel eventsList;
    private JLabel[] eventLabels = new JLabel[5]; // store event labels
    private int nextEventIndex = 0; // index to replace next

    // Slide menu variables
    private JPanel slideMenu;
    private boolean menuVisible = false;
    private Timer slideTimer;
    private int menuWidth = 260;
    private int currentX = -menuWidth;

    // Labels for edit profile
    private JLabel nameLabel, emailLabel, deptLabel, sessionLabel;

    public Dashboard(String name, String email, String department, String session) {
        setTitle("User Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel("dashboard.css");

        // Top bar with menu button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setOpaque(false);

        JButton menuButton;
        File iconFile = new File("menu.png");
        if (iconFile.exists()) {
            ImageIcon menuIcon = new ImageIcon("menu.png");
            Image scaled = menuIcon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            menuButton = new JButton(new ImageIcon(scaled));
        } else {
            menuButton = new JButton("☰");
        }

        menuButton.setFocusPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setBorderPainted(false);
        menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuButton.addActionListener(e -> toggleMenu());
        topBar.add(menuButton);

        backgroundPanel.add(topBar, BorderLayout.NORTH);

        // Main Split Pane
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(2);
        splitPane.setResizeWeight(0);
        splitPane.setOpaque(false);

        // Left profile panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);

        JPanel profilePanel = new JPanel();
        profilePanel.setOpaque(false);
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));

        // Profile Picture
        JLayeredPane picLayer = new JLayeredPane();
        picLayer.setPreferredSize(new Dimension(130, 130));
        picLayer.setMaximumSize(new Dimension(130, 130));
        picLayer.setAlignmentX(Component.LEFT_ALIGNMENT);
        picLayer.setLayout(null);

        JLabel profilePic = new JLabel(name.substring(0,1).toUpperCase(), SwingConstants.CENTER);
        profilePic.setOpaque(true);
        profilePic.setBackground(new Color(185,170,255));
        profilePic.setFont(new Font("Arial", Font.BOLD, 45));
        profilePic.setForeground(Color.WHITE);
        profilePic.setBounds(0,0,120,120);
        profilePic.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon cameraIcon = new ImageIcon("camera.png");
        Image camImg = cameraIcon.getImage().getScaledInstance(24,24,Image.SCALE_SMOOTH);
        JLabel cameraLabel = new JLabel(new ImageIcon(camImg));
        cameraLabel.setBounds(96,96,24,24);
        cameraLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        MouseAdapter picClick = new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                File picturesFolder = new File(System.getProperty("user.home"), "Pictures");
                JFileChooser fileChooser = new JFileChooser(picturesFolder);
                int result = fileChooser.showOpenDialog(null);
                if(result == JFileChooser.APPROVE_OPTION){
                    ImageIcon icon = new ImageIcon(fileChooser.getSelectedFile().getAbsolutePath());
                    Image img = icon.getImage().getScaledInstance(profilePic.getWidth(), profilePic.getHeight(), Image.SCALE_SMOOTH);
                    profilePic.setIcon(new ImageIcon(img));
                    profilePic.setText("");
                }
            }
        };
        profilePic.addMouseListener(picClick);
        cameraLabel.addMouseListener(picClick);

        picLayer.add(profilePic, Integer.valueOf(0));
        picLayer.add(cameraLabel, Integer.valueOf(1));

        profilePanel.add(picLayer);
        profilePanel.add(Box.createRigidArea(new Dimension(0,20)));

        // Profile labels
        nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(Color.BLACK);
        profilePanel.add(nameLabel);

        emailLabel = new JLabel(email);
        emailLabel.setForeground(Color.DARK_GRAY);
        profilePanel.add(emailLabel);

        profilePanel.add(Box.createRigidArea(new Dimension(0,20)));

        JLabel deptTitle = new JLabel("DEPARTMENT");
        deptTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        deptTitle.setForeground(Color.GRAY);
        profilePanel.add(deptTitle);

        deptLabel = new JLabel("🏫 " + department);
        deptLabel.setFont(new Font("Arial", Font.BOLD, 18));
        deptLabel.setForeground(Color.BLACK);
        profilePanel.add(deptLabel);

        profilePanel.add(Box.createRigidArea(new Dimension(0,20)));

        JLabel sessionTitle = new JLabel("SESSION");
        sessionTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        sessionTitle.setForeground(Color.GRAY);
        profilePanel.add(sessionTitle);

        sessionLabel = new JLabel("📚 Session: " + session);
        sessionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        sessionLabel.setForeground(Color.BLACK);
        profilePanel.add(sessionLabel);

        leftPanel.add(profilePanel, BorderLayout.NORTH);

        // Right Panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        JLabel eventsTitle = new JLabel("Your Events");
        eventsTitle.setFont(new Font("Arial", Font.BOLD, 18));
        eventsTitle.setForeground(Color.BLACK);
        eventsTitle.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        rightPanel.add(eventsTitle, BorderLayout.NORTH);

        eventsList = new JPanel();
        eventsList.setLayout(new BoxLayout(eventsList, BoxLayout.Y_AXIS));
        eventsList.setOpaque(false);

        // Initialize event labels and store in array
        for(int i=0;i<5;i++){
            JLabel event = new JLabel("Event " + (i+1));
            event.setOpaque(true);
            event.setBackground(new Color(200,200,255,150));
            event.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            event.setAlignmentX(Component.LEFT_ALIGNMENT);
            event.setMaximumSize(new Dimension(Integer.MAX_VALUE,50));
            eventsList.add(event);
            eventsList.add(Box.createRigidArea(new Dimension(0,10)));
            eventLabels[i] = event; // store reference
        }

        JScrollPane scrollPane = new JScrollPane(eventsList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        backgroundPanel.add(splitPane, BorderLayout.CENTER);
        setContentPane(backgroundPanel);

        setupSlideMenu();

        setVisible(true);
    }

    // -------------------- SLIDE MENU --------------------
    private void setupSlideMenu() {
        slideMenu = new JPanel();
        slideMenu.setLayout(new BoxLayout(slideMenu, BoxLayout.Y_AXIS));
        slideMenu.setBackground(new Color(245,245,245,230));
        slideMenu.setBounds(-menuWidth, 0, menuWidth, Toolkit.getDefaultToolkit().getScreenSize().height);
        slideMenu.setBorder(BorderFactory.createEmptyBorder(30,20,20,20));
        slideMenu.setOpaque(true);

        String[] options = {
            "Dashboard", "Edit Profile", "Create / Join Event",
            "Change Password", "Sign Out", "Help & Support"
        };

        for (String opt : options) {
            JButton btn = new JButton(opt);
            btn.setFocusPainted(false);
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.BLACK);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(menuWidth - 40, 40));
            btn.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

            // --- BUTTON ACTIONS ---
            btn.addActionListener(e -> {
                if(opt.equals("Dashboard")){
                    JOptionPane.showMessageDialog(this, "Dashboard refreshed");
                } else if(opt.equals("Edit Profile")){
                    JTextField nameField = new JTextField(nameLabel.getText());
                    JTextField emailField = new JTextField(emailLabel.getText());
                    JTextField deptField = new JTextField(deptLabel.getText().replace("🏫 ", ""));
                    JTextField sessionField = new JTextField(sessionLabel.getText().replace("📚 Session: ", ""));

                    JPanel panel = new JPanel(new GridLayout(4,2,5,5));
                    panel.add(new JLabel("Name:")); panel.add(nameField);
                    panel.add(new JLabel("Email:")); panel.add(emailField);
                    panel.add(new JLabel("Department:")); panel.add(deptField);
                    panel.add(new JLabel("Session:")); panel.add(sessionField);

                    if(JOptionPane.showConfirmDialog(this, panel, "Edit Profile", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
                        nameLabel.setText(nameField.getText());
                        emailLabel.setText(emailField.getText());
                        deptLabel.setText("🏫 " + deptField.getText());
                        sessionLabel.setText("📚 Session: " + sessionField.getText());
                    }
                } else if(opt.equals("Create / Join Event")){
                    String event = JOptionPane.showInputDialog(this, "Enter event name:");
                    if(event != null && !event.trim().isEmpty()){
                        addEvent(event); // replace next Event label
                    }
                } else if(opt.equals("Change Password")){
                    JPasswordField oldP = new JPasswordField();
                    JPasswordField newP = new JPasswordField();
                    JPanel p = new JPanel(new GridLayout(2,2,5,5));
                    p.add(new JLabel("Old Password:")); p.add(oldP);
                    p.add(new JLabel("New Password:")); p.add(newP);
                    JOptionPane.showConfirmDialog(this, p, "Change Password", JOptionPane.OK_CANCEL_OPTION);
                } else if(opt.equals("Sign Out")){
                    dispose();
                    JOptionPane.showMessageDialog(null, "Signed Out");
                }
                toggleMenu();
            });

            slideMenu.add(btn);
            slideMenu.add(Box.createRigidArea(new Dimension(0,10)));
        }

        JButton closeBtn = new JButton("❌ Close");
        closeBtn.setFocusPainted(false);
        closeBtn.setBackground(Color.WHITE);
        closeBtn.setForeground(Color.BLACK);
        closeBtn.addActionListener(e -> toggleMenu());
        slideMenu.add(Box.createVerticalGlue());
        slideMenu.add(closeBtn);

        getLayeredPane().add(slideMenu, JLayeredPane.POPUP_LAYER);
    }

    private void toggleMenu() {
        if (slideTimer != null && slideTimer.isRunning()) return;

        menuVisible = !menuVisible;

        slideTimer = new Timer(5, e -> {
            if(menuVisible){
                if(currentX < 0){
                    currentX += 10;
                    slideMenu.setLocation(currentX, 0);
                } else {
                    currentX = 0;
                    slideMenu.setLocation(0,0);
                    slideTimer.stop();
                }
            } else {
                if(currentX > -menuWidth){
                    currentX -= 10;
                    slideMenu.setLocation(currentX,0);
                } else {
                    currentX = -menuWidth;
                    slideMenu.setLocation(currentX,0);
                    slideTimer.stop();
                }
            }
        });
        slideTimer.start();
    }

    public void addEvent(String eventName) {
        if(nextEventIndex < eventLabels.length){
            eventLabels[nextEventIndex].setText(eventName);
            nextEventIndex++;
            eventsList.revalidate();
            eventsList.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "All event slots are full!");
        }
    }

    public static void main(String[] args) {
        new Dashboard("Test User", "test@mail.com", "CSE", "2021-22");
    }
}










