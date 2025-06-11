// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import de.sesu8642.feudaltactics.ingame.dagger.SeedHistoryPrefStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data access object for seed history.
 */
@Singleton
public class SeedHistoryDao {

    public static final String SEED_HISTORY_PREFERENCES_NAME = "seedHistoryPreferences";
    private static final String SEED_HISTORY_JSON_KEY = "seedHistoryJson";
    private static final int MAX_HISTORY_ENTRIES = 50; // Limit to avoid excessive storage

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private final Preferences prefStore;
    private final Json json = new Json(OutputType.json);
    private final JsonReader jsonReader = new JsonReader();

    @Inject
    public SeedHistoryDao(@SeedHistoryPrefStore Preferences prefStore) {
        this.prefStore = prefStore;
    }

    /**
     * Add a new seed entry to the history.
     */
    public void addSeedEntry(SeedHistoryEntry entry) {
        try {
            List<SeedHistoryEntry> history = getSeedHistory();

            // Remove duplicate if exists (same seed with same parameters)
            history.removeIf(existing ->
                    existing.getSeed() == entry.getSeed() &&
                            existing.getMapSize() == entry.getMapSize() &&
                            existing.getDensity() == entry.getDensity() &&
                            existing.getBotIntelligence() == entry.getBotIntelligence() &&
                            existing.getStartingPosition() == entry.getStartingPosition()
            );

            // Add new entry at the beginning
            history.add(0, entry);

            // Limit the size
            if (history.size() > MAX_HISTORY_ENTRIES) {
                history = history.subList(0, MAX_HISTORY_ENTRIES);
            }

            saveSeedHistory(history);
            logger.debug("Added seed entry to history: {}", entry.getSeed());

        } catch (Exception e) {
            logger.warn("Failed to add seed entry to history", e);
        }
    }

    /**
     * Update an existing seed entry (typically to mark as completed).
     */
    public void updateSeedEntry(long seed, boolean won) {
        try {
            List<SeedHistoryEntry> history = getSeedHistory();

            // Find the most recent entry with this seed
            SeedHistoryEntry entryToUpdate = history.stream()
                    .filter(entry -> entry.getSeed() == seed && !entry.isCompleted())
                    .findFirst()
                    .orElse(null);

            if (entryToUpdate != null) {
                entryToUpdate.markCompleted(won);
                saveSeedHistory(history);
                logger.debug("Updated seed entry {} as {}", seed, won ? "won" : "lost");
            }

        } catch (Exception e) {
            logger.warn("Failed to update seed entry", e);
        }
    }

    /**
     * Get the complete seed history, ordered by most recent first.
     */
    public List<SeedHistoryEntry> getSeedHistory() {
        try {
            String historyJson = prefStore.getString(SEED_HISTORY_JSON_KEY, "[]");
            JsonValue jsonValue = jsonReader.parse(historyJson);

            List<SeedHistoryEntry> history = new ArrayList<>();
            for (JsonValue entry : jsonValue) {
                try {
                    SeedHistoryEntry seedEntry = json.readValue(SeedHistoryEntry.class, entry);
                    history.add(seedEntry);
                } catch (Exception e) {
                    logger.warn("Failed to parse seed history entry, skipping", e);
                }
            }

            return history;

        } catch (Exception e) {
            logger.warn("Failed to load seed history, returning empty list", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get only completed games from history.
     */
    public List<SeedHistoryEntry> getCompletedGames() {
        return getSeedHistory().stream()
                .filter(SeedHistoryEntry::isCompleted)
                .collect(Collectors.toList());
    }

    /**
     * Get favorite seeds (completed and won games).
     */
    public List<SeedHistoryEntry> getFavoriteSeeds() {
        return getSeedHistory().stream()
                .filter(entry -> entry.isCompleted() && Boolean.TRUE.equals(entry.getWon()))
                .collect(Collectors.toList());
    }

    /**
     * Clear all history.
     */
    public void clearHistory() {
        prefStore.remove(SEED_HISTORY_JSON_KEY);
        prefStore.flush();
        logger.debug("Cleared seed history");
    }

    /**
     * Remove a specific entry from history.
     */
    public void removeSeedEntry(SeedHistoryEntry entryToRemove) {
        try {
            List<SeedHistoryEntry> history = getSeedHistory();
            history.removeIf(entry -> entry.equals(entryToRemove));
            saveSeedHistory(history);
            logger.debug("Removed seed entry: {}", entryToRemove.getSeed());

        } catch (Exception e) {
            logger.warn("Failed to remove seed entry", e);
        }
    }

    private void saveSeedHistory(List<SeedHistoryEntry> history) {
        try {
            String historyJson = json.toJson(history);
            prefStore.putString(SEED_HISTORY_JSON_KEY, historyJson);
            prefStore.flush();

        } catch (Exception e) {
            logger.error("Failed to save seed history", e);
        }
    }
}