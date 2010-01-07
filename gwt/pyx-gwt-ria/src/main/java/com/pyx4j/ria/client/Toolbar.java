/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 23, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.ria.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.style.CSSClass;

/**
 * @author michaellif
 * 
 */
public class Toolbar extends HorizontalPanel {

    private final HorizontalPanel buttonsPanel;

    public Toolbar() {

        buttonsPanel = new HorizontalPanel();
        buttonsPanel.setWidth("1px");

        add(buttonsPanel);
        setCellHorizontalAlignment(buttonsPanel, HasHorizontalAlignment.ALIGN_LEFT);
        //setSize("100%", "100%");
        setStyleName(CSSClass.pyx4j_Toolbar.name());
    }

    public void addItem(String caption, final Command command) {
        Button button = new Button(caption);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                command.execute();
            }
        });
        addItem(button);
    }

    public void addItem(ImageResource imageResource, final Command command, String tooltip) {
        Button button = new Button(new Image(imageResource));
        button.setTooltip(tooltip);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                command.execute();
            }
        });
        addItem(button);
    }

    public void addItem(Widget widget) {
        buttonsPanel.add(widget);
        DOM.setStyleAttribute(widget.getElement(), "padding", "3px");
        DOM.setStyleAttribute(widget.getElement(), "cursor", "pointer");
        DOM.setStyleAttribute(widget.getElement(), "cursor", "hand");
        buttonsPanel.setCellHeight(widget, "100%");
        buttonsPanel.setCellWidth(widget, "1px");
        buttonsPanel.setCellVerticalAlignment(widget, ALIGN_MIDDLE);
    }

    public void addSeparator() {
        BarSeparator separator = new BarSeparator();
        buttonsPanel.add(separator);
        buttonsPanel.setCellHeight(separator, "100%");
        buttonsPanel.setCellVerticalAlignment(separator, ALIGN_MIDDLE);
    }

}