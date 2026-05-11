package it.polimi.gc06.mesos.DTOs;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class PickTopBuildingsDTO implements SmallModelEditor{

    private final String player;
    private final int cardIndex;

    public PickTopBuildingsDTO(String player, int cardIndex) {
        this.player = player;
        this.cardIndex = cardIndex;
    }

    @Override
    public void edit(SmallModel smallModel) {
        Card card = smallModel.getTopBuildings().get(cardIndex);
        if(smallModel.getPlayer().getNickname().equals(player)) {
            smallModel.getPlayer().getCharacters().add(card);
        }
        else{
            smallModel.getOpponents().stream().filter(v -> v.getNickname().equals(player)).findFirst()
                    .orElseThrow(IllegalStateException::new).getCharacters().add(card);
        }
    }
}
