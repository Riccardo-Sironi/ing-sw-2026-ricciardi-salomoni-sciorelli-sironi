package it.polimi.gc06.mesos.view;

import java.util.ArrayList;

public record SmallModel (ArrayList<String> topRow, ArrayList<String> bottomRow, ArrayList<String> topBuilding,
                          ArrayList<String> bottomBuilding, ArrayList<String> turnOrderTile, String turnOrderTileURL,
                          ArrayList<String> offerTrack, ArrayList<String> offerTrackURLs, int tribeDeckSize,
                          int round, boolean isActive, boolean isEndgame, PlayerView player,
                          ArrayList<PlayerView> opponents){

    record PlayerView (String nickname, String totemURL, int numFood, int numPrestige, ArrayList<String> characters,
                       ArrayList<String> buildings, /*recap values*/
                       int numShamanStar, int numGatherer, int numHunter, int numArtist, int buildersDiscount,
                       int buildersPrestige, ArrayList<String> collectedIcons, int buildingsPrestige){}
}
