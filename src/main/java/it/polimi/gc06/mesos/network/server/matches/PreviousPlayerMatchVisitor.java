package it.polimi.gc06.mesos.network.server.matches;

public class PreviousPlayerMatchVisitor extends MatchVisitor{

    private boolean canEnter;
    private final String nickname;

    public PreviousPlayerMatchVisitor(String nickname){
        canEnter = true;
        this.nickname = nickname;
    }

    @Override
    public void visit(Match match){
        canEnter = true;
    }

    @Override
    public void visit(RestoredMatch match){
        canEnter = match.getPreviousPlayers().containsKey(nickname);
    }

    public boolean canEnter() {
        return canEnter;
    }
}
