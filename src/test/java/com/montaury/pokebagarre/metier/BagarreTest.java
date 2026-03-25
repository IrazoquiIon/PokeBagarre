/**
 * Fichier de gestion des tests pour l'orchestration de la bagarre
 *
 * Tests:
 * - si le premier pokemon est vide
 * - si le second pokemon est vide
 * - si ce sont les mêmes
 * - si le second est null
 * - si le premier est null
 * - le premier nom est mal récupéré par l'api
 * - le deuxieme nom est mal récupéré par l'api
 * - si c'est le premier pokemon qui gagne
 * - si c'est le deuxième pokemon qui gagne
 */

package com.montaury.pokebagarre.metier;

import com.montaury.pokebagarre.erreurs.ErreurMemePokemon;
import com.montaury.pokebagarre.erreurs.ErreurPokemonNonRenseigne;
import com.montaury.pokebagarre.erreurs.ErreurRecuperationPokemon;
import com.montaury.pokebagarre.webapi.PokeBuildApi;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.montaury.pokebagarre.fixtures.ConstructeurDePokemon.unPokemon;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BagarreTest {

    private PokeBuildApi fausseApi;
    private Bagarre bagarre;

    @BeforeEach
    void preparer() {
        // Initialisation de la doublure avant chaque test
        fausseApi = mock(PokeBuildApi.class);
        bagarre = new Bagarre(fausseApi);
    }

    @Test
    void devrait_echouer_si_le_premier_pokemon_est_vide() {
        // GIVEN / WHEN
        Throwable thrown = catchThrowable(() -> bagarre.demarrer("", "Pikachu"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(ErreurPokemonNonRenseigne.class)
                .hasMessage("Le premier pokemon n'est pas renseigne");
    }

    @Test
    void devrait_echouer_si_le_premier_pokemon_est_null() {
        // GIVEN / WHEN
        Throwable thrown = catchThrowable(() -> bagarre.demarrer(null, "Pikachu"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(ErreurPokemonNonRenseigne.class)
                .hasMessage("Le premier pokemon n'est pas renseigne");
    }

    @Test
    void devrait_echouer_si_le_second_pokemon_est_vide() {
        // GIVEN / WHEN
        Throwable thrown = catchThrowable(() -> bagarre.demarrer("Pikachu", ""));

        // THEN
        assertThat(thrown)
                .isInstanceOf(ErreurPokemonNonRenseigne.class)
                .hasMessage("Le second pokemon n'est pas renseigne");
    }

    @Test
    void devrait_echouer_si_le_second_pokemon_est_null() {
        // GIVEN / WHEN
        Throwable thrown = catchThrowable(() -> bagarre.demarrer("Pikachu", null));

        // THEN
        assertThat(thrown)
                .isInstanceOf(ErreurPokemonNonRenseigne.class)
                .hasMessage("Le second pokemon n'est pas renseigne");
    }

    @Test
    void devrait_echouer_si_les_deux_pokemons_sont_les_memes() {
        // GIVEN / WHEN
        Throwable thrown = catchThrowable(() -> bagarre.demarrer("Pikachu", "PIKACHU"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(ErreurMemePokemon.class)
                .hasMessage("Impossible de faire se bagarrer un pokemon avec lui-meme");
    }

    @Test
    void devrait_echouer_si_le_premier_nom_est_mal_recupere_par_l_api() {
        // GIVEN
        when(fausseApi.recupererParNom("Inconnu"))
                .thenReturn(CompletableFuture.failedFuture(new ErreurRecuperationPokemon("Inconnu")));
        when(fausseApi.recupererParNom("Pikachu"))
                .thenReturn(CompletableFuture.completedFuture(unPokemon().construire()));

        // WHEN
        CompletableFuture<Pokemon> futurVainqueur = bagarre.demarrer("Inconnu", "Pikachu");

        // THEN
        assertThat(futurVainqueur)
                .failsWithin(Duration.ofSeconds(2))
                .withThrowableOfType(ExecutionException.class)
                .havingCause()
                .isInstanceOf(ErreurRecuperationPokemon.class)
                .withMessage("Impossible de recuperer les details sur 'Inconnu'");
    }

    @Test
    void devrait_echouer_si_le_deuxieme_nom_est_mal_recupere_par_l_api() {
        // GIVEN
        when(fausseApi.recupererParNom("Pikachu"))
                .thenReturn(CompletableFuture.completedFuture(unPokemon().construire()));
        when(fausseApi.recupererParNom("Inconnu"))
                .thenReturn(CompletableFuture.failedFuture(new ErreurRecuperationPokemon("Inconnu")));

        // WHEN
        CompletableFuture<Pokemon> futurVainqueur = bagarre.demarrer("Pikachu", "Inconnu");

        // THEN
        assertThat(futurVainqueur)
                .failsWithin(Duration.ofSeconds(2))
                .withThrowableOfType(ExecutionException.class)
                .havingCause()
                .isInstanceOf(ErreurRecuperationPokemon.class)
                .withMessage("Impossible de recuperer les details sur 'Inconnu'");
    }

    @Test
    void devrait_renvoyer_le_premier_pokemon_si_il_gagne() {
        // GIVEN
        Pokemon p1 = unPokemon().avecNom("Pikachu").avecAttaque(100).construire();
        Pokemon p2 = unPokemon().avecNom("Rattata").avecAttaque(50).construire();

        when(fausseApi.recupererParNom("Pikachu")).thenReturn(CompletableFuture.completedFuture(p1));
        when(fausseApi.recupererParNom("Rattata")).thenReturn(CompletableFuture.completedFuture(p2));

        // WHEN
        CompletableFuture<Pokemon> futurVainqueur = bagarre.demarrer("Pikachu", "Rattata");

        // THEN
        assertThat(futurVainqueur)
                .succeedsWithin(Duration.ofSeconds(2))
                .satisfies(pokemon -> {
                    assertThat(pokemon.getNom()).isEqualTo("Pikachu");
                });
    }

    @Test
    void devrait_renvoyer_le_second_pokemon_si_il_gagne() {
        // GIVEN
        Pokemon p1 = unPokemon().avecNom("Pikachu").avecAttaque(50).construire();
        Pokemon p2 = unPokemon().avecNom("Mewtwo").avecAttaque(150).construire();

        when(fausseApi.recupererParNom("Pikachu")).thenReturn(CompletableFuture.completedFuture(p1));
        when(fausseApi.recupererParNom("Mewtwo")).thenReturn(CompletableFuture.completedFuture(p2));

        // WHEN
        CompletableFuture<Pokemon> futurVainqueur = bagarre.demarrer("Pikachu", "Mewtwo");

        // THEN
        assertThat(futurVainqueur)
                .succeedsWithin(Duration.ofSeconds(2))
                .satisfies(pokemon -> {
                    assertThat(pokemon.getNom()).isEqualTo("Mewtwo");
                });
    }
}