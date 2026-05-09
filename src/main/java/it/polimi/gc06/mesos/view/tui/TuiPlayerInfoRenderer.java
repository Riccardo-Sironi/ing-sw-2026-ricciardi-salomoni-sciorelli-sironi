package it.polimi.gc06.mesos.view.tui;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;

public class TuiPlayerInfoRenderer implements TuiRenderer<PlayerView> {

    @Override
    public String[] render(PlayerView player) {
        String[] card = new String[7];

        card[0] = "┌────────────────────┐";
        // Center the player nickname, within 20 characters (22 - 2 for the borders)
        card[1] = "│" + centerText(player.getNickname(), 20) + "│";
        card[2] = "│" + centerText("\uD83C\uDF56" + player.getNumFood(), 20) + "│";
        card[3] = "│" + centerText("★" + player.getNumShamanStar() + " " + "⚒ -" + player.getBuildersDiscount() + " " + "✋" + player.getCollectedIcons().size(), 20) + "│";
        card[4] = "│" + centerText("\uD83E\uDD57" + player.getNumGatherer() + " " + "\uD83C\uDFF9" + player.getNumHunter() + " " + "\uD83C\uDFD7\uFE0F" + player.getNumBuilders(), 20) + "│";
        card[5] = "│" + centerText("\uD83E\uDDE9" + player.getNumInventor() + " " + "\uD83E\uDDB4" + player.getNumShaman() + " " + "\uD83C\uDFA8" + player.getNumArtist(), 20) + "│";
        card[6] = "└────────────────────┘";

        return card;
    }
}
