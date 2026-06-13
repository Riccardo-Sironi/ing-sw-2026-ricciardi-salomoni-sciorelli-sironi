package it.polimi.gc06.mesos.view.gui.helpers;

import javafx.geometry.Point2D;

import java.util.List;

public enum TurnOrderTileInfo {
    TURN_ORDER_TILE_5_PLAYERS,
    TURN_ORDER_TILE_4_PLAYERS,
    TURN_ORDER_TILE_3_PLAYERS,
    TURN_ORDER_TILE_2_PLAYERS;
    
    public List<Point2D> getPoints() {
        return switch (this) {
            case TURN_ORDER_TILE_5_PLAYERS -> List.of(
                    new Point2D(0.535, 0.05),
                    new Point2D(0.535, 0.24),
                    new Point2D(0.535, 0.43),
                    new Point2D(0.535, 0.62),
                    new Point2D(0.535, 0.81));
            case TURN_ORDER_TILE_4_PLAYERS -> List.of(
                    new Point2D(0.53, 0.13),
                    new Point2D(0.53, 0.33),
                    new Point2D(0.53, 0.514),
                    new Point2D(0.53, 0.7)
            );
            case TURN_ORDER_TILE_3_PLAYERS -> List.of(
                    new Point2D(0.53, 0.185),
                    new Point2D(0.53, 0.378),
                    new Point2D(0.53, 0.57));
            case TURN_ORDER_TILE_2_PLAYERS -> List.of(
                    new Point2D(0.53, 0.24),
                    new Point2D(0.53, 0.42));
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
