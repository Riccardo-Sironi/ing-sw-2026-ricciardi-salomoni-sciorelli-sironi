package it.polimi.gc06.mesos.model.gameBoard;

public interface TileEffectVisitor {

    public void visit(FoodTileEffect effect);
    public void visit(RemoveFoodTileEffect effect);
    public void visit(ChooseCardTileEffect effect);
    public void visit(TileEffect effect);

}
