/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jun 9, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.layout;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.resources.SiteImages;

public class OverlayActionsPanel implements IsWidget {

    private final TabPanel tabPanel;

    private final SimplePanel closeLabel;

    public OverlayActionsPanel() {

        tabPanel = new TabPanel();
        tabPanel.setStylePrimaryName(ResponsiveLayoutTheme.StyleName.ResponsiveLayoutOverlayActions.name());

        closeLabel = new SimplePanel(new Image(SiteImages.INSTANCE.closeDevConsoleButton()));
        closeLabel.setVisible(false);
        closeLabel.setStylePrimaryName(ResponsiveLayoutTheme.StyleName.ResponsiveLayoutOverlayActionsCloseButton.name());
        final HTML closeLabelPanel = new HTML();
        tabPanel.add(closeLabelPanel, closeLabel);

        ((Composite) tabPanel.getTabBar().getTab(0)).getElement().getParentElement().getStyle().setProperty("width", "100%");

        tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {

            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                if (event.getItem() == tabPanel.getTabBar().getSelectedTab()) {
                    event.cancel();
                    tabPanel.selectTab(tabPanel.getWidgetIndex(closeLabelPanel));
                }
            }
        });

        tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                if (event.getSelectedItem() == tabPanel.getWidgetIndex(closeLabelPanel)) {
                    closeLabel.setVisible(false);
                } else {
                    closeLabel.setVisible(true);
                }
            }
        });

    }

    @Override
    public Widget asWidget() {
        return tabPanel;
    }

    public void addTab(IsWidget widget, String tabText) {
        HTML tabLabel = new HTML(tabText);
        tabLabel.setStylePrimaryName(ResponsiveLayoutTheme.StyleName.ResponsiveLayoutOverlayActionsTab.name());
        tabPanel.insert(widget, tabLabel, tabPanel.getWidgetCount() - 1);

        widget.asWidget().addStyleName(ResponsiveLayoutTheme.StyleName.ResponsiveLayoutOverlayActionsTabPanel.name());
    }

}
