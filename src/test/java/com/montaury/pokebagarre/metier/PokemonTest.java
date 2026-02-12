/**
 * Fichier de gestion des tests pour le vainqueur de pokemon
 *
 *
 * Tests:
 *      - Le pokemon 1 qui a l’attaque la plus haute (vainqueur = pokemon 1)
 *      - Le pokemon 2 qui a l’attaque la plus haute (vainqueur = pokemon 2)
 *      - Le pokemon 1 et 2 ont la meme attaque mais le pokemon 1 a plus de défense (vainqueur = pokemon 1)
 *      - Le pokemon 1 et 2 ont la meme attaque mais le pokemon 2 a plus de défense (vainqueur = pokemon 2)
 *      - Le pokemon 1 et 2 ont la meme attaque mais ils ont la meme défense (vainqueur = pokemon 1)
 *
 *
 */


package com.montaury.pokebagarre.metier;

import com.montaury.pokebagarre.fixtures.ConstructeurDePokemon;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PokemonTest {
    @Test
    void p1_devrait_gagner_quand_il_a_une_meilleure_attaque() {
        // GIVEN
        Pokemon p1 = ConstructeurDePokemon.unPokemon().avecAttaque(100).construire();
        Pokemon p2 = ConstructeurDePokemon.unPokemon().avecAttaque(80).construire();

        // WHEN
        boolean resultat = p1.estVainqueurContre(p2);

        // THEN
        assertThat(resultat).isTrue();
    }

    @Test
    void p1_devrait_perdre_quand_le_pokemon_2_a_une_meilleure_attaque() {
        // GIVEN
        Pokemon p1 = ConstructeurDePokemon.unPokemon().avecAttaque(70).construire();
        Pokemon p2 = ConstructeurDePokemon.unPokemon().avecAttaque(90).construire();

        // WHEN
        boolean resultat = p1.estVainqueurContre(p2);

        // THEN
        assertThat(resultat).isFalse();
    }

    @Test
    void p1_devrait_gagner_quand_les_attaques_sont_egales_mais_p1_a_une_meilleure_defense() {
        // GIVEN
        Pokemon p1 = ConstructeurDePokemon.unPokemon().avecAttaque(100).avecDefense(60).construire();
        Pokemon p2 = ConstructeurDePokemon.unPokemon().avecAttaque(100).avecDefense(40).construire();

        // WHEN
        boolean resultat = p1.estVainqueurContre(p2);

        // THEN
        assertThat(resultat).isTrue();
    }

    @Test
    void p1_devrait_perdre_quand_les_attaques_sont_egales_mais_p2_a_une_meilleure_defense() {
        // GIVEN
        Pokemon p1 = ConstructeurDePokemon.unPokemon().avecAttaque(100).avecDefense(30).construire();
        Pokemon p2 = ConstructeurDePokemon.unPokemon().avecAttaque(100).avecDefense(50).construire();

        // WHEN
        boolean resultat = p1.estVainqueurContre(p2);

        // THEN
        assertThat(resultat).isFalse();
    }

    @Test
    void p1_devrait_gagner_quand_les_attaques_et_les_defenses_sont_identiques(){
        //GIVEN
        Pokemon p1 = ConstructeurDePokemon.unPokemon().avecAttaque(100).avecDefense(50).construire();
        Pokemon p2 = ConstructeurDePokemon.unPokemon().avecAttaque(100).avecDefense(50).construire();
        //WHEN
        boolean resultat = p1.estVainqueurContre(p2);
        //THEN
        assertThat(resultat).isTrue();
    }




}