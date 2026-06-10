package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.HuntEvent;
import it.polimi.gc06.mesos.model.cards.events.PaintingsEvent;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;

import java.util.ArrayList;

public class BottomRowInitVisitor extends CardVisitor {
    ArrayList<TribeCard> bottomRow;
    ArrayList<TribeCard> topRow;
    int maxTopRowSize;

    public BottomRowInitVisitor(ArrayList<TribeCard> bottomRow, ArrayList<TribeCard> topRow, int maxTopRowSize) {
        this.bottomRow = bottomRow;
        this.topRow = topRow;
        this.maxTopRowSize = maxTopRowSize;
    }

    /**
     * This method visits a RitualEvent card and adds it to the top row.
     *
     * @param ritual the RitualEvent card to be visited.
     * @throws IllegalStateException if the top row exceeds its maximum allowed size during initialization.
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
     * @param sustenance the SustenanceEvent card to be visited.
     * @throws IllegalStateException if the top row exceeds its maximum allowed size during initialization.
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
     * @param hunt the HuntEvent card to be visited.
     * @throws IllegalStateException if the top row exceeds its maximum allowed size during initialization.
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
     * @param paintings the PaintingsEvent card to be visited.
     * @throws IllegalStateException if the top row exceeds its maximum allowed size during initialization.
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
     * @param card the HunterCard to be visited.
     */
    @Override
    public void visit(HunterCard card) {
        bottomRow.addLast(card);
    }

    /**
     * This method visits a ShamanCard and adds it to the bottom row.
     *
     * @param card the ShamanCard to be visited.
     */
    @Override
    public void visit(ShamanCard card) {
        bottomRow.addLast(card);
    }

    /**
     * This method visits a ArtistCard and adds it to the bottom row.
     *
     * @param card the ArtistCard to be visited.
     */
    @Override
    public void visit(ArtistCard card) {
        bottomRow.addLast(card);
    }

    /**
     * This method visits a BuilderCard and adds it to the bottom row.
     *
     * @param card the BuilderCard to be visited.
     */
    @Override
    public void visit(BuilderCard card) {
        bottomRow.addLast(card);
    }

    /**
     * This method visits a InventorCard and adds it to the bottom row.
     *
     * @param card the InventorCard to be visited.
     */
    @Override
    public void visit(InventorCard card) {
        bottomRow.addLast(card);
    }

    /**
     * This method visits a GathererCard and adds it to the bottom row.
     *
     * @param card the GathererCard to be visited.
     */
    @Override
    public void visit(GathererCard card) {
        bottomRow.addLast(card);
    }

}
