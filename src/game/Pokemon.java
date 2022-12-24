package game;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

/**
 * Classe pokémon
 * HP - points de vie actuels
 * VP - points de vie
 * PC - points de combat
 * evolution - forme évolutive
 * types - types de pokémon déterminent les résistances et les faiblesses du Pokémon
 */
public class Pokemon implements Serializable {
    private String name;
    private String[] type;
    private EvolutionFormPokemon evolution;
    private int HP;
    private int PV;
    private int PC;
    private int level;
    private static Random r = new Random();
    private static String[] types = {"Normal", "Feu", "Eau", "Plante", "Electrique", "Poison", "Vol", "Insecte"};
    private static final int MAX_LEVEL = 20;
    private static double[][] resistances = {
            {1, 1, 1, 1, 1, 1, 1, 1,},
            {1, 0.5, 2, 0.5, 1, 1, 1, 0.5},
            {1, 0.5, 0.5, 2, 2, 1, 1, 1},
            {1, 2, 0.5, 0.5, 0.5, 2, 2, 2},
            {1, 1, 1, 1, 0.5, 1, 0.5, 1},
            {1, 1, 1, 0.5, 1, 0.5, 1, 0.5},
            {1, 1, 1, 0.5, 2, 1, 1, 0.5},
            {1, 2, 1, 0.5, 1, 1, 2, 1}
    };

    /**
     * Les statistiques de base (PV, PC) de Pokémon sont générées aléatoirement
     */
    public Pokemon(String name, String[] type, EvolutionFormPokemon evolution) {
        this.type = type;
        this.name = name;
        this.evolution = evolution;
        PC = r.nextInt(200, 300);
        PV = r.nextInt(1000, 1500);
        HP = PV;
        level = 1;
    }

    /**
     * pokémon sans évolution
     */
    public Pokemon(String name, String[] type) {
        this.type = type;
        this.name = name;
        this.evolution = null;
        PC = r.nextInt(200, 300);
        PV = r.nextInt(1000, 1500);
        HP = PV;
        level = 1;
    }

    /**
     * envoie une commande au Pokémon défenseur pour qu'il subisse des dégâts
     * @return des informations sur l'attaque
     */
    public String attack(Pokemon pokemon) {
        String defend = pokemon.defend(this);
        return  this.name + " attaque " + pokemon.getName() + "\n" +
                defend;
    }

    /**
     * Pokémon subit et inflige des dégâts
     * @return informations sur les dégâts
     */
    public String defend(Pokemon pokemon) {
        int damage = calculateDamage(pokemon);
        if (this.HP - damage < 0){
            this.HP = 0;
        } else {
            this.HP -= damage;
        }
        return  this.name + " reçoit " + damage + " dégâts\n" +
                this.name + " a " + this.HP + "/" + this.PV + " points de vie\n";
    }

    /**
     * @return la resistance de type1 contre type2
     */
    private static double getResistance(String type1, String type2) {
        return resistances[Arrays.asList(types).indexOf(type2)][Arrays.asList(types).indexOf(type1)];
    }

    /**
     * @return la resistance de ce Pokemon (this) contre pokemon dans les paramètres
     */
    public double calculateResistance(Pokemon pokemon) {
        double resistance = 1;
        String[] type1 = pokemon.getType();
        String[] type2 = this.getType();
        resistance *= getResistance(type1[0], type2[0]);
        if (type1[1] != null) {
            if (type2[1] != null) {
                resistance *= getResistance(type1[0], type2[1]);
                resistance *= getResistance(type1[1], type2[0]);
                resistance *= getResistance(type1[1], type2[1]);
            } else {
                resistance *= getResistance(type1[1], type2[0]);
            }
        } else {
            if (type2[1] != null) {
                resistance *= getResistance(type1[0], type2[1]);
            }
        }
        return resistance;
    }

    /**
     * @return dégâts aléatoires (min 50%, max 150% des dégâts initiaux)
     */
    public int calculateDamage(Pokemon pokemon) {
        int pc = pokemon.getPC();
        int damage = r.nextInt((int) pc - pc / 2, (int) pc + pc / 2);
        damage *= calculateResistance(pokemon);
        damage++;
        return damage;
    }

    /**
     * Lors de la mise à niveau d'un Pokémon, si le niveau du Pokémon est un multiple de 5,
     * alors le Pokémon évoluera vers sa forme évolutive.
     * Le nom du pokémon change et les caractéristiques augmentent également de manière significative
     */
    public void evolve() {
        name = evolution.getName();
        type = evolution.getType();
        evolution = evolution.getEvolution();
        PC += (int) (PC / 10);
        PV += (int) (PV / 10);
        HP = PV;
    }

    /**
     * Pokémon monte de niveau et les statistiques augmentent
     */
    public void levelUp() {
        if (level >= MAX_LEVEL) {
            System.out.println("Vous ne pouvez plus élever le niveau de ce Pokémon");
        } else {
            level += 1;
            PC += (int) (PC / 10);
            PV += (int) (PV / 10);
            HP = PV;
            if (level % 5 == 0) {
                if (evolution != null) {
                    this.evolve();
                }
            }
        }
    }

    @Override
    public String toString() {
        if (this.type[1] != null) {
            return String.format("%s (Type: %s/%s, PC = %d, PV = %d/%d)",
                    this.name, this.type[0], this.type[1], this.PC, this.HP, this.PV);
        } else {
            return String.format("%s (Type: %s, PC = %d, PV = %d/%d)",
                    this.name, this.type[0], this.PC, this.HP, this.PV);
        }
    }

    /**
     * Sérialise un tableau de Pokémon dans un BasePokemonsArray.dat
     */
    public static void serialize(Pokemon[] pokemons) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("BasePokemonsArray.dat"))) {
            oos.writeObject(pokemons);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Désérialise un tableau de Pokémon du BasePokemonsArray.dat
     */
    public static Pokemon[] deserialize() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("BasePokemonsArray.dat"))) {
            Pokemon[] BasePokemonsArray = (Pokemon[])ois.readObject();
            return BasePokemonsArray;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    // get/set functions

    public int getPV() {
        return PV;
    }

    public int getPC() {
        return PC;
    }

    public int getHP() {
        return HP;
    }

    public int getLevel() {
        return level;
    }

    public EvolutionFormPokemon getEvolution() {
        return evolution;
    }

    public String getName() {
        return name;
    }

    public String[] getType() {
        return type;
    }

    public void setPV(int PV) {
        this.PV = (PV < 0) ? 0 : PV;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }

}
