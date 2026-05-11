package it.polimi.gc06.mesos.view.tui;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;

public class TuiPlayerInfoRenderer implements TuiRenderer<PlayerView> {

    @Override
    public String[] render(PlayerView player) {
        String[] card = new String[7];

        card[0] = "┌────────────────────┐";
        // Center the player nickname, within 20 characters (22 - 2 for the borders)
        card[1] = "│" + centerText(player.getNickname(), 20) + "│";
        card[2] = "│" + centerText("\uD83C\uDF56" + player.getNumFood(), 10) + centerText("\uD83E\uDD47" + player.getNumPrestige(), 10) + "│";
        card[3] = String.format("│  ★ %-2d ⚒ -%-2d  ✋%-2d  │", player.getNumShamanStar(), player.getBuildersDiscount(), player.getCollectedIcons().size());
        card[4] = String.format("│  \uD83E\uDD57%-2d  \uD83C\uDFF9%-2d  \uD83C\uDFD7\uFE0F%-2d  │", player.getNumGatherer(), player.getNumHunter(), player.getNumBuilders());
        card[5] = String.format("│  \uD83E\uDDE9%-2d  \uD83E\uDDB4%-2d  \uD83C\uDFA8%-2d  │", player.getNumInventor(), player.getNumShaman(), player.getNumArtist());
        card[6] = "└────────────────────┘";

        return card;
    }
}
