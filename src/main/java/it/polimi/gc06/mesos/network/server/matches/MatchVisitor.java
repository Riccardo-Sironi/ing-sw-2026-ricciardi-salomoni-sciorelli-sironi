package it.polimi.gc06.mesos.network.server.matches;

/**
 * A visitor pattern base class used for traversing match objects,
 * allowing different operations on standard and restored matches.
 */
public abstract class MatchVisitor {

    /**
     * Visits a standard match.
     *
     * @param match The match to visit.
     */
    public void visit(Match match){

    }

    /**
     * Visits a restored match.
     *
     * @param match The match to visit.
     */
    public void visit(RestoredMatch match){

    }

}