package it.polimi.gc06.mesos.view.gui.helpers;

import javafx.geometry.Point2D;

import java.util.List;

public enum TurnOrderTileInfo {
    TURN_ORDER_TILE_5_PLAYERS(5),
    TURN_ORDER_TILE_4_PLAYERS(4),
    TURN_ORDER_TILE_3_PLAYERS(3),
    TURN_ORDER_TILE_2_PLAYERS(2);

    private int nPlayers;

    TurnOrderTileInfo(int nPlayers) {
        this.nPlayers = nPlayers;
    }

    public String getImagePath() {
        return this.name().toLowerCase() + ".png";
    }

    public List<Point2D> getPoints() {
        return switch (this) {
            case TURN_ORDER_TILE_5_PLAYERS -> List.of(
                    new Point2D(0.5, 0.074),
                    new Point2D(0.5, 0.25),
                    new Point2D(0.5, 0.42),
                    new Point2D(0.5, 0.59),
                    new Point2D(0.5, 0.767));
            case TURN_ORDER_TILE_4_PLAYERS -> List.of(
                    new Point2D(0.5, 0.125),
                    new Point2D(0.5, 0.3),
                    new Point2D(0.5, 0.475),
                    new Point2D(0.5, 0.65)
            );
            case TURN_ORDER_TILE_3_PLAYERS -> List.of(
                    new Point2D(0.5, 0.175),
                    new Point2D(0.5, 0.35),
                    new Point2D(0.5, 0.52));
            case TURN_ORDER_TILE_2_PLAYERS -> List.of(
                    new Point2D(0.5, 0.22),
                    new Point2D(0.5, 0.39));
        };
    }

    public static TurnOrderTileInfo getInfo(int nPlayers) {
        return switch (nPlayers) {
            case 5 -> TurnOrderTileInfo.TURN_ORDER_TILE_5_PLAYERS;
            case 4 -> TurnOrderTileInfo.TURN_ORDER_TILE_4_PLAYERS;
            case 3 -> TurnOrderTileInfo.TURN_ORDER_TILE_3_PLAYERS;
            case 2 -> TurnOrderTileInfo.TURN_ORDER_TILE_2_PLAYERS;
            default -> null;
        };
    }
}
