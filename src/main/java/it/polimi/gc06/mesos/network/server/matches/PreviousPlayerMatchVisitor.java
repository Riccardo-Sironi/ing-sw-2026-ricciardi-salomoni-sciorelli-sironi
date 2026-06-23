package it.polimi.gc06.mesos.network.server.matches;

/**
 * A visitor that checks if a specific player was part of a previously restored match,
 * effectively verifying if they are allowed to rejoin.
 */
public class PreviousPlayerMatchVisitor extends MatchVisitor {

    private boolean canEnter;
    private final String nickname;

    /**
     * Constructs a visitor for a given player.
     *
     * @param nickname The player's nickname.
     */
    public PreviousPlayerMatchVisitor(String nickname){
        canEnter = true;
        this.nickname = nickname;
    }

    /**
     * Visits a standard match, where entry is allowed by default.
     *
     * @param match The standard match.
     */
    @Override
    public void visit(Match match){
        canEnter = true;
    }

    /**
     * Visits a restored match, validating if the player was present in the original game.
     *
     * @param match The restored match to check.
     */
    @Override
    public void visit(RestoredMatch match){
        canEnter = match.getPreviousPlayers().containsKey(nickname);
    }

    /**
     * Retrieves the permission result.
     *
     * @return True if the player can enter, false otherwise.
     */
    public boolean canEnter() {
        return canEnter;
    }
}