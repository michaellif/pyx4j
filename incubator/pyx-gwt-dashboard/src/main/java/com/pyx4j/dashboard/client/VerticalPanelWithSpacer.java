package com.pyx4j.dashboard.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * {@link VerticalPanel} which has a permanent spacer at the end to prevent CSS collapse
 * of the panel and its parent.
 */
public class VerticalPanelWithSpacer extends FlowPanel /* VerticalPanel */{
    private static final String CSS_INSERT_PANEL_SPACER = "InsertPanel-spacer";

    public VerticalPanelWithSpacer() {
        Label spacerLabel = new Label("");
        spacerLabel.setStylePrimaryName(CSS_INSERT_PANEL_SPACER);
        super.add(spacerLabel);
    }

    @Override
    public void add(Widget w) {
        super.insert(w, getWidgetCount() - 1);
    }

    @Override
    public void insert(Widget w, int beforeIndex) {
        if (beforeIndex == getWidgetCount()) {
            beforeIndex--;
        }

        super.insert(w, beforeIndex);
    }
}
