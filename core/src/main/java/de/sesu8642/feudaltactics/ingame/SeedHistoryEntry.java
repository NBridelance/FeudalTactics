// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.ingame.ui.EnumDisplayNameConverter;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

import java.util.Objects;

/**
 * Entry representing a played seed with its parameters and result.
 */
public class SeedHistoryEntry {
    private long seed;
    private MapSizes mapSize;
    private Densities density;
    private Intelligence botIntelligence;
    private int startingPosition;
    private GameResult result;

    public SeedHistoryEntry() {
        // Default constructor for JSON deserialization
    }

    public SeedHistoryEntry(long seed, MapSizes mapSize, Densities density, Intelligence botIntelligence, int startingPosition) {
        this.seed = seed;
        this.mapSize = mapSize;
        this.density = density;
        this.botIntelligence = botIntelligence;
        this.startingPosition = startingPosition;
        this.result = GameResult.INCOMPLETE;
    }

    // Getters and Setters
    public long getSeed() { return seed; }
    public void setSeed(long seed) { this.seed = seed; }

    public MapSizes getMapSize() { return mapSize; }
    public void setMapSize(MapSizes mapSize) { this.mapSize = mapSize; }

    public Densities getDensity() { return density; }
    public void setDensity(Densities density) { this.density = density; }

    public Intelligence getBotIntelligence() { return botIntelligence; }
    public void setBotIntelligence(Intelligence botIntelligence) { this.botIntelligence = botIntelligence; }

    public int getStartingPosition() { return startingPosition; }
    public void setStartingPosition(int startingPosition) { this.startingPosition = startingPosition; }

    public GameResult getResult() { return result; }
    public void setResult(GameResult result) { this.result = result; }

    @Override
    public String toString() {
        String resultText;
        switch (result) {
            case WON:
                resultText = "Victory";
                break;
            case LOST:
                resultText = "Defeat";
                break;
            case INCOMPLETE:
            default:
                resultText = "Incomplete";
                break;
        }

        return String.format("Seed %d - %s, %s, %s - %s",
                seed,
                EnumDisplayNameConverter.getDisplayName(mapSize),
                EnumDisplayNameConverter.getDisplayName(density),
                EnumDisplayNameConverter.getDisplayName(botIntelligence),
                resultText
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeedHistoryEntry that = (SeedHistoryEntry) o;
        return seed == that.seed &&
                startingPosition == that.startingPosition &&
                mapSize == that.mapSize &&
                density == that.density &&
                botIntelligence == that.botIntelligence &&
                result == that.result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(seed, mapSize, density, botIntelligence, startingPosition, result);
    }

    public enum GameResult {
        INCOMPLETE, WON, LOST
    }
}