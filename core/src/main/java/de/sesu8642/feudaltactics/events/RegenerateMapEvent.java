// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.ingame.GameParameters;

/**
 * Event: Map needs to be re-generated because the Parameters of the generated
 * map changed or the player wants to retry or starts a new game.
 */
public class RegenerateMapEvent {

    private GameParameters gameParams;

    /**
     * Constructor.
     */
    public RegenerateMapEvent(GameParameters gameParams) {
        super();
        this.gameParams = gameParams;
    }

    public GameParameters getGameParams() {
        return gameParams;
    }

    public void setGameParams(GameParameters gameParams) {
        this.gameParams = gameParams;
    }

}
