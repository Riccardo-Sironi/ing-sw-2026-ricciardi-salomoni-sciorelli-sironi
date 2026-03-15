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

        for (int i = 0; i < model.getPlayers().size(); i++) {

            int stars = model.getPlayers().get(i).getShamanStars();

            if(i == 0) {
                maxStars = stars;
                minStars = stars;
            }
            else {
                if (stars > maxStars) maxStars = stars;
                if (stars < minStars) minStars = stars;
            }
        }
    }

    @Override
    public void resolveEvent(Player player) {
        if(!hasBounds) {
            getStarsBound(player.getGameModel());
            hasBounds = true;
        }

        if(player.getShamanStars() == maxStars) player.addPrestigeTokens(numPrestigeGained);
        if(player.getShamanStars() == minStars) player.removePrestigeTokens(numPrestigeLost);
    }
}