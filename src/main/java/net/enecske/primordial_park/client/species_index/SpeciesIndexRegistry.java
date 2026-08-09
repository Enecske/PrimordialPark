package net.enecske.primordial_park.client.species_index;

import net.enecske.primordial_park.TimePeriod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpeciesIndexRegistry {
    private static final Map<String, SpeciesIndexEntry> entries = new HashMap<>();
    private static final Map<TimePeriod, ArrayList<SpeciesIndexEntry>> entriesByTimePeriod = new HashMap<>();

    public static SpeciesIndexEntry register(SpeciesIndexEntry entry) {
        if(!entries.containsKey(entry.id()))
            entries.put(entry.id(), entry);

        if (entriesByTimePeriod.isEmpty()) {
            for (TimePeriod period : TimePeriod.values()) {
                entriesByTimePeriod.put(period, new ArrayList<>());
            }
        }

        if(!entriesByTimePeriod.get(entry.timePeriod()).contains(entry))
            entriesByTimePeriod.get(entry.timePeriod()).add(entry);

        return entry;
    }

    public static Map<String, SpeciesIndexEntry> getEntries() {
        return Map.copyOf(entries);
    }

    public static Map<TimePeriod, ArrayList<SpeciesIndexEntry>> getEntriesByTimePeriod() {
        return Map.copyOf(entriesByTimePeriod);
    }
}
