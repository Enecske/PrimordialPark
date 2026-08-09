package net.enecske.primordial_park;

public enum TimePeriod {
    ICE_AGE("ice_age"),
    CALABRIAN_STAGE("calabrian_stage"),
    MAASTRICHTIAN_STAGE("maastrichtian_stage");

    public final String id;

    TimePeriod(String id) {
        this.id = id;
    }
}
