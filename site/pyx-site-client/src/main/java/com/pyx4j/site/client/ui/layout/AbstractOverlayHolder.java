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
 */
package com.pyx4j.site.client.ui.layout;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.resources.SiteImages;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Toolbar;

public abstract class AbstractOverlayHolder implements IsWidget {

    private final FlowPanel mainPanel;

    private final DeckLayoutPanel tabPanel;

    private final Toolbar tabBar;

    private final Button closeButton;

    public AbstractOverlayHolder() {

        mainPanel = new FlowPanel();
        mainPanel.setStylePrimaryName(ResponsiveLayoutTheme.StyleName.ResponsiveLayoutOverlayActions.name());

        tabBar = new Toolbar();
        tabBar.addStyleName(ResponsiveLayoutTheme.StyleName.ResponsiveLayoutOverlayActionsTabbar.name());
        mainPanel.add(tabBar);

        closeButton = new Button(SiteImages.INSTANCE.closeDevConsoleButton(), new Command() {

            @Override
            public void execute() {
                setTabSelected(-1);
            }
        });
        closeButton.addStyleName(ResponsiveLayoutTheme.StyleName.ResponsiveLayoutOverlayActionsCloseButton.name());
        closeButton.setVisible(false);
        mainPanel.add(closeButton);

        tabPanel = new DeckLayoutPanel();
        tabPanel.addStyleName(ResponsiveLayoutTheme.StyleName.ResponsiveLayoutOverlayActionsTabDeck.name());
        tabPanel.setVisible(false);
        mainPanel.add(tabPanel);

    }

    @Override
    public Widget asWidget() {
        return mainPanel;
    }

    public void addTab(final IsWidget widget, String tabText) {
        Button tabLabel = new Button(tabText, new Command() {

            @Override
            public void execute() {
                int selectedTab = tabPanel.getWidgetIndex(widget);
                int visibleTab = tabPanel.isVisible() ? tabPanel.getVisibleWidgetIndex() : -1;
                setTabSelected(selectedTab == visibleTab ? -1 : selectedTab);
            }
        });
        tabLabel.addStyleName(ResponsiveLayoutTheme.StyleName.ResponsiveLayoutOverlayActionsTabItem.name());

        tabPanel.add(widget);

        tabBar.addItem(tabLabel);

        widget.asWidget().addStyleName(ResponsiveLayoutTheme.StyleName.ResponsiveLayoutOverlayActionsTabPanel.name());
    }

    public void setTabSelected(int index) {
        if (index == -1) {
            tabPanel.setVisible(false);
            closeButton.setVisible(false);
        } else {
            tabPanel.showWidget(index);
            tabPanel.setVisible(true);
            closeButton.setVisible(true);
        }

        for (int i = 0; i < tabBar.getItemCount(); i++) {
            if (i == index) {
                tabBar.getItemHolder(i).asWidget().addStyleDependentName(ResponsiveLayoutTheme.StyleDependent.selected.name());
            } else {
                tabBar.getItemHolder(i).asWidget().removeStyleDependentName(ResponsiveLayoutTheme.StyleDependent.selected.name());
            }
        }
    }

    public void setTabVisible(int index, boolean visible) {
        if (!visible && (tabPanel.getVisibleWidgetIndex() == index)) {
            setTabSelected(-1);
        }
        tabBar.getItemHolder(index).asWidget().setVisible(visible);
    }

    public void setTabLabel(int index, String tabText) {
        ((Button) tabBar.getItem(index)).setTextLabel(tabText);
    }

    public int getTabIndex(IsWidget widget) {
        return tabPanel.getWidgetIndex(widget);
    }

}
