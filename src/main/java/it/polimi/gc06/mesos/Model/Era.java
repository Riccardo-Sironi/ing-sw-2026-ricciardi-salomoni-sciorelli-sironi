package it.polimi.gc06.mesos.Model;

public enum Era {
    ERA_I {
        public Era nextEra() {
            return ERA_II;
        }
    },
    ERA_II {
        public Era nextEra() {
            return ERA_III;
        }
    },
    ERA_III {
        public Era nextEra() {
            // it should never happen btw
            throw new IllegalStateException("There is no next era after the third one");
        }
    };

    public abstract Era nextEra();
}
