package game;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Sérialisation des Pokémon de base
 */
public class SerializationTest {
    public static void main(String[] args) {

        EvolutionFormPokemon Florizarre = new EvolutionFormPokemon("Florizarre", new String[] {"Plante", "Poison"});
        EvolutionFormPokemon Herbizarre = new EvolutionFormPokemon("Herbizarre", new String[] {"Plante", "Poison"}, Florizarre);
        Pokemon Bulbizarre = new Pokemon("Bulbizarre", new String[] {"Plante", "Poison"}, Herbizarre);

        EvolutionFormPokemon Dracaufeu = new EvolutionFormPokemon("Dracaufeu", new String[] {"Feu", "Vol"});
        EvolutionFormPokemon Reptincel = new EvolutionFormPokemon("Reptincel", new String[] {"Feu", null}, Dracaufeu);
        Pokemon Salameche = new Pokemon("Salameche", new String[] {"Feu", null}, Reptincel);

        EvolutionFormPokemon Tortank = new EvolutionFormPokemon("Tortank", new String[] {"Eau", null});
        EvolutionFormPokemon Carabaffe = new EvolutionFormPokemon("Carabaffe", new String[] {"Eau", null}, Tortank);
        Pokemon Carapuce = new Pokemon("Carapuce", new String[] {"Eau", null}, Carabaffe);

        EvolutionFormPokemon Papiluison = new EvolutionFormPokemon("Papilusion", new String[] {"Insecte", "Vol"});
        EvolutionFormPokemon Chrysacier = new EvolutionFormPokemon("Chrysacier", new String[] {"Insecte", "Vol"}, Papiluison);
        Pokemon Chenipan = new Pokemon("Chenipan", new String[] {"Insecte", "Vol"}, Chrysacier);

        EvolutionFormPokemon Dardargnan = new EvolutionFormPokemon("Dardargnan", new String[] {"Insecte", "Poison"});
        EvolutionFormPokemon Coconfort = new EvolutionFormPokemon("Coconfort", new String[] {"Insecte", "Poison"}, Dardargnan);
        Pokemon Aspicot = new Pokemon("Aspicot", new String[] {"Insecte", "Poison"}, Coconfort);

        EvolutionFormPokemon Roucarnage = new EvolutionFormPokemon("Roucarnage", new String[] {"Normal", "Vol"});
        EvolutionFormPokemon Roucoups = new EvolutionFormPokemon("Roucoups", new String[] {"Normal", "Vol"}, Roucarnage);
        Pokemon Roucool = new Pokemon("Roucool", new String[] {"Normal", "Vol"}, Roucoups);

        EvolutionFormPokemon Rattatac = new EvolutionFormPokemon("Rattatac", new String[] {"Normal", null});
        Pokemon Rattata = new Pokemon("Rattata", new String[] {"Normal", null}, Rattatac);

        EvolutionFormPokemon Rapasdepic = new EvolutionFormPokemon("Rapasdepic", new String[] {"Normal", "Vol"});
        Pokemon Piafabec = new Pokemon("Piafabec", new String[] {"Normal", "Vol"}, Rapasdepic);

        EvolutionFormPokemon Arbok = new EvolutionFormPokemon("Arbok", new String[] {"Poison", null});
        Pokemon Abo = new Pokemon("Abo", new String[] {"Posion", null}, Arbok);

        EvolutionFormPokemon Raichu = new EvolutionFormPokemon("Raichu", new String[] {"Electrique", null});
        Pokemon Pikachu = new Pokemon("Pikachu", new String[] {"Electrique", null}, Raichu);

        Pokemon[] basePokemonsArray = {Bulbizarre, Salameche, Carapuce, Chenipan, Aspicot, Roucool, Rattata, Piafabec, Abo, Pikachu};

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("BasePokemonsArray.dat"))) {
            oos.writeObject(basePokemonsArray);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        ArrayList<Player> PlayersList = new ArrayList<>();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("PlayersArray.dat"))) {
            oos.writeObject(PlayersList);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}