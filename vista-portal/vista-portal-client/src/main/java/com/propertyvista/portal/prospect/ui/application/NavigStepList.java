/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.propertyvista.portal.prospect.ui.application.NavigStepItem.StepStatus;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class NavigStepList implements IsWidget {

    private final ContentPanel contentPanel;

    private final List<NavigStepItem> items;

    private ApplicationWizard applicationWizard;

    public NavigStepList() {
        contentPanel = new ContentPanel();
        items = new ArrayList<NavigStepItem>();
    }

    @Override
    public Widget asWidget() {
        return contentPanel;
    }

    public void addStepItem(String stepTitle, final int stepIndex, StepStatus stepStatus) {
        NavigStepItem menuItem = new NavigStepItem(new Command() {
            @Override
            public void execute() {
                applicationWizard.selectStep(stepIndex);
            }
        }, stepTitle, stepIndex, stepStatus);
        items.add(menuItem);
        contentPanel.addNavigItem(menuItem);
    }

    public List<NavigStepItem> getStepItems() {
        return items;
    }

    public NavigStepItem getSelectedMenuItem() {
        if (items == null)
            return null;
        for (NavigStepItem item : items) {
            if (item.isSelected()) {
                return item;
            }
        }
        return null;
    }

    public void reset(ApplicationWizard applicationWizard) {
        contentPanel.clear();
        this.applicationWizard = applicationWizard;
    }

    private class ContentPanel extends ComplexPanel {
        public ContentPanel() {
            setElement(DOM.createElement("ul"));
            setStyleName(PortalRootPaneTheme.StyleName.MainMenuHolder.name());
            setVisible(true);
        }

        public void addNavigItem(NavigStepItem menuItem) {
            add(menuItem.asWidget(), getElement());
        }
    }

}