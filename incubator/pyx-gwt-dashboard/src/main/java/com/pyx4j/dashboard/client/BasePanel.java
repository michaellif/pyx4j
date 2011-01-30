package com.pyx4j.dashboard.client;

import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Class representing a single drag-and-drop example.
 */
public abstract class BasePanel extends SimplePanel {

    private static final String CSS_BASE_PANEL = "BasePanel";

    private boolean initialLoaded = false;

    public BasePanel() {
        addStyleName(CSS_BASE_PANEL);
    }

    /**
     * Called when {@link #onLoad()} is called for the first time.
     */
    protected void onInitialLoad() {
    }

    /**
     * Calls {@link #onInitialLoad()} when called for the first time.
     */
    @Override
    protected void onLoad() {
        super.onLoad();

        if (!initialLoaded) {
            onInitialLoad();
            initialLoaded = true;
        }
    }
}