package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.gameBoard.TileEffect;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import it.polimi.gc06.mesos.view.smallModel.TileSlotView;

import java.util.ArrayList;
import java.util.Map;

/**
 * The "kickoff" DTO. Gives the client the full initial state of the world to build the view from scratch.
 */
public class LobbyInitializedDTO implements SmallModelEditor {

    private final ArrayList<String> playersOrder;
    private final Map<String, Color> colorMap;
    private final Map<String, Integer> foodMap;
    private final ArrayList<Card> topRow;
    private final ArrayList<Card> topBuildings;
    private final ArrayList<Card> bottomRow;
    private final ArrayList<Card> bottomBuildings;
    private final String nickname;
    private final boolean isActive;
    private final ArrayList<TileEffect> tileEffects;

    private final int topDrawNum;
    private final int bottomDrawNum;
    private final int tribeDeckSize;

    private Integer sequenceNumber;

    /**
     * Stuffs everything needed for the first round into one giant container
     */
    public LobbyInitializedDTO(String nickname, ArrayList<String> playersOrder, Map<String, Color> colorMap,
                               Map<String, Integer> foodMap,
                               ArrayList<Card> topRow, ArrayList<Card> topBuildings,
                               ArrayList<Card> bottomRow, ArrayList<Card> bottomBuildings,
                               boolean isActive, ArrayList<TileEffect> tileEffects,
                               int topDrawNum, int bottomDrawNum, int tribeDeckSize) {
        this.playersOrder = playersOrder;
        this.colorMap = colorMap;
        this.foodMap = foodMap;
        this.topRow = topRow;
        this.topBuildings = topBuildings;
        this.bottomRow = bottomRow;
        this.bottomBuildings = bottomBuildings;
        this.nickname = nickname;
        this.isActive = isActive;
        this.tileEffects = tileEffects;
        this.topDrawNum = topDrawNum;
        this.bottomDrawNum = bottomDrawNum;
        this.tribeDeckSize = tribeDeckSize;

        this.sequenceNumber = null;
    }

    /**
     * {@inheritDoc}
     *
     * @param smallModel the client's small model.
     * @throws IllegalStateException if the board hasn't been set up correctly yet or blocks the setup.
     */
    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException {

        smallModel.setEra(Era.ERA_I);
        smallModel.setPhase(new PlacingTotemPhase().toString());
        smallModel.setRound(1);
        smallModel.setTribeDeckSize(tribeDeckSize);
        smallModel.setTopDrawNum(topDrawNum);
        smallModel.setBottomDrawNum(bottomDrawNum);
        smallModel.setCanSkip(false);

        smallModel.getTopRow().clear();
        smallModel.getTopRow().addAll(topRow);
        smallModel.getTopBuildings().clear();
        smallModel.getTopBuildings().addAll(topBuildings);
        smallModel.getBottomRow().clear();
        smallModel.getBottomRow().addAll(bottomRow);
        smallModel.getBottomBuildings().clear();
        smallModel.getBottomBuildings().addAll(bottomBuildings);

        tileEffects.forEach(e -> smallModel.getOfferTrack().add(new TileSlotView(e)));

        smallModel.setPlayer(nickname, colorMap.get(nickname));
        smallModel.setActive(isActive);

        PlayerView localPlayerInstance = smallModel.getPlayer();
        localPlayerInstance.setNumFood(foodMap.get(nickname));
        localPlayerInstance.setNumPrestige(0);

        for (String pName : playersOrder) {
            if (pName.equals(nickname)) {
                smallModel.getTurnOrderTile().add(localPlayerInstance);
            } else {
                PlayerView opponent = new PlayerView(pName, colorMap.get(pName));
                opponent.setNumFood(foodMap.get(pName));
                opponent.setNumPrestige(0);

                smallModel.getTurnOrderTile().add(opponent);
                smallModel.addOpponent(opponent);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param visitor
     */
    @Override
    public void accept(DTOVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void setSequenceNumber(int sNum) {
        sequenceNumber = sNum;
    }

    @Override
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }
}
