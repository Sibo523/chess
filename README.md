# Chess

A Java-based chess game featuring a networked client–server architecture. This project supports standard chess rules—including castling and en passant—and comes with a Swing–based GUI that highlights legal moves and shows whose turn it is.

## Overview

This project is a modular chess game implemented in Java. It includes:
- **Board and Piece Logic:** Object-oriented design with separate classes for each chess piece (Pawn, Rook, Knight, Bishop, Queen, King) and a Board class that validates moves, handles special moves (castling, en passant, and pawn promotion), and tracks game state.
- **Graphical User Interface (GUI):** A Swing-based interface that displays the board, highlights selected pieces and legal moves, and shows status messages.
- **Networked Play:** A simple client–server architecture that allows two players to connect from different machines. The server (run via `ChessServer`) is the authority for the game state, and two clients (run via `ChessClient`) connect to play.

## Features

- **Standard Chess Moves:** Validates legal moves for each piece.
- **Special Moves:** Supports castling, en passant, and pawn promotion (auto-promotes to queen by default).
- **Networked Multiplayer:** A server accepts two clients, assigning one to White and one to Black.
- **GUI Enhancements:** The GUI highlights the selected square and legal moves, and updates messages to indicate whose turn it is.
- **Modular Design:** The code is organized into separate classes for better maintainability and future enhancements.

## Requirements

- Java JDK (tested with Java 17+; your setup uses Java 23 OpenJDK)
- Basic command-line tools (or your favorite IDE)

## Getting Started

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/Sibo523/chess.git
   cd chess
   ```
2. **Compile the Project:**
    ```bash
    javac -d bin src/*.java
    ```
This compiles all Java files in src and outputs the class files into the bin directory.

3. **Run the Server:**

Start the server in one terminal window:
```bash
    java -cp bin ChessServer
```
4. **Run the Client(s):**

On two separate machines (or two separate terminals on the same machine),
 run:

```bash
java -cp bin ChessClient
```
Each client will be assigned a color (White or Black) by the server.

## Project Structure
chess/
├── src/
│   ├── Board.java
│   ├── ChessServer.java
│   ├── ChessClient.java
│   ├── NetworkedScreen.java
│   ├── Coordinate.java
│   ├── Piece.java
│   ├── Pawn.java
│   ├── Rook.java
│   ├── Knight.java
│   ├── Bishop.java
│   ├── Queen.java
│   └── King.java
└── README.md

## Description of every file
- **Board.java**: Contains the game logic for move validation, special moves (castling, en passant, promotion), and overall game state.
- **Piece.java & Subclasses**: Each piece (Pawn, Rook, Knight, Bishop, Queen, King) implements its own movement logic.
- **ChessServer.java**: Handles incoming connections, assigns players, validates moves, and broadcasts updates.
- **ChessClient.java**: Connects to the server and updates the local UI based on server messages.
- **NetworkedScreen.java**: Provides the GUI for the game, including move highlighting and status messages.

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for more details.

---






