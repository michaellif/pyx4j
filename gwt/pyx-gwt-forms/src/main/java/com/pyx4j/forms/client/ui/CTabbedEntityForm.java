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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.FlexFormPanelTheme;
import com.pyx4j.forms.client.validators.IValidatable;
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

        addPropertyChangeHandler(new PropertyChangeHandler() {
            boolean sheduled = false;

            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.valid, PropertyName.repopulated, PropertyName.showErrorsUnconditional)) {
                    if (!sheduled) {
                        sheduled = true;
                        Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {
                                for (int i = 0; i < tabPanel.size(); i++) {
                                    Tab tab = tabPanel.getTab(i);
                                    ValidationResults validationResults = getValidationResults(tab);
                                    if (validationResults.isValid()) {
                                        tab.setTabWarning(null);
                                    } else {
                                        tab.setTabWarning(validationResults.getValidationShortMessage());
                                    }
                                }
                                sheduled = false;
                            }
                        });
                    }

                }
            }
        });
    }

    private ValidationResults getValidationResults(Widget widget) {
        ValidationResults results = new ValidationResults();
        if (widget instanceof IValidatable) {
            results.appendValidationErrors(((IValidatable) widget).getValidationResults());
        } else if (widget instanceof HasWidgets) {
            for (Widget childWidget : ((HasWidgets) widget)) {
                results.appendValidationErrors(getValidationResults(childWidget));
            }
        }
        return results;
    }

    @Override
    public IsWidget createContent() {
        return tabPanel;
    }

    public Tab addTab(final BasicFlexFormPanel panel) {
        return addTab(panel, panel.getTitle());
    }

    public Tab addTab(Widget content, String tabTitle) {
        Tab tab = null;
        SimplePanel containerPanel = new SimplePanel(content);
        containerPanel.setStyleName(CComponentTheme.StyleName.TabbedFormTab.name());
        ScrollPanel scroll = new ScrollPanel(containerPanel);
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

    public Element getPrintableElement() {
        //Traverse tabs of TabPanel
        //for enabled - create Caption + body
        //Expand all collapsed containers and folder items
        VerticalPanel printWidget = new VerticalPanel();
        printWidget.setWidth("100%");
        for (final Tab tab : tabPanel.getTabs()) {
            if (!tab.isTabEnabled()) {
                continue;
            }
            tab.setTabVisible(true);
            Label tabLabel = new Label(tab.getTabTitle());
            tabLabel.setStyleName(FlexFormPanelTheme.StyleName.FormFlexPanelCaptionLabel.name());
            printWidget.add(tabLabel);
            printWidget.add(new HTML(tab.getWidget(0).getElement().getInnerHTML()));
        }
        return printWidget.getElement();
    }

    public void setTabBarVisible(boolean visible) {
        tabPanel.setTabBarVisible(visible);
    }
}
