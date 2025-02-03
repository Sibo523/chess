import javax.swing.*;
import java.io.*;
import java.net.*;

public class ChessClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private NetworkedScreen screen;
    private String color;
    
    // We use a static reference so that the GUI code can send moves.
    public static ChessClient instance;
    
    public ChessClient(String serverAddress, int port) throws Exception {
        socket = new Socket(serverAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        instance = this;
        screen = new NetworkedScreen();
        // Start listening to server messages.
        new Thread(new Listener()).start();
    }
    
    // Send a move command to the server.
    public void sendMove(int fromX, int fromY, int toX, int toY) {
        out.println("MOVE " + fromX + " " + fromY + " " + toX + " " + toY);
    }
    
    // Called from NetworkedScreen via a static helper.
    public static void sendMoveStatic(int fromX, int fromY, int toX, int toY) {
        if (instance != null) {
            instance.sendMove(fromX, fromY, toX, toY);
        }
    }
    
    class Listener implements Runnable {
        @Override
        public void run() {
            try {
                String line;
                while((line = in.readLine()) != null) {
                    System.out.println("Server: " + line);
                    if (line.startsWith("COLOR")) {
                        // e.g. "COLOR WHITE"
                        String[] tokens = line.split(" ");
                        color = tokens[1];
                        screen.setPlayerColor(color);
                    } else if (line.startsWith("START")) {
                        screen.setMessage("Game started. You are " + color + ".");
                    } else if (line.startsWith("MOVE")) {
                        // e.g. "MOVE fromX fromY toX toY"
                        String[] tokens = line.split(" ");
                        int fromX = Integer.parseInt(tokens[1]);
                        int fromY = Integer.parseInt(tokens[2]);
                        int toX = Integer.parseInt(tokens[3]);
                        int toY = Integer.parseInt(tokens[4]);
                        screen.applyMove(fromX, fromY, toX, toY);
                    } else if (line.startsWith("TURN")) {
                        screen.setMessage("Turn: " + line.substring(5));
                    } else if (line.startsWith("ERROR")) {
                        screen.setMessage(line);
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void show() {
        JFrame frame = new JFrame("Chess Client (" + color + ")");
        frame.setContentPane(screen.getGui());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) throws Exception {
        // Change "localhost" to your server’s IP if needed.
        ChessClient client = new ChessClient("localhost", 5000);
        client.show();
    }
}
