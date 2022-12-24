package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import game.*;

/*
Le serveur accepte de nouvelles connexions, crée de nouveaux threads pour eux et les stocke dans un tableau
 */
public class Server {

    public static final int PORT = 4040;
    public static LinkedList<ServerConnection> serverConnections = new LinkedList<>();
    public static ArrayList<Player> players = Player.deserialize();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        try {
            while (true) {
                Socket socket = server.accept();
                System.out.println("New connection");
                try {
                    serverConnections.add(new ServerConnection(socket));
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }
}