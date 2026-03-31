package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;

public class TileEffectRegistryAssigner implements TileEffectVisitor{

    private final ModifierBuildingsRegistry registry;

    public TileEffectRegistryAssigner(ModifierBuildingsRegistry registry) {
        this.registry = registry;
    }

    /**
     * Permits to pass the registry to the FoodTileEffect.
     * */
    @Override
    public void visit(FoodTileEffect effect) {
        effect.setRegistry(registry);
    }

    /**
    * Does nothing.
    * */
    @Override
    public void visit(RemoveFoodTileEffect effect) {

    }

    /**
     * Does nothing.
     * */
    @Override
    public void visit(ChooseCardTileEffect effect) {

    }

    /**
     * Permits to pass the registry to the FoodTileEffect.
     * */
    @Override
    public void visit(TileEffect effect) {

    }
}
