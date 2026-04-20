package it.polimi.gc06.mesos.model.InstancesManager;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.*;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;

import java.util.ArrayList;
import java.util.EnumMap;

public class InstanceSorterCardVisitor extends CardVisitor {

    private final EnumMap<Era, ArrayList<TribeCard>> tribeCards;
    private final EnumMap<Era, ArrayList<BuildingCard>> buildingCards;
    private final ArrayList<EventCard> finalEvents;
    private final ModifierBuildingsRegistry registry;

    public InstanceSorterCardVisitor(ModifierBuildingsRegistry registry) {
        this.tribeCards = new EnumMap<Era, ArrayList<TribeCard>>(Era.class);
        ;
        this.buildingCards = new EnumMap<Era, ArrayList<BuildingCard>>(Era.class);
        for (Era era : Era.values()) {
            this.tribeCards.put(era, new ArrayList<TribeCard>());
            this.buildingCards.put(era, new ArrayList<BuildingCard>());
        }
        this.finalEvents = new ArrayList<EventCard>();
        this.registry = registry;
    }

    @Override
    public void visit(BuildingCard card) {
        buildingCards.get(card.getEra()).add(card);
    }

    @Override
    public void visit(EndGameBuildingCard building) {
        buildingCards.get(building.getEra()).add(building);
    }

    @Override
    public void visit(ModifierBuildingCard building) {
        buildingCards.get(building.getEra()).add(building);
    }

    @Override
    public void visit(ObserverSetBuildingCard building) {
        buildingCards.get(building.getEra()).add(building);
    }

    @Override
    public void visit(ObserverPairBuildingCard building) {
        buildingCards.get(building.getEra()).add(building);
    }

    @Override
    public void visit(TribeCard card) {
        tribeCards.get(card.getEra()).add(card);
    }

    @Override
    public void visit(HunterCard card) {
        tribeCards.get(card.getEra()).add(card);
    }

    @Override
    public void visit(ShamanCard card) {
        tribeCards.get(card.getEra()).add(card);
    }

    @Override
    public void visit(ArtistCard card) {
        tribeCards.get(card.getEra()).add(card);
    }

    @Override
    public void visit(BuilderCard card) {
        tribeCards.get(card.getEra()).add(card);
    }

    @Override
    public void visit(InventorCard card) {
        tribeCards.get(card.getEra()).add(card);
    }

    @Override
    public void visit(GathererCard card) {
        tribeCards.get(card.getEra()).add(card);
    }

    @Override
    public void visit(EventCard card) {
        card.setRegistry(registry);
        tribeCards.get(card.getEra()).add(card);
    }

    @Override
    public void visit(RitualEvent card) {
        card.setRegistry(registry);
        if (card.getEra().equals(Era.ERA_III)) finalEvents.add(card);
        else tribeCards.get(card.getEra()).add(card);
    }

    @Override
    public void visit(SustenanceEvent card) {
        card.setRegistry(registry);
        if (card.getEra().equals(Era.ERA_III)) finalEvents.add(card);
        else tribeCards.get(card.getEra()).add(card);
    }

    public EnumMap<Era, ArrayList<TribeCard>> getTribeCards() {
        return tribeCards;
    }

    public EnumMap<Era, ArrayList<BuildingCard>> getBuildingCards() {
        return buildingCards;
    }

    public EventCard[] getFinalEvents() {
        return finalEvents.toArray(new EventCard[2]);
    }
}
