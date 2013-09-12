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
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.form;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

public class FormDecorator<E extends IEntity, T extends CEntityForm<E>> extends FlowPanel implements IDecorator<T> {

    private T component;

    private final Toolbar headerToolbar;

    private final FlowPanel headerPanel;

    private final Label captionLabel;

    private final SimplePanel mainPanel;

    private final Toolbar footerToolbar;

    private final SimplePanel footerPanel;

    public FormDecorator() {
        captionLabel = new Label();
        captionLabel.setStyleName(FormDecoratorTheme.StyleName.FormDecoratorCaption.name());

        headerToolbar = new Toolbar();
        headerPanel = new FlowPanel();
        headerPanel.add(captionLabel);
        headerPanel.add(headerToolbar);
        headerPanel.setStyleName(FormDecoratorTheme.StyleName.FormDecoratorHeader.name());
        add(headerPanel);

        add(mainPanel = new SimplePanel());
        mainPanel.setStyleName(FormDecoratorTheme.StyleName.FormDecoratorMain.name());

        footerToolbar = new Toolbar();
        footerPanel = new SimplePanel();
        footerPanel.setStyleName(FormDecoratorTheme.StyleName.FormDecoratorFooter.name());
        footerPanel.setWidget(footerToolbar);
        add(footerPanel);
    }

    protected void addHeaderToolbarButton(Button button) {
        headerToolbar.add(button);
    }

    protected void addFooterToolbarButton(Button button) {
        footerToolbar.add(button);
    }

    @Override
    public void setComponent(T component) {
        assert this.component == null;
        this.component = component;
        setContent(component.createContent());
    }

    public T getComponent() {
        return component;
    }

    protected IsWidget getContent() {
        return mainPanel.getWidget();
    }

    protected void setContent(IsWidget widget) {
        mainPanel.clear();
        mainPanel.setWidget(widget);
    }

    public Panel getHeaderPanel() {
        return headerPanel;
    }

    public Panel getMainPanel() {
        return mainPanel;
    }

    public Panel getFooterPanel() {
        return footerPanel;
    }

    public void setCaption(String caption) {
        captionLabel.setText(caption);
    }

    public String getCaption() {
        return captionLabel.getText();
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
    }

}
