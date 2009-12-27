/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
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
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.ria.client.AbstractView;
import com.pyx4j.widgets.client.GroupBoxPanel;

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
                public void onClick(ClickEvent event) {
                    runnable.run();
                }
            });
            setWidget(b);
            return b;
        }

    }

    public AbstractProvingView(String title, ImageResource imageResource) {
        super(new SimplePanel(), title, imageResource);
        SimplePanel contentPane = (SimplePanel) getContentPane();
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

    @Override
    public Widget getFooterPane() {
        return null;
    }

    @Override
    public Widget getToolbarPane() {
        return null;
    }

    @Override
    public MenuBar getMenu() {
        return null;
    }

}
