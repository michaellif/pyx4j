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
 * @version $Id$
 */
package com.pyx4j.ria.client;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.style.CSSClass;

/**
 * @author michaellif
 * 
 */
public class Toolbar extends FlowPanel {

    public Toolbar() {
        setStyleName(CSSClass.pyx4j_Toolbar.name());
        getElement().getStyle().setProperty("verticalAlign", "middle");
        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        setWidth("100%");

    }

    public void addItem(String caption, final Command command) {
        Button button = new Button(caption);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                command.execute();
            }
        });
        addItem(button, false);
    }

    public void addItem(ImageResource imageResource, String caption, final Command command) {
        addItem(imageResource, caption, command, false);
    }

    public void addItem(ImageResource imageResource, String caption, final Command command, boolean floatRight) {
        Button button = new Button(new Image(imageResource), caption);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                command.execute();
            }
        });
        addItem(button, floatRight);
    }

    public void addItem(ImageResource imageResource, final Command command, String tooltip) {
        addItem(imageResource, command, tooltip, false);
    }

    public void addItem(ImageResource imageResource, final Command command, String tooltip, boolean floatRight) {
        Button button = new Button(new Image(imageResource));
        button.setTooltip(tooltip);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                command.execute();
            }
        });
        addItem(button, floatRight);
    }

    public void addItem(Widget widget) {
        addItem(widget, false);
    }

    public void addItem(Widget widget, boolean floatRight) {
        add(widget);
        widget.getElement().getStyle().setPadding(3, Unit.PX);
        widget.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        if (floatRight) {
            widget.getElement().getStyle().setProperty("cssFloat", "right");
        }
    }

    public void addSeparator() {
        BarSeparator separator = new BarSeparator();
        add(separator);
    }

}