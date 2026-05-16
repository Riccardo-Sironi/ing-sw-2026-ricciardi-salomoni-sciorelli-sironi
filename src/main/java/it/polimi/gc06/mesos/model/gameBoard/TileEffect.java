package it.polimi.gc06.mesos.model.gameBoard;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.gc06.mesos.model.Player;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "class"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FoodTileEffect.class, name = "FoodTileEffect"),
        @JsonSubTypes.Type(value = RemoveFoodTileEffect.class, name = "RemoveFoodTileEffect"),
        @JsonSubTypes.Type(value = ChooseCardTileEffect.class, name = "ChooseCardTileEffect")
})

public interface TileEffect extends Serializable {

    /**
     * Applies the effect to the player.
     *
     * @param player the player to which the effect will be applied.
     * @throws IllegalArgumentException {@inheritDoc}
     */
    void execute(Player player) throws IllegalArgumentException;

    public void accept(TileEffectVisitor visitor);

}
