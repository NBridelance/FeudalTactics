// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.Densities;

import java.util.Date;
import java.util.Objects;

/**
 * Value object: represents a seed that was played with its game parameters and metadata.
 */
public class SeedHistoryEntry {

    private long seed;
    private MapSizes mapSize;
    private Densities density;
    private Intelligence botIntelligence;
    private int startingPosition;
    private Date playedAt;
    private boolean completed; // true if the game was finished (won/lost)
    private Boolean won; // null if not completed, true if won, false if lost

    /**
     * Constructor for a new game entry.
     */
    public SeedHistoryEntry(long seed, MapSizes mapSize, Densities density,
                            Intelligence botIntelligence, int startingPosition) {
        this.seed = seed;
        this.mapSize = mapSize;
        this.density = density;
        this.botIntelligence = botIntelligence;
        this.startingPosition = startingPosition;
        this.playedAt = new Date();
        this.completed = false;
        this.won = null;
    }

    /**
     * Constructor for deserialization.
     */
    public SeedHistoryEntry() {
    }

    /**
     * Mark this entry as completed with the game result.
     */
    public void markCompleted(boolean won) {
        this.completed = true;
        this.won = won;
    }

    /**
     * Convert to NewGamePreferences for replaying.
     */
    public NewGamePreferences toNewGamePreferences() {
        return new NewGamePreferences(seed, botIntelligence, mapSize, density, startingPosition);
    }

    /**
     * Get a display string for the UI.
     */
    public String getDisplayString() {
        String status = "";
        if (completed) {
            status = won ? " ✓ Won" : " ✗ Lost";
        } else {
            status = " ⏸ Incomplete";
        }

        return String.format("Seed: %d (%s, %s)%s",
                seed,
                mapSize.name().toLowerCase(),
                density.name().toLowerCase(),
                status);
    }

    // Getters and setters
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

    public Date getPlayedAt() { return playedAt; }
    public void setPlayedAt(Date playedAt) { this.playedAt = playedAt; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Boolean getWon() { return won; }
    public void setWon(Boolean won) { this.won = won; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeedHistoryEntry that = (SeedHistoryEntry) o;
        return seed == that.seed &&
                startingPosition == that.startingPosition &&
                completed == that.completed &&
                Objects.equals(mapSize, that.mapSize) &&
                Objects.equals(density, that.density) &&
                Objects.equals(botIntelligence, that.botIntelligence) &&
                Objects.equals(playedAt, that.playedAt) &&
                Objects.equals(won, that.won);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seed, mapSize, density, botIntelligence, startingPosition,
                playedAt, completed, won);
    }

    @Override
    public String toString() {
        return String.format("SeedHistoryEntry{seed=%d, mapSize=%s, density=%s, " +
                        "botIntelligence=%s, startingPosition=%d, playedAt=%s, " +
                        "completed=%s, won=%s}",
                seed, mapSize, density, botIntelligence, startingPosition,
                playedAt, completed, won);
    }
}