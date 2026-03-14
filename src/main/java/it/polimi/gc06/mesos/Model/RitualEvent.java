package it.polimi.gc06.mesos.Model;

public class RitualEvent extends EventCard{

    private final int numPrestigeGained;
    private final int numPrestigeLost;

    private boolean hasBounds;

    private int minStars;
    private int maxStars;

    public RitualEvent(Era era, int numPrestigeGained, int numPrestigeLost){
        super(era,false);
        this.numPrestigeGained = numPrestigeGained;
        this.numPrestigeLost = numPrestigeLost;

        this.minStars = 0;
        this.maxStars = 0;

        hasBounds = false;
    }

    private void getStarsBound(GameModel model) {
        int nCard = 0;
        for (int i = 0; i < model.getPlayers().size(); i++) {
            int starts = model.getPlayers().get(i).getShamanStars();

            if(nCard > maxStars) maxStars = starts;
            if(nCard < minStars || i == 0) minStars = starts;
        }
    }

    @Override
    public void resolveEvent(Player player) {
        if(!hasBounds) {
            hasBounds = true;
            getStarsBound(player.getGameModel());
        }

        if(player.getShamanStars() == maxStars) player.addPrestigeTokens(numPrestigeGained);
        if(player.getShamanStars() == minStars) player.addPrestigeTokens(numPrestigeLost);
    }
}