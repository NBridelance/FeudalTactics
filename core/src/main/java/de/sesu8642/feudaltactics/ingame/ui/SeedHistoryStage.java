// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.ingame.SeedHistoryDao;
import de.sesu8642.feudaltactics.ingame.SeedHistoryEntry;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ButtonFactory;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.ResizableResettableStage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.function.Consumer;

/**
 * Stage for displaying and managing seed history.
 */
@Singleton
public class SeedHistoryStage extends ResizableResettableStage {

    private final SeedHistoryDao seedHistoryDao;
    private final Skin skin;

    private Table rootTable;
    private ScrollPane scrollPane;
    private Table historyTable;
    private TextButton backButton;
    private TextButton clearButton;

    // Changed from CheckBox to SelectBox for filter options
    private SelectBox<String> filterSelectBox;
    private static final String FILTER_ALL = "All Games";
    private static final String FILTER_COMPLETED = "Completed";
    private static final String FILTER_WON = "Won Games";

    private Consumer<SeedHistoryEntry> onSeedSelected;
    private Runnable onBackPressed;

    @Inject
    public SeedHistoryStage(@MenuViewport Viewport viewport, SeedHistoryDao seedHistoryDao, Skin skin) {
        super(viewport);
        this.seedHistoryDao = seedHistoryDao;
        this.skin = skin;
        initUi();
    }

    private void initUi() {
        // Filter options using SelectBox instead of CheckBox
        filterSelectBox = new SelectBox<>(skin);
        filterSelectBox.setItems(FILTER_ALL, FILTER_COMPLETED, FILTER_WON);
        filterSelectBox.setSelected(FILTER_ALL);

        // Add filter listener
        filterSelectBox.addListener(new ExceptionLoggingChangeListener(this::refreshHistoryList));

        // History table
        historyTable = new Table();
        scrollPane = new ScrollPane(historyTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setVariableSizeKnobs(false);

        // Action buttons
        backButton = ButtonFactory.createTextButton("Back", skin);
        backButton.addListener(new ExceptionLoggingChangeListener(() -> {
            if (onBackPressed != null) {
                onBackPressed.run();
            }
        }));

        clearButton = ButtonFactory.createTextButton("Clear History", skin);
        clearButton.addListener(new ExceptionLoggingChangeListener(() -> {
            seedHistoryDao.clearHistory();
            refreshHistoryList();
        }));

        // Layout
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(20);

        // Title
        Label titleLabel = new Label("Seed History", skin, "headline");
        rootTable.add(titleLabel).center().colspan(3).pad(0, 0, 20, 0);
        rootTable.row();

        // Filter
        Table filterTable = new Table();
        filterTable.add(new Label("Filter:", skin)).padRight(10);
        filterTable.add(filterSelectBox).fillX();

        rootTable.add(filterTable).center().colspan(3).pad(0, 0, 10, 0);
        rootTable.row();

        // History list
        rootTable.add(scrollPane).fill().expand().colspan(3);
        rootTable.row();

        // Bottom buttons
        rootTable.add(backButton).fillX().padTop(10);
        rootTable.add().expandX(); // Spacer
        rootTable.add(clearButton).fillX().padTop(10);

        this.addActor(rootTable);
        refreshHistoryList();
    }

    private void refreshHistoryList() {
        historyTable.clear();

        List<SeedHistoryEntry> history;
        String selectedFilter = filterSelectBox.getSelected();

        if (FILTER_WON.equals(selectedFilter)) {
            history = seedHistoryDao.getFavoriteSeeds();
        } else if (FILTER_COMPLETED.equals(selectedFilter)) {
            history = seedHistoryDao.getCompletedGames();
        } else {
            history = seedHistoryDao.getSeedHistory();
        }

        if (history.isEmpty()) {
            Label emptyLabel = new Label("No seeds in history yet.", skin);
            historyTable.add(emptyLabel).center().pad(20);
            return;
        }

        historyTable.defaults().fillX().pad(2);

        for (SeedHistoryEntry entry : history) {
            Table entryTable = createHistoryEntryRow(entry);
            historyTable.add(entryTable).fillX();
            historyTable.row();
        }
    }

    private Table createHistoryEntryRow(SeedHistoryEntry entry) {
        Table entryTable = new Table(skin);
        entryTable.setBackground("button"); // Use button background for visual separation
        entryTable.pad(5);

        // Seed info
        Label seedLabel = new Label("Seed: " + entry.getSeed(), skin);
        Label paramsLabel = new Label(String.format("%s, %s, %s",
                entry.getMapSize().name(),
                entry.getDensity().name(),
                entry.getBotIntelligence().name()), skin);

        // Status
        String statusText = "Incomplete";
        String statusColor = "gray";
        if (entry.isCompleted()) {
            if (Boolean.TRUE.equals(entry.getWon())) {
                statusText = "Won";
                statusColor = "green";
            } else {
                statusText = "Lost";
                statusColor = "red";
            }
        }
        Label statusLabel = new Label(statusText, skin);
        // Note: Color would need to be defined in skin or set programmatically

        // Play button
        TextButton playButton = ButtonFactory.createTextButton("Play", skin);
        playButton.addListener(new ExceptionLoggingChangeListener(() -> {
            if (onSeedSelected != null) {
                onSeedSelected.accept(entry);
            }
        }));

        // Delete button
        TextButton deleteButton = ButtonFactory.createTextButton("Delete", skin);
        deleteButton.addListener(new ExceptionLoggingChangeListener(() -> {
            seedHistoryDao.removeSeedEntry(entry);
            refreshHistoryList();
        }));

        // Layout the row
        entryTable.add(seedLabel).left().expandX();
        entryTable.add(paramsLabel).center().expandX();
        entryTable.add(statusLabel).center();
        entryTable.add(playButton).padLeft(10);
        entryTable.add(deleteButton).padLeft(5);

        return entryTable;
    }

    @Override
    public void updateOnResize(int width, int height) {
        rootTable.pack();
    }

    @Override
    public void reset() {
        filterSelectBox.setSelected(FILTER_ALL);
        refreshHistoryList();
    }

    // Setters for callbacks
    public void setOnSeedSelected(Consumer<SeedHistoryEntry> onSeedSelected) {
        this.onSeedSelected = onSeedSelected;
    }

    public void setOnBackPressed(Runnable onBackPressed) {
        this.onBackPressed = onBackPressed;
    }
}