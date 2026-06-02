package it.polimi.gc06.mesos.view.tui;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;

import static it.polimi.gc06.mesos.view.tui.Style.getAnsiFromColorName;

/**
 * A renderer responsible for transforming a player's state (infos such as the number of tokens, food, icons, characters etc.)
 * into a structured ASCII box to be rendered in the Text-based User Interface.
 */
public class TuiPlayerInfoRenderer implements TuiRenderer<PlayerView> {

    /**
     * Renders a player's information into an array of string rows forming an ASCII box.
     * It displays the nickname, food, prestige, and the counts of the player's
     * various characters, items, and discounts.
     *
     * @param player the PlayerView object capturing the state of a player
     * @return an array of strings representing the formatted player state
     */
    @Override
    public String[] render(PlayerView player) {
        String[] card = new String[7];

        String playerColor = getAnsiFromColorName(player.getColor().toString());

        card[0] = playerColor + "┌────────────────────┐" + Style.RESET;
        // Center the player nickname, within 20 characters (22 - 2 for the borders)
        card[1] = playerColor + "│" + Style.RESET + centerText(player.getNickname(), 20) + playerColor + "│" + Style.RESET;
        card[2] = playerColor + "│" + Style.RESET + centerText("\uD83C\uDF56" + player.getNumFood(), 10) + centerText("\uD83E\uDD47" + player.getNumPrestige(), 10) + playerColor + "│" + Style.RESET;
        card[3] = String.format(playerColor + "│  " + Style.RESET + "★ %-2d ⚒ -%-2d  ✋%-2d  " + playerColor + "│" + Style.RESET, player.getNumShamanStar(), player.getBuildersDiscount(), player.getCollectedIcons().size());
        card[4] = String.format(playerColor + "│  " + Style.RESET + "\uD83E\uDD57%-2d  \uD83C\uDFF9%-2d  \uD83C\uDFD7\uFE0F%-2d  " + playerColor + "│" + Style.RESET, player.getNumGatherer(), player.getNumHunter(), player.getNumBuilders());
        card[5] = String.format(playerColor + "│  " + Style.RESET + "\uD83E\uDDE9%-2d  \uD83E\uDDB4%-2d  \uD83C\uDFA8%-2d  " + playerColor + "│" + Style.RESET, player.getNumInventor(), player.getNumShaman(), player.getNumArtist());
        card[6] = playerColor + "└────────────────────┘" + Style.RESET;

        return card;
    }
}
