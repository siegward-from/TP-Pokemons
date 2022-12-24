package server;

import game.Player;
import game.Pokemon;

import java.io.*;
import java.net.Socket;

/**
 * ServerConnection - Thread pour gérer le joueur connecté
 */
class ServerConnection extends Thread {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Player player;

    public ServerConnection(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }

    /**
     * La fonction principale exécutée lors de la création d'une instance ServerConnection
     */
    @Override
    public void run() {

        try {
            this.initializePlayer();
            this.mainCycle();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * La fonction pour envoyer un message au joueur
     */
    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * La fonction pour sélectionner pokemon
     */
    private Pokemon choosePokemon() throws IOException {
        int i = 1;
        for (Pokemon pkmn : this.player.getPokemons()) {
            this.send(i + ") " + pkmn.toString());
            i++;
        }
        this.send("");
        this.send(Integer.toString(i));
        String answer = in.readLine();
        return this.player.getPokemons().get(Integer.parseInt(answer) - 1);
    }

    /**
     * Afficher les résultats de la bataille
     */
    private static void printResultFight(ServerConnection sc1, ServerConnection sc2, Pokemon pokemon1, Pokemon pokemon2) {
        sc1.send(pokemon1.getName() + " a gagné\n");
        sc2.send(pokemon1.getName() + " a gagné\n");
        sc1.player.addBonbon(pokemon1.getType()[0]);
        sc1.player.addBonbon(pokemon1.getType()[0]);
        sc2.player.addBonbon(pokemon2.getType()[0]);
        pokemon1.setHP(pokemon1.getPV());
        pokemon2.setHP(pokemon2.getPV());
    }

    /**
     * Mèner une bataille entre le joueur sc1.player et son Pokémon pokemon1 et
     * le joueur sc2.player et son Pokémon pokemon2
     */
    private static void fight(ServerConnection sc1, ServerConnection sc2, Pokemon pokemon1, Pokemon pokemon2) {
        String attackMessage;
        while (pokemon1.getHP() > 0 && pokemon2.getHP() > 0) {
            attackMessage = pokemon1.attack(pokemon2);
            sc1.send(attackMessage);
            sc2.send(attackMessage);
            if (pokemon2.getHP() <= 0) {
                break;
            }
            attackMessage = pokemon2.attack(pokemon1);
            sc1.send(attackMessage);
            sc2.send(attackMessage);
        }
        if (pokemon1.getHP() == 0) {
            ServerConnection.printResultFight(sc2, sc1, pokemon2, pokemon1);
        } else {
            ServerConnection.printResultFight(sc1, sc2, pokemon1, pokemon2);
        }
        sc1.send("Fin du duel");
        sc2.send("Fin du duel");
    }

    /**
     * Initialisation du joueur
     * Connection avec un pseudo ou créez un nouveau joueur
     */
    private void initializePlayer() {
        try {
            String answer = in.readLine();
            if (answer.equals("1")) {
                main: while (true) {
                    answer = in.readLine();
                    for (Player plr : Server.players) {
                        if (plr.getUsername().equals(answer)) {
                            this.player = plr;
                            this.send("Vous êtes connecté");
                            break main;
                        }
                    }
                    this.send("Joueur non trouvé");
                }
            } else if (answer.equals("2")) {
                player = new Player(in.readLine());
                Server.players.add(player);
                this.send("Création réussie");
            }
            System.out.println(player.getUsername() + " s'est connecté");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * La boucle de jeu principale
     * Reçoit les messages du joueur en nombre et effectue les actions appropriées
     * jusqu'à ce qu'il reçoive un message pour quitter le jeu
     */
    private void mainCycle() throws IOException {
        main: while (true) {
            String clientMessage = in.readLine();
            switch (clientMessage) {

                // Afficher tous les pokemons
                case "1":
                    for (Pokemon pokemon : this.player.getPokemons()) {
                        this.send(pokemon.toString());
                    }
                    this.send("");
                    break;

                // Afficher tous les bonbons
                case "2":
                    for (String type: Player.getTypes()) {
                        this.send(type + ": " + this.player.getBonbons().get(type));
                    }
                    this.send("");
                    break;

                // Attraper le pokemon
                case "3":
                    this.send(player.catchPokemon());
                    this.send("");
                    break;

                // Rejoinder la bataille dans l'arène
                case "4":
                    if (this.player.getPokemons().isEmpty()) {
                        this.send("Vous n'avez pas de Pokémon");
                        break;
                    }
                    boolean check = false;
                    for (ServerConnection sc: Server.serverConnections) {
                        if (sc.getPlayer() != null) {
                            if (sc.getPlayer().isWaiting()) {
                                this.send("L'adversaire choisit un Pokémon");
                                sc.send("Qui va se battre?");
                                Pokemon pokemon1 = sc.choosePokemon();
                                sc.send("L'adversaire choisit un Pokémon");
                                this.send("Qui va se battre?");
                                Pokemon pokemon2 = this.choosePokemon();
                                ServerConnection.fight(sc, this, pokemon1, pokemon2);
                                sc.player.stopWaiting();
                                check = true;
                                break;
                            }
                        }
                    }
                    if (check) {
                        break;
                    }
                    this.send("En attente d'un adversaire");
                    this.player.startWaiting();
                    while (this.player.isWaiting()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;

                // Augmenter le niveau d'un Pokémon
                case "5":
                    this.send("Quel pokémon voulez vous monter de niveau?");
                    Pokemon pokemon = this.choosePokemon();
                    if (this.player.getBonbons().get(pokemon.getType()[0]) > 0) {
                        pokemon.levelUp();
                        this.player.subBonbon(pokemon.getType()[0]);
                        this.send(pokemon.toString());
                        break;
                    } else if (pokemon.getType()[1] != null) {
                        if (this.player.getBonbons().get(pokemon.getType()[1]) > 0) {
                            pokemon.levelUp();
                            this.player.subBonbon(pokemon.getType()[1]);
                            this.send(pokemon.toString());
                            break;
                        }
                    }
                    this.send("Vous ne pouvez pas monter de niveau de ce pokémon");
                    break;

                // Sortir
                case "6":
                    for (Player player: Server.players) {
                        if (player.getUsername().equals(this.player.getUsername())) {
                            int i = Server.players.indexOf(player);
                            Server.players.set(i, player);
                            Player.serialize(Server.players);
                            socket.close();
                            in.close();
                            out.close();
                            break main;
                        }
                    }
                    Server.players.add(this.player);
                    Player.serialize(Server.players);
                    socket.close();
                    in.close();
                    out.close();
                    break main;
            }
        }
    }
}