// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.gamestate.Player.Type;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

import java.util.ArrayList;
import java.util.List;

/**
 * Game settings and settings for map generation. Immutable class.
 */
public class GameParameters {
    private final List<Player> players;
    private final long seed;
    private final int landMass;
    private final float density;
    private final Intelligence botIntelligence;

    /**
     * Constructor. Assumes one human player.
     *
     * @param humanPlayerIndex index of the human player
     * @param seed             map seed to use for generating the map
     * @param landMass         number of tiles to generate
     * @param density          map density to use for generation
     */
    public GameParameters(int humanPlayerIndex, long seed, int landMass, float density, Intelligence botIntelligence,
                          int numberOfBotPlayers) {
        this.botIntelligence = botIntelligence;
        this.players = new ArrayList<>();
        for (int i = 0; i < numberOfBotPlayers + 1; i++) {
            if (i == humanPlayerIndex) {
                this.players.add(new Player(i, Type.LOCAL_PLAYER));
            } else {
                this.players.add(new Player(i, Type.LOCAL_BOT));
            }
        }
        this.seed = seed;
        this.landMass = landMass;
        this.density = density;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public long getSeed() {
        return seed;
    }

    public int getLandMass() {
        return landMass;
    }

    public float getDensity() {
        return density;
    }

    public Intelligence getBotIntelligence() {
        return botIntelligence;
    }

    @Override
    public String toString() {
        return "GameParameters{" +
                "players=" + players +
                ", seed=" + seed +
                ", landMass=" + landMass +
                ", density=" + density +
                ", botIntelligence=" + botIntelligence +
                '}';
    }
}
