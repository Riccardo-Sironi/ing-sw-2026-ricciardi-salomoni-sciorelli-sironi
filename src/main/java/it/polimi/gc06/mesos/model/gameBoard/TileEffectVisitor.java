package it.polimi.gc06.mesos.model.gameBoard;

public interface TileEffectVisitor {

    void visit(FoodTileEffect effect);

    void visit(RemoveFoodTileEffect effect);

    void visit(ChooseCardTileEffect effect);

    void visit(TileEffect effect);

}
