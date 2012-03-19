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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.GroupFocusHandler;

public class TriggerPanel extends HorizontalPanel implements HasDoubleClickHandlers {

    private final NComponent<?, ?, ?> component;

    private final Button triggerButton;

    private final GroupFocusHandler focusHandlerManager;

    private boolean toggledOn = false;

    public TriggerPanel(final NComponent<?, ?, ?> component, ImageResource triggerImage) {
        super();

        setStyleName(DefaultCCOmponentsTheme.StyleName.TriggerPannel.name());

        this.component = component;
        component.getEditor().asWidget().setWidth("100%");
        add(component.getEditor());
        setCellWidth(component.getEditor(), "100%");

        focusHandlerManager = new GroupFocusHandler(this);

        if (component.getEditor() instanceof NFocusComponent) {
            ((NFocusComponent<?, ?, ?>) component.getEditor()).addFocusHandler(focusHandlerManager);
            ((NFocusComponent<?, ?, ?>) component.getEditor()).addBlurHandler(focusHandlerManager);
        }

        triggerButton = new Button(new Image(triggerImage));
        triggerButton.addFocusHandler(focusHandlerManager);
        triggerButton.addBlurHandler(focusHandlerManager);

        add(triggerButton);
        setCellVerticalAlignment(triggerButton, ALIGN_TOP);

        String marginTop;
        if (BrowserType.isFirefox()) {
            marginTop = "0";
        } else if (BrowserType.isIE()) {
            marginTop = "1";
        } else {
            //Chrome and Safari
            marginTop = "2";
        }
        DOM.setStyleAttribute(triggerButton.getElement(), "marginTop", marginTop);
        DOM.setStyleAttribute(triggerButton.getElement(), "marginLeft", "4px");

        triggerButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                toggleOn(!toggledOn);
            }
        });

        triggerButton.addKeyDownHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                switch (event.getNativeKeyCode()) {
                case KeyCodes.KEY_TAB:
                case KeyCodes.KEY_ESCAPE:
                case KeyCodes.KEY_UP:
                    toggleOn(false);
                    break;
                case KeyCodes.KEY_DOWN:
                    toggleOn(true);
                    break;
                }

            }
        });

        this.addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                toggleOn(true);
            }
        });
    }

    protected void toggleOn(boolean flag) {
        toggledOn = flag;
        component.onToggle();
    }

    protected boolean isToggledOn() {
        return toggledOn;
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        //super.onEnsureDebugId(baseID);
        ((Widget) component.getEditor()).ensureDebugId(baseID);
        // Special name for selenium to fire events instead of click
        triggerButton.ensureDebugId(CompositeDebugId.debugId(baseID, CCompDebugId.trigger));
    }

    public Button getTriggerButton() {
        return triggerButton;
    }

    protected GroupFocusHandler getGroupFocusHandler() {
        return focusHandlerManager;
    }

    @Override
    public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
        return addDomHandler(handler, DoubleClickEvent.getType());
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        DomDebug.attachedWidget();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        DomDebug.detachWidget();
    }

}
