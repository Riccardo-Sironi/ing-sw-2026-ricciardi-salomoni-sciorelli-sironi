package it.polimi.gc06.mesos.model.cards.buildings;

import java.util.EnumMap;

public class ModifierBuildingsRegistry {

    private final EnumMap<ModifierBuildingRegistryKey,ModifierBuildingCard> registry;

    public ModifierBuildingsRegistry(){
        registry = new EnumMap<ModifierBuildingRegistryKey,ModifierBuildingCard>(ModifierBuildingRegistryKey.class);
    }

    public void register(ModifierBuildingCard card){
        registry.put(card.getCardKey(),card);
    }

    public ModifierBuildingCard get(ModifierBuildingRegistryKey key){
        return registry.get(key);
    }
}
