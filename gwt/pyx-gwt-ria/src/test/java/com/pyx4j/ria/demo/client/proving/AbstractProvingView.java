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
 * Created on May 16, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.ria.demo.client.proving;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.ria.client.view.AbstractView;
import com.pyx4j.widgets.client.GroupBoxPanel;
import com.pyx4j.widgets.client.tabpanel.Tab;

public abstract class AbstractProvingView extends AbstractView {

    protected final VerticalPanel mainPanel;

    private final Label descriptionText;

    protected class ActionGroup extends GroupBoxPanel {

        private FlexTable table;

        private int row = 0;

        private int column = 0;

        public ActionGroup(boolean collapsible) {
            super(collapsible);
            super.setContainer(table = new FlexTable());
        }

        public void setWidget(Widget widget) {
            table.setWidget(row, column, widget);
            column++;
        }

        public void nextRow() {
            row++;
            column = 0;
        }

        public Button addAction(String name, final Runnable runnable) {
            Button b = new Button(name, new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    runnable.run();
                }
            });
            setWidget(b);
            return b;
        }

    }

    public AbstractProvingView(String tabTitle, ImageResource tabImage) {
        super(tabImage, false);
        ScrollPanel contentPane = new ScrollPanel();
        addPage(new Tab(contentPane, null, null, true));

        setTabTitle(tabTitle);

        this.mainPanel = new VerticalPanel();
        contentPane.add(this.mainPanel);
        this.mainPanel.getElement().getStyle().setProperty("marginLeft", "10px");
        mainPanel.add(descriptionText = new Label());
    }

    protected ActionGroup createActionGroup(String name) {
        return createActionGroup(name, false);
    }

    protected ActionGroup createActionGroup(String name, boolean collapsible) {
        ActionGroup ag = new ActionGroup(collapsible);
        ag.setCaption(name);
        mainPanel.add(ag);
        return ag;
    }

    protected ActionGroup createWidgetTestActionGroup(String name, final FocusWidget widget) {
        ActionGroup g = createActionGroup(name, true);

        g.setWidget(widget);
        g.nextRow();

        final CheckBox c = new CheckBox("disable");
        g.setWidget(c);
        c.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                widget.setEnabled(!c.getValue());
            }
        });
        return g;
    }

    public void setDescription(String text) {
        descriptionText.setText(text);
    }

}
