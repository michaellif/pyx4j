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
 * Created on Apr 24, 2014
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.panels;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;

public class FormPanel extends AbstractFormPanel implements IsWidget {

    private final DualColumnFluidPanel fluidPanel;

    public FormPanel(CForm<?> parent) {
        super(parent);
        fluidPanel = new DualColumnFluidPanel();
    }

    public CompOptions append(Location location, IObject<?> member) {
        CField<?, ?> comp = getParent().inject(member);
        return append(location, comp);
    }

    public CompOptions append(Location location, IObject<?> member, CField<?, ?> comp) {
        comp = getParent().inject(member, comp);
        return append(location, comp);
    }

    public CompOptions append(Location location, CField<?, ?> comp) {
        append(location, (IsWidget) comp);
        return new CompOptions(comp);
    }

    public void append(Location location, IObject<?> member, CComponent<?, ?, ?, ?> comp) {
        comp = getParent().inject(member, comp);
        append(location, comp);
    }

    public void append(Location location, IsWidget widget) {
        Widget handlerPanel = new SimplePanel(widget.asWidget());
        handlerPanel.setStyleName(FormPanelTheme.StyleName.FormPanelCell.name());
        switch (location) {
        case Left:
            handlerPanel.addStyleDependentName(FormPanelTheme.StyleDependent.left.name());
            break;
        case Right:
            handlerPanel.addStyleDependentName(FormPanelTheme.StyleDependent.right.name());
            break;
        case Dual:
            handlerPanel.addStyleDependentName(FormPanelTheme.StyleDependent.dual.name());
            break;
        case Absolute:
            handlerPanel.addStyleDependentName(FormPanelTheme.StyleDependent.absolute.name());
            break;
        }
        fluidPanel.append(location, handlerPanel);
    }

    @Override
    public Widget asWidget() {
        return fluidPanel.asWidget();
    }

    @Override
    protected void append(IsWidget widget) {
        append(Location.Dual, widget);
    }

    public void addStyleName(String style) {
        fluidPanel.addStyleName(style);
    }

    public void clear() {
        fluidPanel.clear();
    }

    public void setVisible(boolean visible) {
        fluidPanel.setVisible(visible);
    }

}
