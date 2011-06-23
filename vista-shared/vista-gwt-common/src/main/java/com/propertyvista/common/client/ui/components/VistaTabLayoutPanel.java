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

    public interface DisableCriterion {
        boolean isDisable();
    }

    private final List<IsWidget> disableTabs = new ArrayList<IsWidget>();

    public VistaTabLayoutPanel(double barHeight, Unit barUnit, final DisableCriterion disableCriterion) {
        super(barHeight, barUnit);

        addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                if (disableCriterion.isDisable()) {
                    if (disableTabs.contains(getWidget(event.getItem()).asWidget())) {
                        event.cancel();
                    }
                }
            }
        });
    }

    public void setDisableMode(boolean disable) {
        for (IsWidget w : disableTabs) {
            if (disable) {
                getTabWidget(w).asWidget().addStyleName(TAB_DIASBLED_STYLE);
            } else {
                getTabWidget(w).asWidget().removeStyleName(TAB_DIASBLED_STYLE);
            }
        }
    }

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
        disableTabs.remove(index);
        return super.remove(index);
    }

    @Override
    public boolean remove(Widget w) {
        disableTabs.remove(w);
        return super.remove(w);
    }
}
