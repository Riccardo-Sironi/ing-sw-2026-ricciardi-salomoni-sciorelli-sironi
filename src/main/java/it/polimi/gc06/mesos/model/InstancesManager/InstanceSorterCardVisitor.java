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

    /**
     * This method visits a BuildingCard and adds it to the appropriate era list.
     *
     * @param card the BuildingCard to be sorted.
     */
    @Override
    public void visit(BuildingCard card) {
        buildingCards.get(card.getEra()).add(card);
    }

    /**
     * This method visits a EndGameBuildingCard and adds it to the appropriate era list.
     *
     * @param building the EndGameBuildingCard to be sorted.
     */
    @Override
    public void visit(EndGameBuildingCard building) {
        buildingCards.get(building.getEra()).add(building);
    }

    /**
     * This method visits a ModifierBuildingCard and adds it to the appropriate era list.
     *
     * @param building the ModifierBuildingCard to be sorted.
     */
    @Override
    public void visit(ModifierBuildingCard building) {
        buildingCards.get(building.getEra()).add(building);
    }

    /**
     * This method visits a ObserverSetBuildingCard and adds it to the appropriate era list.
     *
     * @param building the ObserverSetBuildingCard to be sorted.
     */
    @Override
    public void visit(ObserverSetBuildingCard building) {
        buildingCards.get(building.getEra()).add(building);
    }

    /**
     * This method visits a ObserverPairBuildingCard and adds it to the appropriate era list.
     *
     * @param building the ObserverPairBuildingCard to be sorted.
     */
    @Override
    public void visit(ObserverPairBuildingCard building) {
        buildingCards.get(building.getEra()).add(building);
    }

    /**
     * This method visits a general TribeCard and adds it to the appropriate era list.
     *
     * @param card the TribeCard to be sorted.
     */
    @Override
    public void visit(TribeCard card) {
        tribeCards.get(card.getEra()).add(card);
    }

    /**
     * This method visits a general HunterCard and adds it to the appropriate era list.
     *
     * @param card the HunterCard to be sorted.
     */
    @Override
    public void visit(HunterCard card) {
        tribeCards.get(card.getEra()).add(card);
    }

    /**
     * This method visits a general ShamanCard and adds it to the appropriate era list.
     *
     * @param card the ShamanCard to be sorted.
     */
    @Override
    public void visit(ShamanCard card) {
        tribeCards.get(card.getEra()).add(card);
    }

    /**
     * This method visits a general ArtistCard and adds it to the appropriate era list.
     *
     * @param card the ArtistCard to be sorted.
     */
    @Override
    public void visit(ArtistCard card) {
        tribeCards.get(card.getEra()).add(card);
    }

    /**
     * This method visits a general BuilderCard and adds it to the appropriate era list.
     *
     * @param card the BuilderCard to be sorted.
     */
    @Override
    public void visit(BuilderCard card) {
        tribeCards.get(card.getEra()).add(card);
    }

    /**
     * This method visits a general InventorCard and adds it to the appropriate era list.
     *
     * @param card the InventorCard to be sorted.
     */
    @Override
    public void visit(InventorCard card) {
        tribeCards.get(card.getEra()).add(card);
    }

    /**
     * This method visits a general GathererCard and adds it to the appropriate era list.
     *
     * @param card the GathererCard to be sorted.
     */
    @Override
    public void visit(GathererCard card) {
        tribeCards.get(card.getEra()).add(card);
    }

    /**
     * Visits an EventCard, injects the modifier registry into it,
     * and adds it to the appropriate era list.
     *
     * @param card the EventCard to be sorted.
     */
    @Override
    public void visit(EventCard card) {
        card.setRegistry(registry);
        tribeCards.get(card.getEra()).add(card);
    }

    /**
     * Visits a RitualEvent, injects the modifier registry into it,
     * and adds it either to the final events list (if Era III)
     * or the regular era list.
     *
     * @param card the RitualEvent to be sorted.
     */
    @Override
    public void visit(RitualEvent card) {
        card.setRegistry(registry);
        if (card.getEra().equals(Era.ERA_III)) finalEvents.add(card);
        else tribeCards.get(card.getEra()).add(card);
    }

    /**
     * Visits a SustenanceEvent, injects the modifier registry into it,
     * and adds it either to the final events list (if Era III)
     * or the regular era list.
     *
     * @param card the SustenanceEvent to be sorted.
     */
    @Override
    public void visit(SustenanceEvent card) {
        card.setRegistry(registry);
        if (card.getEra().equals(Era.ERA_III)) finalEvents.add(card);
        else tribeCards.get(card.getEra()).add(card);
    }

    /**
     * This method retrieves the sorted map of TribeCards categorized by Era.
     *
     * @return an EnumMap containing lists of TribeCards for each Era.
     */
    public EnumMap<Era, ArrayList<TribeCard>> getTribeCards() {
        return tribeCards;
    }

    /**
     * This method retrieves the sorted map of BuildingCard categorized by Era.
     *
     * @return an EnumMap containing lists of BuildingCard for each Era.
     */
    public EnumMap<Era, ArrayList<BuildingCard>> getBuildingCards() {
        return buildingCards;
    }

    /**
     * This method retrieves the final events allocated for Era III.
     *
     * @return an array containing the final EventCards.
     */
    public EventCard[] getFinalEvents() {
        return finalEvents.toArray(new EventCard[2]);
    }
}
