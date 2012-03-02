/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-23
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class VistaTabLayoutPanel extends TabLayoutPanel {

    public static final String TAB_DIASBLED_STYLE = "vista-TabLayoutPanelTabDisabled";

    private boolean disableMode = false;

    private final List<IsWidget> disableTabs = new ArrayList<IsWidget>();

    public VistaTabLayoutPanel(double barHeight, Unit barUnit) {
        super(barHeight, barUnit);

        addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                if (disableMode) {
                    if (isTabDisabled(event.getItem())) {
                        event.cancel();
                    }
                }
            }
        });
    }

    /**
     * Implement it and pass to appropriate constructor to disable on-the-fly.
     */
    public interface DisableCriterion {
        boolean isDisable();
    }

    public VistaTabLayoutPanel(double barHeight, Unit barUnit, final DisableCriterion disableCriterion) {
        super(barHeight, barUnit);

        addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                if (disableCriterion.isDisable()) {
                    if (isTabDisabled(event.getItem())) {
                        event.cancel();
                    }
                }
            }
        });
    }

    public void setDisableMode(boolean disable) {
        disableMode = disable;
        for (IsWidget w : disableTabs) {
            setTabDisabledStyles(w, disable);
        }

        try {
            if (disable && isTabDisabled(getSelectedIndex())) {
                selectFirstAvailableTab(false);
            }
        } catch (IndexOutOfBoundsException e) {
            // do nothing - it's just incorrect (-1) SelectedIndex because tabs are empty!...   
        }
    }

    @Override
    public void selectTab(int index, boolean fireEvents) {
        if (disableMode && isTabDisabled(index)) {
            selectFirstAvailableTab(fireEvents);
        } else {
            super.selectTab(index, fireEvents);
        }
    }

    @Override
    public void selectTab(Widget child) {
        if (disableMode && isTabDisabled(child)) {
            return; // ignore mouse click selection of disabled tabs
        } else {
            super.selectTab(child);
        }
    }

    protected void selectFirstAvailableTab(boolean fireEvents) {
        if (disableMode) {
            for (int i = getWidgetCount() - 1; i >= 0; --i) { // iterate backward! - the tabs stored in reverse order!?
                if (!isTabDisabled(i)) {
                    super.selectTab(i, false);
                }
            }
        } else {
            super.selectTab(0, false);
        }
    }

    public boolean isTabDisabled(Widget widget) {
        return disableTabs.contains(widget);
    }

    public boolean isTabDisabled(int index) {
        return isTabDisabled(getWidget(index).asWidget());
    }

    public void setTabDisabled(int index, boolean disable) {
        setTabDisabled(getWidget(index).asWidget(), disable);
    }

    public void setTabDisabled(Widget widget, boolean disable) {
        if (disable) {
            if (!isTabDisabled(widget)) {
                disableTabs.add(widget);
            }
        } else {
            disableTabs.remove(widget);
        }
        setTabDisabledStyles(widget, disable);
    }

    private void setTabDisabledStyles(IsWidget widget, boolean disable) {
        if (disable) {
            getTabWidget(widget).asWidget().getParent().addStyleName(TAB_DIASBLED_STYLE);
        } else {
            getTabWidget(widget).asWidget().getParent().removeStyleName(TAB_DIASBLED_STYLE);
        }
    }

    //
    // add/insert/remove/clear method complements:
    //
    public void addDisable(IsWidget w) {
        disableTabs.add(w);
        super.add(w);
    }

    public void addDisable(IsWidget w, IsWidget tab) {
        disableTabs.add(w);
        super.add(w, tab);
    }

    public void addDisable(IsWidget w, String text) {
        disableTabs.add(w);
        super.add(w, text);
    }

    public void addDisable(IsWidget w, String text, boolean asHtml) {
        disableTabs.add(w);
        super.add(w, text, asHtml);
    }

    public void addDisable(Widget w) {
        disableTabs.add(w);
        super.add(w);
    }

    public void addDisable(Widget child, String text) {
        disableTabs.add(child);
        super.add(child, text);
    }

    public void addDisable(Widget child, SafeHtml html) {
        disableTabs.add(child);
        super.add(child, html);
    }

    public void addDisable(Widget child, String text, boolean asHtml) {
        disableTabs.add(child);
        super.add(child, text, asHtml);
    }

    public void addDisable(Widget child, Widget tab) {
        disableTabs.add(child);
        super.add(child, tab);
    }

    public void insertDisable(IsWidget child, int beforeIndex) {
        disableTabs.add(child);
        super.insert(child, beforeIndex);
    }

    public void insertDisable(IsWidget child, IsWidget tab, int beforeIndex) {
        disableTabs.add(child);
        super.insert(child, tab, beforeIndex);
    }

    public void insertDisable(IsWidget child, String text, boolean asHtml, int beforeIndex) {
        disableTabs.add(child);
        super.insert(child, text, asHtml, beforeIndex);
    }

    public void insertDisable(IsWidget child, String text, int beforeIndex) {
        disableTabs.add(child);
        super.insert(child, text, beforeIndex);
    }

    public void insertDisable(Widget child, int beforeIndex) {
        disableTabs.add(child);
        super.insert(child, beforeIndex);
    }

    public void insertDisable(Widget child, SafeHtml html, int beforeIndex) {
        disableTabs.add(child);
        super.insert(child, html, beforeIndex);
    }

    public void insertDisable(Widget child, String text, boolean asHtml, int beforeIndex) {
        disableTabs.add(child);
        super.insert(child, text, asHtml, beforeIndex);
    }

    public void insertDisable(Widget child, String text, int beforeIndex) {
        disableTabs.add(child);
        super.insert(child, text, beforeIndex);
    }

    public void insertDisable(Widget child, Widget tab, int beforeIndex) {
        disableTabs.add(child);
        super.insert(child, tab, beforeIndex);
    }

    @Override
    public void clear() {
        disableTabs.clear();
        super.clear();
    }

    @Override
    public boolean remove(int index) {
        disableTabs.remove(getWidget(index).asWidget());
        return super.remove(index);
    }

    @Override
    public boolean remove(Widget w) {
        disableTabs.remove(w);
        return super.remove(w);
    }
}
