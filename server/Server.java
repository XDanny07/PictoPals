
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {
    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;
    ServerSocket serverSocket;
    JLabel heading = new JLabel("Server Chat ");
    WatermarkTextPane messagePane = new WatermarkTextPane("Server");
    JTextField messageInput = new JTextField();
    JButton sendButton = new JButton("Send");
    JButton imgselect = new JButton("Attach");
    JFrame imageFrame;
    File[] fileToSend = new File[1];
    Font font = new Font("Arial", Font.PLAIN, 16);

    public Server() {

        try {
            server = new ServerSocket(7776);
            System.out.println("Server is ready to accept connection");
            System.out.println("Waiting...");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            startRecieve();
            handleEvents();

            startReading();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        this.setTitle("Server Messenger");
        this.setSize(600, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(240, 240, 240)); // Set background color

        heading.setFont(new Font("Arial", Font.BOLD, 40)); // Change font and size
        heading.setForeground(new Color(70, 130, 180)); // Change text color
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        messagePane.setEditable(false);
        messagePane.setFont(font);
        messagePane.setBackground(new Color(255, 255, 255)); // Change background color of text pane
        messagePane.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180))); // Add border
        messagePane.setMargin(new Insets(10, 10, 10, 10)); // Add some padding
        JScrollPane jScrollPane = new JScrollPane(messagePane);
        jScrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border from scroll pane

        messageInput.setFont(font);
        messageInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180)), // Add border
                BorderFactory.createEmptyBorder(5, 5, 5, 5))); // Add padding
        messageInput.setBackground(new Color(240, 240, 240)); // Change background color of text field

        sendButton.setFont(new Font("Arial", Font.BOLD, 16)); // Change font and size
        sendButton.setBackground(new Color(70, 130, 180)); // Change background color
        sendButton.setForeground(Color.WHITE); // Change text color
        sendButton.setFocusPainted(false); // Remove focus border
        sendButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor on hover

        imgselect.setFont(new Font("Arial", Font.BOLD, 16)); // Change font and size
        imgselect.setBackground(new Color(70, 130, 180)); // Change background color
        imgselect.setForeground(Color.WHITE); // Change text color
        imgselect.setFocusPainted(false); // Remove focus border
        imgselect.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding
        imgselect.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor on hover

        imgselect.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // Create a file chooser to open the dialog to choose a file.
                JFileChooser jFileChooser = new JFileChooser();
                // Set the title of the dialog.
                jFileChooser.setDialogTitle("Choose a file to send.");
                // Show the dialog and if a file is chosen from the file chooser execute the
                // following statements.
                if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    // Get the selected file.
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    // Change the text of the java swing label to have the file name.
                    imageFrame = new JFrame("Selected Image");
                    imageFrame.setSize(400, 400);
                    imageFrame.setLocationRelativeTo(null);
                    imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                    JLabel imageLabel = new JLabel();
                    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    imageLabel.setVerticalAlignment(SwingConstants.CENTER);

                    if (fileToSend[0] != null) {
                        ImageIcon imageIcon = new ImageIcon(fileToSend[0].getAbsolutePath());
                        Image image = imageIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                        imageIcon = new ImageIcon(image);
                        imageLabel.setIcon(imageIcon);
                    }

                    imageFrame.add(imageLabel);
                    imageFrame.setVisible(true);

                    JButton sendImageButton = new JButton("Send Image");
                    sendImageButton.setFont(new Font("Arial", Font.BOLD, 16)); // Change font and size
                    sendImageButton.setBackground(new Color(70, 130, 180)); // Change background color
                    sendImageButton.setForeground(Color.WHITE); // Change text color
                    sendImageButton.setFocusPainted(false); // Remove focus border
                    sendImageButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding
                    sendImageButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor on hover

                    sendImageButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // If a file has not yet been selected then display this message.
                            if (fileToSend[0] == null) {
                                return;
                                // If a file has been selected then do the following.
                            } else {
                                try {
                                    // Create an input stream into the file you want to send.
                                    FileInputStream fileInputStream = new FileInputStream(
                                            fileToSend[0].getAbsolutePath());
                                    // Create a socket connection to connect with the server.
                                    Socket socket = new Socket("localhost", 1234);
                                    // Create an output stream to write to write to the server over the socket
                                    // connection.
                                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                                    // Get the name of the file you want to send and store it in filename.
                                    String fileName = fileToSend[0].getName();
                                    // Convert the name of the file into an array of bytes to be sent to the server.
                                    byte[] fileNameBytes = fileName.getBytes();
                                    // Create a byte array the size of the file so don't send too little or too much
                                    // data to the server.
                                    byte[] fileBytes = new byte[(int) fileToSend[0].length()];
                                    // Put the contents of the file into the array of bytes to be sent so these
                                    // bytes can be sent to the server.
                                    fileInputStream.read(fileBytes);
                                    // Send the length of the name of the file so server knows when to stop reading.
                                    dataOutputStream.writeInt(fileNameBytes.length);
                                    // Send the file name.
                                    dataOutputStream.write(fileNameBytes);
                                    // Send the length of the byte array so the server knows when to stop reading.
                                    dataOutputStream.writeInt(fileBytes.length);
                                    // Send the actual file.
                                    dataOutputStream.write(fileBytes);
                                    socket.close();
                                    imageFrame.dispose();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    });

                    JPanel buttonPanel = new JPanel(new BorderLayout());
                    buttonPanel.setBackground(new Color(240, 240, 240)); // Change background color of button panel
                    buttonPanel.add(sendImageButton, BorderLayout.CENTER);

                    imageFrame.add(buttonPanel, BorderLayout.SOUTH);

                }
            }
        });

        JPanel btns = new JPanel(new BorderLayout());
        btns.setBackground(new Color(240, 240, 240)); // Change background color of input panel
        btns.add(sendButton, BorderLayout.EAST);
        btns.add(imgselect, BorderLayout.WEST);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(240, 240, 240)); // Change background color of input panel
        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(btns, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 255, 255)); // Change background color of main panel
        mainPanel.add(heading, BorderLayout.NORTH);
        mainPanel.add(jScrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        this.add(mainPanel);

        this.setVisible(true);
    }

    private void startRecieve() {
        try {
            serverSocket = new ServerSocket(1234);

        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        // Wait for a client to connect and when they do create a socket to communicate
                        // with them.
                        Socket socket = serverSocket.accept();

                        // Stream to receive data from the client through the socket.
                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                        // Read the size of the file name so know when to stop reading.
                        int fileNameLength = dataInputStream.readInt();
                        // If the file exists
                        if (fileNameLength > 0) {
                            // Byte array to hold name of file.
                            byte[] fileNameBytes = new byte[fileNameLength];
                            // Read from the input stream into the byte array.
                            dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                            // Read how much data to expect for the actual content of the file.
                            int fileContentLength = dataInputStream.readInt();
                            // If the file exists.
                            if (fileContentLength > 0) {
                                // Array to hold the file data.
                                byte[] fileContentBytes = new byte[fileContentLength];
                                // Read from the input stream into the fileContentBytes array.
                                dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);
                                // Panel to hold the picture and file name.

                                imageFrame = new JFrame("Selected Image");
                                imageFrame.setSize(400, 400);
                                imageFrame.setLocationRelativeTo(null);
                                imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                imageFrame.setVisible(true);
                                JPanel filePanel = new JPanel(new BorderLayout());
                                filePanel.setBackground(Color.WHITE); // Change background color of file panel

                                // Create an image label to display the received image.
                                JLabel receivedImageLabel = new JLabel();
                                receivedImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                receivedImageLabel.setVerticalAlignment(SwingConstants.CENTER);

                                // Create a button to download the received file.
                                JButton downloadButton = new JButton("Download");
                                downloadButton.setFont(new Font("Arial", Font.BOLD, 16)); // Change font and size
                                downloadButton.setBackground(new Color(70, 130, 180)); // Change background color
                                downloadButton.setForeground(Color.WHITE); // Change text color
                                downloadButton.setFocusPainted(false); // Remove focus border
                                downloadButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add
                                                                                                           // padding
                                downloadButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor on hover

                                // Add action listener to the download button
                                downloadButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        // Create a file chooser dialog to select the download location
                                        JFileChooser fileChooser = new JFileChooser();
                                        fileChooser.setDialogTitle("Select Download Location");
                                        int userSelection = fileChooser.showSaveDialog(imageFrame);
                                        if (userSelection == JFileChooser.APPROVE_OPTION) {
                                            // Get the selected file
                                            File selectedFile = fileChooser.getSelectedFile();
                                            // Create a file output stream to write the image data to the selected file
                                            try (FileOutputStream fos = new FileOutputStream(selectedFile)) {
                                                // Write the image data to the file
                                                fos.write(fileContentBytes);
                                                // Show a success message
                                                JOptionPane.showMessageDialog(imageFrame, "Image downloaded successfully!", "Download Success", JOptionPane.INFORMATION_MESSAGE);
                                            } catch (IOException ex) {
                                                // Show an error message if there was an error writing the file
                                                JOptionPane.showMessageDialog(imageFrame, "Error downloading image: " + ex.getMessage(), "Download Error", JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                        imageFrame.dispose();
                                    }
                                });

                                // Add the image label and download button to the file panel
                                filePanel.add(receivedImageLabel, BorderLayout.CENTER);
                                filePanel.add(downloadButton, BorderLayout.SOUTH);

                                // Set the received image and update the file panel
                                if (fileContentBytes != null) {
                                    ImageIcon receivedImageIcon = new ImageIcon(fileContentBytes);
                                    Image receivedImage = receivedImageIcon.getImage().getScaledInstance(300, 300,
                                            Image.SCALE_SMOOTH);
                                    receivedImageIcon = new ImageIcon(receivedImage);
                                    receivedImageLabel.setIcon(receivedImageIcon);
                                    filePanel.revalidate();
                                    filePanel.repaint();
                                }

                                // Add the file panel to the main frame
                                imageFrame.add(filePanel, BorderLayout.CENTER);

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        sendButton.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String contentToSend = messageInput.getText().trim();
        if (!contentToSend.equals("")) {
            appendToPane(messagePane, "User2" + contentToSend + "\n", Color.BLACK, true);
            out.println(contentToSend);
            out.flush();
            messageInput.setText("");
            messageInput.requestFocus();
        }
    }

    private void appendToPane(JTextPane tp, String msg, Color c, boolean isRight) {
        StyledDocument doc = tp.getStyledDocument();

        SimpleAttributeSet right = new SimpleAttributeSet();
        StyleConstants.setAlignment(right, isRight ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(right, c);
        StyleConstants.setFontSize(right, 20);
        StyleConstants.setSpaceAbove(right, 4);
        StyleConstants.setSpaceBelow(right, 4);

        try {
            doc.insertString(doc.getLength(), msg, null);
            doc.setParagraphAttributes(doc.getLength() - 1, 1, right, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started...");
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("Client terminated the chat");
                        socket.close();
                        break;
                    }
                    appendToPane(messagePane, "Client: " + msg + "\n", Color.BLACK, false);
                }
            } catch (Exception e) {
                System.out.println("Connection closed");
            }
        };
        new Thread(r1).start();
    }

    class WatermarkTextPane extends JTextPane {
        private String watermarkText;

        public WatermarkTextPane(String watermarkText) {
            this.watermarkText = watermarkText;
            this.setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(220, 220, 220, 128));
            g.setFont(new Font("Arial", Font.BOLD, 100));
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(watermarkText)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(watermarkText, x, y);
        }
    }

    public static void main(String[] args) {
        System.out.println("This is the server. Going to start server");
        new Server();
    }
}
