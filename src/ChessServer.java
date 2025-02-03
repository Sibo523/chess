import java.io.*;
import java.net.*;
import java.util.*;

public class ChessServer {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private Board board;
    
    public ChessServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        board = new Board();
    }
    
    public void start() {
        System.out.println("Chess server started. Waiting for players...");
        // Wait for exactly two players.
        while (clients.size() < 2) {
            try {
                Socket socket = serverSocket.accept();
                // The first player is WHITE, the second BLACK.
                ClientHandler client = new ClientHandler(socket, clients.size() == 0 ? "WHITE" : "BLACK");
                clients.add(client);
                new Thread(client).start();
                System.out.println("Player " + client.getColor() + " connected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        broadcast("START");
        broadcast("TURN " + board.getTurnString());
    }
    
    // Broadcast a message to all clients.
    public synchronized void broadcast(String msg) {
        for (ClientHandler client : clients) {
            client.sendMessage(msg);
        }
    }
    
    // Process a move from a client.
    public synchronized void handleMove(String color, int fromX, int fromY, int toX, int toY, ClientHandler sender) {
        // Only allow the move if it’s that player’s turn.
        if (!color.equals(board.getTurnString().toUpperCase())) {
            sender.sendMessage("ERROR Not your turn.");
            return;
        }
        Coordinate from = new Coordinate(fromX, fromY);
        Coordinate to = new Coordinate(toX, toY);
        if (board.movePiece(from, to)) {
            // Valid move: broadcast it so all clients can update.
            broadcast("MOVE " + fromX + " " + fromY + " " + toX + " " + toY);
            broadcast("TURN " + board.getTurnString());
        } else {
            sender.sendMessage("ERROR Invalid move.");
        }
    }
    
    class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String color;
        
        public ClientHandler(Socket socket, String color) {
            this.socket = socket;
            this.color = color;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        
        public String getColor() {
            return color;
        }
        
        public void sendMessage(String msg) {
            out.println(msg);
        }
        
        @Override
        public void run() {
            // Send initial assignment.
            sendMessage("COLOR " + color);
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Received from " + color + ": " + line);
                    // Expect messages like: "MOVE fromX fromY toX toY"
                    if (line.startsWith("MOVE")) {
                        String[] tokens = line.split(" ");
                        int fromX = Integer.parseInt(tokens[1]);
                        int fromY = Integer.parseInt(tokens[2]);
                        int toX = Integer.parseInt(tokens[3]);
                        int toY = Integer.parseInt(tokens[4]);
                        handleMove(color, fromX, fromY, toX, toY, this);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) throws IOException {
        ChessServer server = new ChessServer(5000);
        server.start();
    }
}
