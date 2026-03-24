package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.*;

import java.util.ArrayList;

public class BottomRowInitVisitor implements TribeCardVisitor {
    ArrayList<TribeCard> bottomRow;
    ArrayList<TribeCard> topRow;
    int maxTopRowSize;

    // TODO : qui dobbiamo gestire il caso (REMOTO) in cui durante l'init della board vengono pescate tante carte evento
    // quante a riempire tutta la top row prima di finire l'init della bottom (assurdo)

    public BottomRowInitVisitor(ArrayList<TribeCard> bottomRow, ArrayList<TribeCard> topRow, int maxTopRowSize) {
        this.bottomRow = bottomRow;
        this.topRow = topRow;
        this.maxTopRowSize = maxTopRowSize;
    }

    @Override
    public void visit(RitualEvent ritual) throws IllegalStateException {
        if (topRow.size() >= maxTopRowSize) {
            //this happens just if we draw (4 + # players) event cards during this phase, which we hope is unlikely to happen
            throw new IllegalStateException("Top row cannot contain more than " + maxTopRowSize + " cards during bottom row initialization");
        }
        topRow.addLast(ritual);
    }

    @Override
    public void visit(SustenanceEvent sustenance) throws IllegalStateException {
        if (topRow.size() >= maxTopRowSize) {
            //this happens just if we draw (4 + # players) event cards during this phase, which we hope is unlikely to happen
            throw new IllegalStateException("Top row cannot contain more than " + maxTopRowSize + " cards during bottom row initialization");
        }
        topRow.addLast(sustenance);
    }

    @Override
    public void visit(HuntEvent hunt) throws IllegalStateException {
        if (topRow.size() >= maxTopRowSize) {
            //this happens just if we draw (4 + # players) event cards during this phase, which we hope is unlikely to happen
            throw new IllegalStateException("Top row cannot contain more than " + maxTopRowSize + " cards during bottom row initialization");
        }
        topRow.addLast(hunt);
    }

    @Override
    public void visit(PaintingsEvent paintings) throws IllegalStateException {
        if (topRow.size() >= maxTopRowSize) {
            //this happens just if we draw (4 + # players) event cards during this phase, which we hope is unlikely to happen
            throw new IllegalStateException("Top row cannot contain more than " + maxTopRowSize + " cards during bottom row initialization");
        }
        topRow.addLast(paintings);
    }

    @Override
    public void visit(HunterCard card) {
        bottomRow.addLast(card);
    }

    @Override
    public void visit(ShamanCard card) {
        bottomRow.addLast(card);
    }

    @Override
    public void visit(ArtistCard card) {
        bottomRow.addLast(card);
    }

    @Override
    public void visit(BuilderCard card) {
        bottomRow.addLast(card);
    }

    @Override
    public void visit(InventorCard card) {
        bottomRow.addLast(card);
    }

    @Override
    public void visit(GathererCard card) {
        bottomRow.addLast(card);
    }

    @Override
    public void visit(TribeCard card) {

    }
}
