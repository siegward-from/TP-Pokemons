package client;

import java.io.*;
import java.net.Socket;

/**
 * L'interface pour le joueur et communication avec le serveur
 */
public class Client {

    private static Socket clientSocket;
    private static BufferedReader reader;
    private static BufferedReader in;
    private static BufferedWriter out;

    public static void main(String[] args) {
        try {
            try {
                Client.connectToServer();
                Client.initialize();
                Client.mainGameCycle();
            } finally {
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Start menu de jeu
     */
    private static void showStartMenu() {
        System.out.println("1) Connecter");
        System.out.println("2) Créer un joueur");
    }

    /**
     * Le menu principal
     */
    private static String MainMenu() throws IOException {
        System.out.println("1) Montrer mes Pokémons");
        System.out.println("2) Montrer mes bonbons");
        System.out.println("3) Attraper des Pokémon");
        System.out.println("4) Rejoinder la bataille dans l'arène");
        System.out.println("5) Augmenter le niveau d'un Pokémon");
        System.out.println("6) Sortir");
        String clientMessage = reader.readLine();
        while (!Client.answerIsCorrect(clientMessage, 4)) {
            System.out.println("Réponse incorrecte");
            clientMessage = reader.readLine();
        }
        Client.send(clientMessage);
        return clientMessage;
    }

    /**
     * La fonction pour envoyer un message au serveur
     */
    private static void send(String msg) throws IOException {
        out.write(msg + "\n");
        out.flush();
    }

    /**
     * Vérifie si answer est un nombre et answer est inférieur à la limite
     */
    private static boolean answerIsCorrect(String answer, int limit) {
        for (int i = 0; i < answer.length(); i++) {
            if (!Character.isDigit(answer.charAt(i))) {
                return false;
            }
        }
        return Integer.parseInt(answer) <= limit || Integer.parseInt(answer) >= 1;
    }

    /**
     * Les instructions pour choisi le pokemon
     */
    private static void choosePokemon() throws IOException {
        String serverMessage = in.readLine();
        while (!serverMessage.isEmpty()) {
            System.out.println(serverMessage);
            serverMessage = in.readLine();
        }
        serverMessage = in.readLine();
        String clientMessage = reader.readLine();
        while (!Client.answerIsCorrect(clientMessage, Integer.parseInt(serverMessage))) {
            System.out.println("Réponse incorrecte");
            clientMessage = reader.readLine();
        }
        Client.send(clientMessage);
    }

    /**
     * Connecter au serveur
     */
    private static void connectToServer() throws IOException {
        clientSocket = new Socket("0.0.0.0", 4040);
        reader = new BufferedReader(new InputStreamReader(System.in));
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    }

    /**
     * Boucle de jeu principale
     * Lit les messages de la console, les envoie au serveur et exécute les commandes appropriées
     */
    private static void mainGameCycle() throws IOException {
        String serverMessage;
        main: while (true) {
            String clientMessage = Client.MainMenu();
            switch (clientMessage) {

                // Afficher pokemons ou bonbons ou attraper le pokemon
                case "1": case "2": case "3":
                    serverMessage = in.readLine();
                    System.out.println(serverMessage);
                    while (!serverMessage.isEmpty()) {
                        serverMessage = in.readLine();
                        System.out.println(serverMessage);
                    }
                    break;

                // Rejoinder la bataille dans l'arène
                case "4":
                    serverMessage = in.readLine();
                    System.out.println(serverMessage);
                    if (serverMessage.equals("Vous n'avez pas de Pokémon")) {
                        break;
                    }
                    Client.choosePokemon();

                    while (true) {
                        serverMessage = in.readLine();
                        if (serverMessage.equals("Fin du duel")) {
                            break;
                        }
                        System.out.println(serverMessage);
                    }
                    break;

                // Augmenter le niveau d'un Pokémon
                case "5":
                    Client.choosePokemon();
                    System.out.println(in.readLine());
                    break;
                // Sortir
                case "6":
                    clientSocket.close();
                    in.close();
                    out.close();
                    break main;
            }
        }
    }

    /**
     * Initialisation du joueur
     * Connection avec un pseudo ou créez un nouveau joueur
     */
    private static void initialize() throws IOException {
        Client.showStartMenu();
        String msg = reader.readLine();
        while (!Client.answerIsCorrect(msg, 2)) {
            System.out.println("Réponse incorrecte");
            msg = reader.readLine();
        }
        Client.send(msg);
        System.out.print("Username: ");
        Client.send(reader.readLine());
        if (msg.equals("1")) {
            String answer = in.readLine();
            System.out.println(answer);
            while (answer.equals("Joueur non trouvé")) {
                System.out.print("Username: ");
                Client.send(reader.readLine());
                answer = in.readLine();
                System.out.println(answer);
            }
        } else if (msg.equals("2")) {
            System.out.println(in.readLine());
        }
    }
}