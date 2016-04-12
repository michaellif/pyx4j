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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.gwt.commons.concerns.HasSecureConcern;
import com.pyx4j.security.shared.AccessControlContext;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class Toolbar implements IsWidget, HasSecureConcern {

    private final FlowPanel panel;

    private final SecureConcernsHolder secureConcerns = new SecureConcernsHolder();

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

        secureConcerns.add(widget);
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
        secureConcerns.clear();
    }

    @Override
    public void setSecurityContext(AccessControlContext context) {
        secureConcerns.setSecurityContext(context);
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