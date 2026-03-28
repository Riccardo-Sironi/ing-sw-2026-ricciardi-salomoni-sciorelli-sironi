package it.polimi.gc06.mesos.model.cards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.gc06.mesos.model.cards.buildings.*;
import it.polimi.gc06.mesos.model.cards.characters.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "class"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EndGameBuildingCard.class, name = "EndGameBuildingCard"),
        @JsonSubTypes.Type(value = ObserverPairBuildingCard.class, name = "ObserverPairBuildingCard"),
        @JsonSubTypes.Type(value = ObserverSetBuildingCard.class, name = "ObserverSetBuildingCard"),
        @JsonSubTypes.Type(value = ArtistCard.class, name = "ArtistCard"),
        @JsonSubTypes.Type(value = BuilderCard.class, name = "BuilderCard"),
        @JsonSubTypes.Type(value = GathererCard.class, name = "GathererCard"),
        @JsonSubTypes.Type(value = HunterCard.class, name = "HunterCard"),
        @JsonSubTypes.Type(value = InventorCard.class, name = "InventorCard"),
        @JsonSubTypes.Type(value = ShamanCard.class, name = "ShamanCard")
})

public interface Card {
}
