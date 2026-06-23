package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.HuntEvent;
import it.polimi.gc06.mesos.model.cards.events.PaintingsEvent;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;

import java.util.ArrayList;

/**
 * A visitor responsible for initializing the bottom and top rows during the game setup or era change.
 * It routes character cards to the bottom row and event cards to the top row.
 */
public class BottomRowInitVisitor extends CardVisitor {
    ArrayList<TribeCard> bottomRow;
    ArrayList<TribeCard> topRow;
    int maxTopRowSize;

    /**
     * Constructs a new BottomRowInitVisitor.
     *
     * @param bottomRow The list representing the bottom row of cards.
     * @param topRow The list representing the top row of cards.
     * @param maxTopRowSize The maximum allowed size for the top row.
     */
    public BottomRowInitVisitor(ArrayList<TribeCard> bottomRow, ArrayList<TribeCard> topRow, int maxTopRowSize) {
        this.bottomRow = bottomRow;
        this.topRow = topRow;
        this.maxTopRowSize = maxTopRowSize;
    }

    /**
     * This method visits a RitualEvent card and adds it to the top row.
     *
     * @param ritual The RitualEvent card to be visited.
     * @throws IllegalStateException If the top row exceeds its maximum allowed size during initialization.
     */
    @Override
    public void visit(RitualEvent ritual) throws IllegalStateException {
        if (topRow.size() >= maxTopRowSize) {
            //this happens just if we draw (4 + # players) event cards during this phase, which we hope is unlikely to happen
            throw new IllegalStateException("Top row cannot contain more than " + maxTopRowSize + " cards during bottom row initialization");
        }
        topRow.addLast(ritual);
    }

    /**
     * This method visits a SustenanceEvent card and adds it to the top row.
     *
     * @param sustenance The SustenanceEvent card to be visited.
     * @throws IllegalStateException If the top row exceeds its maximum allowed size during initialization.
     */
    @Override
    public void visit(SustenanceEvent sustenance) throws IllegalStateException {
        if (topRow.size() >= maxTopRowSize) {
            //this happens just if we draw (4 + # players) event cards during this phase, which we hope is unlikely to happen
            throw new IllegalStateException("Top row cannot contain more than " + maxTopRowSize + " cards during bottom row initialization");
        }
        topRow.addLast(sustenance);
    }

    /**
     * This method visits a HuntEvent card and adds it to the top row.
     *
     * @param hunt The HuntEvent card to be visited.
     * @throws IllegalStateException If the top row exceeds its maximum allowed size during initialization.
     */
    @Override
    public void visit(HuntEvent hunt) throws IllegalStateException {
        if (topRow.size() >= maxTopRowSize) {
            //this happens just if we draw (4 + # players) event cards during this phase, which we hope is unlikely to happen
            throw new IllegalStateException("Top row cannot contain more than " + maxTopRowSize + " cards during bottom row initialization");
        }
        topRow.addLast(hunt);
    }

    /**
     * This method visits a PaintingsEvent card and adds it to the top row.
     *
     * @param paintings The PaintingsEvent card to be visited.
     * @throws IllegalStateException If the top row exceeds its maximum allowed size during initialization.
     */
    @Override
    public void visit(PaintingsEvent paintings) throws IllegalStateException {
        if (topRow.size() >= maxTopRowSize) {
            //this happens just if we draw (4 + # players) event cards during this phase, which we hope is unlikely to happen
            throw new IllegalStateException("Top row cannot contain more than " + maxTopRowSize + " cards during bottom row initialization");
        }
        topRow.addLast(paintings);
    }

    /**
     * This method visits a HunterCard and adds it to the bottom row.
     *
     * @param card The HunterCard to be visited.
     */
    @Override
    public void visit(HunterCard card) {
        bottomRow.addLast(card);
    }

    /**
     * This method visits a ShamanCard and adds it to the bottom row.
     *
     * @param card The ShamanCard to be visited.
     */
    @Override
    public void visit(ShamanCard card) {
        bottomRow.addLast(card);
    }

    /**
     * This method visits an ArtistCard and adds it to the bottom row.
     *
     * @param card The ArtistCard to be visited.
     */
    @Override
    public void visit(ArtistCard card) {
        bottomRow.addLast(card);
    }

    /**
     * This method visits a BuilderCard and adds it to the bottom row.
     *
     * @param card The BuilderCard to be visited.
     */
    @Override
    public void visit(BuilderCard card) {
        bottomRow.addLast(card);
    }

    /**
     * This method visits an InventorCard and adds it to the bottom row.
     *
     * @param card The InventorCard to be visited.
     */
    @Override
    public void visit(InventorCard card) {
        bottomRow.addLast(card);
    }

    /**
     * This method visits a GathererCard and adds it to the bottom row.
     *
     * @param card The GathererCard to be visited.
     */
    @Override
    public void visit(GathererCard card) {
        bottomRow.addLast(card);
    }

}