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

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.widgets.client.util.BrowserType;

public abstract class NativeTriggerComponent<E> extends HorizontalPanel implements Focusable, HasDoubleClickHandlers, INativeFocusComponent<E> {

    private FocusWidget focusWidget;

    private Composite composite;

    private NativeTriggerButton triggerButton;

    private GroupFocusHandler focusHandlerManager;

    private boolean enabled = true;

    private boolean readOnly = false;

    public NativeTriggerComponent() {
        super();
    }

    public void construct(FocusWidget focusWidget) {
        construct(null, focusWidget, ImageFactory.getImages().triggerBlueUp(), ImageFactory.getImages().triggerBlueDown());
    }

    public void construct(Composite composite, FocusWidget focusWidget, ImageResource upImage, ImageResource downImage) {
        if (composite == null) {
            this.focusWidget = focusWidget;
            focusWidget.setWidth("100%");
            add(focusWidget);
            setCellWidth(focusWidget, "100%");
        } else {
            this.focusWidget = focusWidget;
            this.composite = composite;
            focusWidget.setWidth("100%");
            composite.setWidth("100%");
            add(composite);
            setCellWidth(composite, "100%");
        }

        focusHandlerManager = new GroupFocusHandler(this);

        focusWidget.addFocusHandler(focusHandlerManager);
        focusWidget.addBlurHandler(focusHandlerManager);

        triggerButton = new NativeTriggerButton(upImage, downImage);
        triggerButton.setWidth("1%");
        Cursor.setHand(triggerButton);
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
        DOM.setStyleAttribute(triggerButton.getElement(), "marginLeft", "1px");

        triggerButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (!isReadOnly() && isEnabled()) {
                    onTrigger(true);
                }
            }
        });

        triggerButton.addKeyDownHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                switch (event.getNativeKeyCode()) {
                case KeyCodes.KEY_TAB:
                case KeyCodes.KEY_ESCAPE:
                case KeyCodes.KEY_UP:
                    onTrigger(false);
                    break;
                case KeyCodes.KEY_DOWN:
                    onTrigger(true);
                    break;
                }

            }
        });

        this.addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                if (!isReadOnly() && isEnabled()) {
                    onTrigger(true);
                }
            }
        });

    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        //super.onEnsureDebugId(baseID);
        focusWidget.ensureDebugId(baseID);
        // Special name for selenium to fire events instead of click
        triggerButton.ensureDebugId(CompositeDebugId.debugId(baseID, CCompDebugId.trigger));
    }

    //TODO This is display/view property and should be done differently
    public void setTrigger(boolean trigger) {
        triggerButton.setVisible(trigger);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setTriggerButtonEnabled(!readOnly && enabled);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        setTriggerButtonEnabled(!readOnly && enabled);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    private void setTriggerButtonEnabled(boolean enabled) {
        triggerButton.setEnabled(enabled);
    }

    @Override
    public int getTabIndex() {
        return focusWidget.getTabIndex();
    }

    @Override
    public void setTabIndex(int index) {
        focusWidget.setTabIndex(index);
    }

    @Override
    public void setAccessKey(char key) {
        focusWidget.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        focusWidget.setFocus(focused);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        return focusHandlerManager.addHandler(FocusEvent.getType(), focusHandler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        return focusHandlerManager.addHandler(BlurEvent.getType(), blurHandler);
    }

    protected GroupFocusHandler getGroupFocusHandler() {
        return focusHandlerManager;
    }

    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return focusWidget.addKeyUpHandler(handler);
    }

    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return focusWidget.addKeyDownHandler(handler);
    }

    @Override
    public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
        return addDomHandler(handler, DoubleClickEvent.getType());
    }

    protected abstract void onTrigger(boolean show);

    public FocusWidget getWidget() {
        return focusWidget;
    }

    public Composite getComposite() {
        return composite;
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
