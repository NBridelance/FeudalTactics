// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.dagger;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Binding annotation for seed history preferences store.
 **/
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface SeedHistoryPrefStore {

}