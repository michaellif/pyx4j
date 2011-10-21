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
 * Created on Oct 20, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.panels;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleName;

public class FormFlexPanel extends FlexTable {

    public static enum StyleName implements IStyleName {
        FormFlexPanel, FormFlexPanelHeader, FormFlexPanelHeaderLabel
    }

    public static enum StyleDependent implements IStyleDependent {

    }

    private double defaultLabelWidth;

    public FormFlexPanel() {
        this(12);
    }

    public FormFlexPanel(double defaultLabelWidth) {
        this.defaultLabelWidth = defaultLabelWidth;
    }

    public void setWidget(int row, int column, final CComponent<?> component, double componentWidth) {
        this.setWidget(row, column, component, defaultLabelWidth, componentWidth);
    }

    public void add(int row, int column, final CComponent<?> component, double componentWidth, String componentCaption) {
        this.setWidget(row, column, component, defaultLabelWidth, componentWidth, componentCaption);
    }

    public void setWidget(int row, int column, final CComponent<?> component, double labelWidth, double componentWidth) {
        this.setWidget(row, column, component, labelWidth, componentWidth, null);
    }

    public void setWidget(int row, int column, final CComponent<?> component, double labelWidth, double componentWidth, String componentCaption) {
        super.setWidget(row, column, createDecorator(component, labelWidth, componentWidth, componentCaption));
    }

    public WidgetDecorator createDecorator(final CComponent<?> component, double componentWidth) {
        return this.createDecorator(component, defaultLabelWidth, componentWidth);
    }

    public WidgetDecorator createDecorator(final CComponent<?> component, double labelWidth, double componentWidth) {
        return this.createDecorator(component, labelWidth, componentWidth, null);
    }

    public WidgetDecorator createDecorator(final CComponent<?> component, double labelWidth, double componentWidth, String componentCaption) {
        return new WidgetDecorator(component, labelWidth, componentWidth, componentCaption, true);
    }

    public double getDefaultLabelWidth() {
        return defaultLabelWidth;
    }

    public void setDefaultLabelWidth(double defaultLabelWidth) {
        this.defaultLabelWidth = defaultLabelWidth;
    }

    public void setHeader(int row, int span, String caption) {
        Label label = new Label(caption);
        label.setStyleName(StyleName.FormFlexPanelHeaderLabel.name());
        SimplePanel header = new SimplePanel();
        header.setWidget(label);
        header.setStyleName(StyleName.FormFlexPanelHeader.name());
        super.setWidget(row, 0, header);
    }
}
