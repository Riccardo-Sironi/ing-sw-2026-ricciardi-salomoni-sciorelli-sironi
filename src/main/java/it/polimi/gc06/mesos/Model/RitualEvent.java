package it.polimi.gc06.mesos.Model;

public class RitualEvent extends EventCard {

    private final int numPrestigeGained;
    private final int numPrestigeLost;

    public RitualEvent(Era era, int numPrestigeGained, int numPrestigeLost) {
        super(era, false);
        this.numPrestigeGained = numPrestigeGained;

        // TODO Da Riguardare. Ha senso AGGIUNGERE un numero negativo? Potrebbe portare a confusione, forse meglio togliere con una funzione dedicata
        this.numPrestigeLost = numPrestigeLost;

    }

    @Override
    public void accept(TribeCardVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void resolveEvent(Player player) {
        int maxStars = player.getEnvironment().getMaxStars();
        int minStars = player.getEnvironment().getMinStars();

        if (player.getShamanStars() == maxStars) player.addPrestigeTokens(numPrestigeGained);
        if (player.getShamanStars() == minStars) player.addPrestigeTokens(numPrestigeLost);
    }
}