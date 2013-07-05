/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Apr 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.widgets.client.tabpanel.Tab;
import com.pyx4j.widgets.client.tabpanel.TabPanel;

public class CTabbedEntityForm<E extends IEntity> extends CEntityForm<E> {

    private final TabPanel tabPanel;

    public CTabbedEntityForm(Class<E> clazz) {
        this(clazz, null);

    }

    public CTabbedEntityForm(Class<E> rootClass, IEditableComponentFactory factory) {
        super(rootClass, factory);
        tabPanel = new TabPanel();
        tabPanel.setSize("100%", "100%");

    }

    @Override
    public IsWidget createContent() {
        return tabPanel;
    }

    public Tab addTab(final FormFlexPanel panel) {
        final Tab tab = addTab(panel, panel.getTitle());
        panel.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.valid, PropertyName.repopulated, PropertyName.showErrorsUnconditional)) {
                    ValidationResults validationResults = panel.getValidationResults();
                    if (validationResults.isValid()) {
                        tab.setTabWarning(null);
                    } else {
                        tab.setTabWarning(validationResults.getValidationShortMessage());
                    }
                }
            }
        });
        return tab;
    }

    public Tab addTab(Widget content, String tabTitle) {
        Tab tab = null;
        ScrollPanel scroll = new ScrollPanel(content);
        tab = new Tab(scroll, tabTitle, null, false);
        tabPanel.addTab(tab);
        return tab;
    }

    public void setTabEnabled(Tab tab, boolean enabled) {
        tabPanel.setTabEnabled(tab, enabled);
    }

    public boolean isTabEnabled(Tab tab) {
        return tabPanel.isTabEnabled(tab);
    }

    public void selectTab(Tab tab) {
        tabPanel.selectTab(tab);
    }

    public void setTabVisible(Tab tab, boolean show) {
        tabPanel.setTabVisible(tab, show);
    }

    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    public String toStringForPrint() {
        //Traverse tabs of TabPanel
        //for enabled - create Caption + body
        //Expand all collapsed containers and folder items
        //add html+body tags
        VerticalPanel printWidget = new VerticalPanel();
        printWidget.setWidth("100%");
        for (final Tab tab : tabPanel.getTabs()) {
            if (!tab.isTabEnabled()) {
                continue;
            }
            tab.setTabVisible(true);
            printWidget.add(new Label(tab.getTabTitle()));
            printWidget.add(new HTML(tab.getWidget(0).getElement().getInnerHTML()));
        }
        StringBuilder html = new StringBuilder();
        //generate styles
        html.append("<style>" + StyleManager.getThemeString() + "</style>");
        html.append("<body>" + printWidget.toString() + "</body>");
        return html.toString();
    }

    public void setTabBarVisible(boolean visible) {
        tabPanel.setTabBarVisible(visible);
    }
}
