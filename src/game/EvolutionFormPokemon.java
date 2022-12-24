package game;

import java.io.Serializable;

/**
 * La forme évolutive de pokémon
 */
public class EvolutionFormPokemon extends Pokemon implements Serializable {

    public EvolutionFormPokemon(String name, String[] type, EvolutionFormPokemon evolution) {
        super(name, type, evolution);
    }

    public EvolutionFormPokemon(String name, String[] type) {
        super(name, type);
    }
}
