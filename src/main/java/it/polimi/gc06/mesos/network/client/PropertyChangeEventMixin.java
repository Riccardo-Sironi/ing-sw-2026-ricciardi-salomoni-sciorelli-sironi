package it.polimi.gc06.mesos.network.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class PropertyChangeEventMixin {
    PropertyChangeEventMixin(
            @JsonProperty("source") Object source,
            @JsonProperty("propertyName") String propertyName,
            @JsonProperty("oldValue") Object oldValue,
            @JsonProperty("newValue") Object newValue) {}
}