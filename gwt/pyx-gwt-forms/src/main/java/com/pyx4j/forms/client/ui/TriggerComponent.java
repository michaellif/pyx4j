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
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.gwt.commons.BrowserType;

public abstract class TriggerComponent<E> extends HorizontalPanel implements Focusable, HasDoubleClickHandlers, INativeFocusComponent<E> {

    private FocusWidget widget;

    private Composite composite;

    private NativeTriggerButton triggerButton;

    private GroupFocusHandler focusHandlerManager;

    private boolean enabled = true;

    private boolean readOnly = false;

    public TriggerComponent() {
        super();
    }

    public void construct(FocusWidget widget) {
        construct(null, widget, ImageFactory.getImages().triggerBlueUp(), ImageFactory.getImages().triggerBlueDown());
    }

    public void construct(Composite composite, FocusWidget widget, ImageResource upImage, ImageResource downImage) {
        this.widget = widget;
        if (composite == null) {
            widget.setWidth("100%");
            add(widget);
            setCellWidth(widget, "100%");
        } else {
            this.composite = composite;
            widget.setWidth("100%");
            composite.setWidth("100%");
            add(composite);
            setCellWidth(composite, "100%");
        }

        focusHandlerManager = new GroupFocusHandler(this);

        widget.addFocusHandler(focusHandlerManager);
        widget.addBlurHandler(focusHandlerManager);

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
        DOM.setStyleAttribute(triggerButton.getElement(), "marginLeft", "4px");

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
        ((Widget) widget).ensureDebugId(baseID);
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

    @Override
    public void setViewable(boolean editable) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isViewable() {
        // TODO Auto-generated method stub
        return false;
    }

    private void setTriggerButtonEnabled(boolean enabled) {
        triggerButton.setEnabled(enabled);
    }

    @Override
    public int getTabIndex() {
        return widget.getTabIndex();
    }

    @Override
    public void setTabIndex(int index) {
        widget.setTabIndex(index);
    }

    @Override
    public void setAccessKey(char key) {
        widget.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        widget.setFocus(focused);
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return widget.addKeyUpHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return widget.addKeyDownHandler(handler);
    }

    protected GroupFocusHandler getGroupFocusHandler() {
        return focusHandlerManager;
    }

    @Override
    public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
        return addDomHandler(handler, DoubleClickEvent.getType());
    }

    protected abstract void onTrigger(boolean show);

    public FocusWidget getWidget() {
        return widget;
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

    class NativeTriggerButton extends FocusPanel {

        private final Image gwtPushButton;

        private boolean enabled = true;

        public NativeTriggerButton(ImageResource upImage, ImageResource downImage) {
            super();
            gwtPushButton = new Image(upImage);
            setWidget(gwtPushButton);

            gwtPushButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (enabled) {
                        NativeTriggerButton.this.fireEvent(event);
                        event.stopPropagation();
                    }
                }
            });

        }

        @Override
        protected void onEnsureDebugId(String baseID) {
            gwtPushButton.ensureDebugId(baseID);
        }

        @Override
        public HandlerRegistration addClickHandler(ClickHandler handler) {
            return addDomHandler(handler, ClickEvent.getType());
        }

        @Override
        public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
            return addDomHandler(handler, KeyDownEvent.getType());
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

    }
}
