package it.polimi.gc06.mesos.model.cards.buildings;

import java.util.EnumMap;

public class ModifierBuildingsRegistry {

    private final EnumMap<ModifierBuildingRegistryKey, ModifierBuildingCard> registry;

    public ModifierBuildingsRegistry() {
        registry = new EnumMap<ModifierBuildingRegistryKey, ModifierBuildingCard>(ModifierBuildingRegistryKey.class);
    }

    /**
     * this method registers a ModifierBuildingCard in the registry using its internal cardKey.
     *
     * @param card the ModifierBuildingCard to be registered.
     */
    public void register(ModifierBuildingCard card) {
        registry.put(card.getCardKey(), card);
    }


    /**
     * this method retrieves a registered ModifierBuildingCard based on the provided key.
     *
     * @param key the ModifierBuildingRegistryKey to look up.
     * @return the ModifierBuildingCard associated with the key
     */
    public ModifierBuildingCard get(ModifierBuildingRegistryKey key) {
        return registry.get(key);
    }
}
