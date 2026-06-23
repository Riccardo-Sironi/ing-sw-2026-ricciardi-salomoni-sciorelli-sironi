package it.polimi.gc06.mesos.model.cards.buildings;

import java.util.EnumMap;

/**
 * Manages a registry that maps modifier building keys to their corresponding card instances,
 * allowing the game engine to easily locate and query active modifier rules.
 */
public class ModifierBuildingsRegistry {

    private final EnumMap<ModifierBuildingRegistryKey, ModifierBuildingCard> registry;

    /**
     * Constructs a new, empty ModifierBuildingsRegistry.
     */
    public ModifierBuildingsRegistry() {
        registry = new EnumMap<ModifierBuildingRegistryKey, ModifierBuildingCard>(ModifierBuildingRegistryKey.class);
    }

    /**
     * This method registers a ModifierBuildingCard in the registry using its internal cardKey.
     *
     * @param card The ModifierBuildingCard to be registered.
     */
    public void register(ModifierBuildingCard card) {
        registry.put(card.getCardKey(), card);
    }


    /**
     * This method retrieves a registered ModifierBuildingCard based on the provided key.
     *
     * @param key The ModifierBuildingRegistryKey to look up.
     * @return The ModifierBuildingCard associated with the key.
     */
    public ModifierBuildingCard get(ModifierBuildingRegistryKey key) {
        return registry.get(key);
    }
}