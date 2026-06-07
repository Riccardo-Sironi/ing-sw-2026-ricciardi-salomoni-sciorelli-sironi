package it.polimi.gc06.mesos.model.cards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.gc06.mesos.model.cards.buildings.EndGameBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ObserverPairBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ObserverSetBuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.HuntEvent;
import it.polimi.gc06.mesos.model.cards.events.PaintingsEvent;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "class"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ModifierBuildingCard.class, name = "ModifierBuildingCard"),
        @JsonSubTypes.Type(value = EndGameBuildingCard.class, name = "EndGameBuildingCard"),
        @JsonSubTypes.Type(value = ObserverPairBuildingCard.class, name = "ObserverPairBuildingCard"),
        @JsonSubTypes.Type(value = ObserverSetBuildingCard.class, name = "ObserverSetBuildingCard"),
        @JsonSubTypes.Type(value = ArtistCard.class, name = "ArtistCard"),
        @JsonSubTypes.Type(value = BuilderCard.class, name = "BuilderCard"),
        @JsonSubTypes.Type(value = GathererCard.class, name = "GathererCard"),
        @JsonSubTypes.Type(value = HunterCard.class, name = "HunterCard"),
        @JsonSubTypes.Type(value = InventorCard.class, name = "InventorCard"),
        @JsonSubTypes.Type(value = ShamanCard.class, name = "ShamanCard"),
        @JsonSubTypes.Type(value = HuntEvent.class, name = "HuntEvent"),
        @JsonSubTypes.Type(value = PaintingsEvent.class, name = "PaintingsEvent"),
        @JsonSubTypes.Type(value = RitualEvent.class, name = "RitualEvent"),
        @JsonSubTypes.Type(value = SustenanceEvent.class, name = "SustenanceEvent")
})

public interface Card extends Serializable {

    void accept(CardVisitor visitor);
}
