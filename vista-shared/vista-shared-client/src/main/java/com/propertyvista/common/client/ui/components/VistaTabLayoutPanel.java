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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class VistaTabLayoutPanel extends TabLayoutPanel {

    public static final String TAB_DIASBLED_STYLE = "vista-TabLayoutPanelTabDisabled";

    private final List<IsWidget> disabledTabs = new ArrayList<IsWidget>();

    public VistaTabLayoutPanel(double barHeight, Unit barUnit) {
        super(barHeight, barUnit);

        addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                if (isTabDisabled(event.getItem())) {
                    event.cancel();
                }
            }
        });
    }

    /**
     * Implement it and pass to appropriate constructor to disable on-the-fly.
     */
    public interface DisableCriterion {
        boolean isDisable(int index);
    }

    public VistaTabLayoutPanel(double barHeight, Unit barUnit, final DisableCriterion disableCriterion) {
        super(barHeight, barUnit);

        addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                if (disableCriterion.isDisable(event.getItem())) {
                    event.cancel();
                }
            }
        });
    }

    @Override
    public void selectTab(int index, boolean fireEvents) {
        if (isTabDisabled(index)) {
            selectFirstAvailableTab(fireEvents);
        } else {
            super.selectTab(index, fireEvents);
        }
    }

    @Override
    public void selectTab(Widget child) {
        if (isTabDisabled(child)) {
            return; // ignore mouse click selection of disabled tabs
        } else {
            super.selectTab(child);
        }
    }

    private void selectFirstAvailableTab(boolean fireEvents) {
        for (int i = getWidgetCount() - 1; i >= 0; --i) { // iterate backward! - the tabs stored in reverse order!?
            if (!isTabDisabled(i)) {
                super.selectTab(i, false);
            }
        }
    }

    public boolean isTabDisabled(Widget widget) {
        return disabledTabs.contains(widget);
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
                disabledTabs.add(widget);
            }
        } else {
            disabledTabs.remove(widget);
        }
        setTabDisabledStyles(widget, disable);
    }

    /**
     * Just for convenience - in editing forms in createContent...
     * 
     * @param disable
     */
    public void setLastTabDisabled(boolean disable) {
        setTabDisabled(getWidget(getWidgetCount() - 1), disable);
    }

    private void setTabDisabledStyles(IsWidget widget, boolean disable) {
        if (disable) {
            getTabWidget(widget).asWidget().getParent().addStyleName(TAB_DIASBLED_STYLE);
        } else {
            getTabWidget(widget).asWidget().getParent().removeStyleName(TAB_DIASBLED_STYLE);
        }
    }

    // visibility:

    public boolean isTabVisible(Widget widget) {
        return getTabWidget(widget).asWidget().getParent().isVisible();
    }

    public boolean isTabVisible(int index) {
        return isTabVisible(getWidget(index).asWidget());
    }

    public void setTabVisible(Widget widget, boolean visible) {
        getTabWidget(widget).asWidget().getParent().setVisible(visible);
    }

    public void setTabVisible(int index, boolean visible) {
        setTabVisible(getWidget(index).asWidget(), visible);
    }

    //
    // remove/clear method complements:
    //
    @Override
    public void clear() {
        disabledTabs.clear();
        super.clear();
    }

    @Override
    public boolean remove(int index) {
        disabledTabs.remove(getWidget(index).asWidget());
        return super.remove(index);
    }

    @Override
    public boolean remove(Widget w) {
        disabledTabs.remove(w);
        return super.remove(w);
    }
}
