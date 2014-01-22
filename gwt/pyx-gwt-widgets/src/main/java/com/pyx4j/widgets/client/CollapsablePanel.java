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
 * Created on May 26, 2010
 * @author stanp
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.event.shared.BeforeToggleEvent;
import com.pyx4j.widgets.client.event.shared.BeforeToggleHandler;
import com.pyx4j.widgets.client.event.shared.HasToggleHandlers;
import com.pyx4j.widgets.client.event.shared.ToggleEvent;
import com.pyx4j.widgets.client.event.shared.ToggleHandler;
import com.pyx4j.widgets.client.images.WidgetsImages;

public class CollapsablePanel extends FlowPanel implements HasToggleHandlers {

    private final WidgetsImages images;

    private final SimplePanel contentHolder;

    private final Image collapseImage;

    private boolean expended = true;

    private boolean collapsible = true;

    public CollapsablePanel(WidgetsImages images) {
        this.images = images;
        getElement().getStyle().setPosition(Position.RELATIVE);

        collapseImage = new Image();
        collapseImage.setResource(images.collapse());

        collapseImage.getElement().getStyle().setPosition(Position.RELATIVE);
        collapseImage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setExpended(!expended);
            }
        });

        SimplePanel collapseImageHolder = new SimplePanel(collapseImage);
        collapseImageHolder.getElement().getStyle().setPosition(Position.ABSOLUTE);
        collapseImageHolder.getElement().getStyle().setDisplay(Display.INLINE);
        collapseImageHolder.getElement().getStyle().setTop(0, Unit.PX);
        collapseImageHolder.getElement().getStyle().setLeft(0, Unit.PX);
        collapseImageHolder.getElement().getStyle().setHeight(2, Unit.EM);

        add(collapseImageHolder);

        contentHolder = new SimplePanel();
        add(contentHolder);
    }

    public void setExpended(boolean expended) {
        BeforeToggleEvent event = BeforeToggleEvent.fire(this, expended);
        if (event != null && event.isCanceled()) {
            return;
        }
        collapseImage.setResource(expended ? images.collapse() : images.expand());
        this.expended = expended;

        ToggleEvent.fire(this, expended);
    }

    @Override
    public HandlerRegistration addBeforeToggleHandler(BeforeToggleHandler handler) {
        return addHandler(handler, BeforeToggleEvent.getType());
    }

    @Override
    public HandlerRegistration addToggleHandler(ToggleHandler handler) {
        return addHandler(handler, ToggleEvent.getType());
    }

    public void setWidget(Widget w) {
        contentHolder.setWidget(w);
    }

    public void setCollapsible(boolean collapsible) {
        this.collapsible = collapsible;
        collapseImage.setVisible(collapsible);
        if (collapsible == false) {
            setExpended(true);
        }
    }

    public boolean isCollapsible() {
        return collapsible;
    }

    public boolean isExpended() {
        return expended;
    }

}
