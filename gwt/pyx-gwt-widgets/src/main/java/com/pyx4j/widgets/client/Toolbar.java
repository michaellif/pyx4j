/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Apr 23, 2009
 * @author Michael
 */
package com.pyx4j.widgets.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.gwt.commons.concerns.ConcernStateChangeEvent;
import com.pyx4j.gwt.commons.concerns.HasSecureConcern;
import com.pyx4j.gwt.commons.concerns.HasSecureConcernedChildren;
import com.pyx4j.gwt.commons.concerns.HasWidgetConcerns;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.gwt.commons.ui.HasStyle;
import com.pyx4j.gwt.commons.ui.SimplePanel;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class Toolbar implements IsWidget, HasSecureConcern, HasSecureConcernedChildren, HasStyle {

    private final FlowPanel panel;

    private final SecureConcernsHolder secureConcernsHolder = new SecureConcernsHolder();

    public Toolbar() {
        panel = new FlowPanel();
        panel.setStyleName(WidgetsTheme.StyleName.Toolbar.name());
    }

    public void addItem(IsWidget widget) {
        insertItem(widget, panel.getWidgetCount());
    }

    public void insertItem(IsWidget widget, int beforeIndex) {
        SimplePanel itemHolder = new SimplePanel();
        itemHolder.setStyleName(WidgetsTheme.StyleName.ToolbarItem.name());

        itemHolder.setWidget(widget);
        panel.insert(itemHolder, beforeIndex);

        addWidgetSecureConcern(widget);

        if (widget instanceof HasWidgetConcerns) {
            ((HasWidgetConcerns) widget).addConcernStateChangeHandler(new ConcernStateChangeEvent.Handler() {
                @Override
                public void onSecureConcernStateChanged(ConcernStateChangeEvent event) {
                    itemHolder.setVisible(((HasWidgetConcerns) widget).isVisible());
                }
            });
        }
    }

    @Override
    public SecureConcernsHolder secureConcernsHolder() {
        return secureConcernsHolder;
    }

    public SimplePanel getItemHolder(int index) {
        return (SimplePanel) panel.getWidget(index);
    }

    public IsWidget getItem(int index) {
        return getItemHolder(index).getWidget();
    }

    public int getItemCount() {
        return panel.getWidgetCount();
    }

    public void clear() {
        panel.clear();
        clearSecureConcerns();
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    public void setStylePrimaryName(String name) {
        panel.setStylePrimaryName(name);
    }

    public void addStyleName(String name) {
        panel.addStyleName(name);
    }

    public Element getElement() {
        return panel.getElement();
    }

}