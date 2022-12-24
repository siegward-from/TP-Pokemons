package game;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


/**
 * pokemons - pokedex: liste de pokemon
 * bonbons - Dictionnaire<type de bonbon, quantité>
 * isWaiting - statut du joueur en attendant qu'un autre joueur se batte
 */
public class Player implements Serializable {

    private String username;
    private ArrayList<Pokemon> pokemons;
    private HashMap<String, Integer> bonbons;
    private boolean isWaiting;
    private static final Random r = new Random();
    private static final Pokemon[] BasePokemons = Pokemon.deserialize();
    private static String[] types = {"Normal", "Feu", "Eau", "Plante", "Electrique", "Poison", "Vol", "Insecte"};

    public Player(String name) {
        this.username = name;
        pokemons = new ArrayList<Pokemon>();
        bonbons = new HashMap<>();
        isWaiting = false;
        for (String type: types) {
            bonbons.put(type, 0);
        }
    }

    /**
     * Ajouter un pokemon de base aléatoire au pokedex
     */
    public String catchPokemon() {
        if (pokemons.size() >= 6) {
            return "Vous avez le nombre maximum de Pokémon";
        }
        Pokemon pokemon = BasePokemons[r.nextInt(BasePokemons.length)];
        while (pokemons.contains(pokemon)) {
            pokemon = BasePokemons[r.nextInt(BasePokemons.length)];
        }
        pokemons.add(pokemon);
        return "Vous avez attrapé un Pokémon: " + pokemon.toString();
    }

    /**
     * Ajouter un bonbon
     */
    public void addBonbon(String type) {
        int num = this.bonbons.get(type);
        this.bonbons.put(type, ++num);
    }

    /**
     * Enlève un bonbon
     */
    public void subBonbon(String type) {
        int num = this.bonbons.get(type);
        this.bonbons.put(type, --num);
    }

    /**
     * Sérialise une liste de joueur dans un PlayersArray.dat
     */
    public static void serialize(ArrayList<Player> players) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("PlayersArray.dat"))) {
            oos.writeObject(players);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Désérialise une liste de joueur de PlayersArray.dat
     */
    public static ArrayList<Player> deserialize() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("PlayersArray.dat"))) {
            ArrayList<Player> Players = (ArrayList) ois.readObject();
            return Players;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    // get/set functions

    @Override
    public String toString() {
        return getUsername();
    }

    public String getUsername() {
        return username;
    }

    public HashMap<String, Integer> getBonbons() {
        return bonbons;
    }

    public ArrayList<Pokemon> getPokemons() {
        return pokemons;
    }

    public static String[] getTypes() {
        return types;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public void stopWaiting() {
        isWaiting = false;
    }

    public void startWaiting() {
        isWaiting = true;
    }
}
