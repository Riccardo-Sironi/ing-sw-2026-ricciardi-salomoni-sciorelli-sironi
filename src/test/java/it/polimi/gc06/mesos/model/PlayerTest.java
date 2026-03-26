package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerTest {

    private Player player;

    private ModifierBuildingCard mockThreeStarCard;
    private GameInfo mockGameInfo;

    @BeforeEach
    void setUp() {
        // 1. Inizializzazione dei Mock
        mockThreeStarCard = mock(ModifierBuildingCard.class);
        mockGameInfo = mock(GameInfo.class);

        // 2. Setup Player
        player = new Player("TestUser", Color.RED, mockThreeStarCard);
    }

    @Test
    void testInitialValuesSuccess() {

        assertEquals("TestUser", player.getNickname(), "Il nickname dovrebbe essere TestUser");
        assertEquals(Color.RED, player.getPlayerColor(), "Il colore dovrebbe essere RED");

        // verifica contatori partono da 0
        assertEquals(0, player.getFoodTokens(), "Il cibo iniziale deve essere 0");
        assertEquals(0, player.getPrestigeTokens(), "Il prestigio iniziale deve essere 0");
        assertEquals(0, player.getShamanStars(), "Le stelle sciamano iniziali devono essere 0");

        // Verifichiamo che i mazzi non siano nulli
        assertNotNull(player.getCharacterDeck(), "Il deck dei personaggi non dovrebbe essere null");
        assertNotNull(player.getBuildingCards(), "Il deck degli edifici non dovrebbe essere null");
    }

    @Test
    void testAddFoodTokensSuccess() {
        // Esecuzione
        assertDoesNotThrow(() -> player.addFoodTokens(5));
        player.addFoodTokens(3);

        // Verifica
        assertEquals(8, player.getFoodTokens(), "La somma dei token cibo non è corretta");
    }

}