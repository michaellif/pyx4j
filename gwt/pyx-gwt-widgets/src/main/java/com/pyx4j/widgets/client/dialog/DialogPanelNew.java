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
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.dialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.style.CSSClass;

public class DialogPanelNew extends PopupPanel implements ProvidesResize, MouseMoveHandler, MouseUpHandler, MouseDownHandler {

    private static final Logger log = LoggerFactory.getLogger(DialogPanelNew.class);

    private static final int DRAG_ZONE_WIDTH = 5;

    enum ResizeZoneType {

        NONE("default"),

        RESIZE_E("e-resize"),

        RESIZE_W("w-resize"),

        RESIZE_S("s-resize"),

        RESIZE_N("n-resize"),

        RESIZE_NE("ne-resize"),

        RESIZE_NW("nw-resize"),

        RESIZE_SE("se-resize"),

        RESIZE_SW("sw-resize");

        private String cursor;

        ResizeZoneType(String cursor) {
            this.cursor = cursor;
        }

        public String getCursor() {
            return cursor;
        }
    }

    private final SimplePanel contentPanel;

    private int clientWindowLeft;

    private int clientWindowRight;

    private int clientWindowTop;

    private int clientWindowBottom;

    private HandlerRegistration resizeHandlerRegistration;

    private final HTML captionPanel;

    private boolean dragging = false;

    private ResizeZoneType resizeZoneType;

    private int dragStartX;

    private int dragStartY;

    public DialogPanelNew(boolean autoHide, boolean modal) {
        super(autoHide, modal);
        setStylePrimaryName(CSSClass.pyx4j_Dialog.name());

        getElement().getStyle().setProperty("zIndex", "20");

        getElement().getStyle().setProperty("padding", DRAG_ZONE_WIDTH + "px");

        updateClientWindowPosition();

        VerticalPanel container = new VerticalPanel();
        DOM.setStyleAttribute(container.getElement(), "cursor", "default");

        captionPanel = new HTML();
        captionPanel.setStylePrimaryName(CSSClass.pyx4j_Dialog_Caption.name());
        DOM.setStyleAttribute(captionPanel.getElement(), "cursor", "move");
        captionPanel.setSize("100%", "22px");

        contentPanel = new SimplePanel();
        contentPanel.setStylePrimaryName(CSSClass.pyx4j_Dialog_Content.name());
        contentPanel.setSize("100%", "100%");

        container.add(captionPanel);
        container.add(contentPanel);
        container.setCellHeight(contentPanel, "100%");

        super.setWidget(container);

        addDomHandler(this, MouseMoveEvent.getType());
        addDomHandler(this, MouseUpEvent.getType());
        addDomHandler(this, MouseDownEvent.getType());

    }

    @Override
    public void setWidget(Widget widget) {
        contentPanel.setWidget(widget);
    }

    public void setCaption(String caption) {
        captionPanel.setHTML(caption);
    }

    private void updateClientWindowPosition() {
        clientWindowLeft = Document.get().getScrollLeft();
        clientWindowRight = clientWindowLeft + Window.getClientWidth();
        clientWindowTop = Document.get().getScrollTop();
        clientWindowBottom = clientWindowTop + Window.getClientHeight();
    }

    @Override
    public void show() {
        updateClientWindowPosition();
        if (resizeHandlerRegistration == null) {
            resizeHandlerRegistration = Window.addResizeHandler(new ResizeHandler() {
                public void onResize(ResizeEvent event) {
                    updateClientWindowPosition();
                }
            });
        }
        super.show();
    }

    @Override
    protected void onPreviewNativeEvent(NativePreviewEvent event) {
        // We need to preventDefault() on mouseDown events (outside of the
        // DialogBox content) to keep text from being selected when it
        // is dragged.
        NativeEvent nativeEvent = event.getNativeEvent();

        EventTarget target = nativeEvent.getEventTarget();
        if (target != null) {
            if (!event.isCanceled() && (event.getTypeInt() == Event.ONMOUSEDOWN)) {
                nativeEvent.preventDefault();
            }
        }
        super.onPreviewNativeEvent(event);
    }

    protected void beginDragging(MouseDownEvent event) {
        dragging = true;
        resizeZoneType = getResizeZoneType(event);
        DOM.setCapture(getElement());
        dragStartX = event.getX();
        dragStartY = event.getY();
    }

    protected void continueDragging(MouseMoveEvent event) {
        if (dragging) {
            int absX = event.getX() + getAbsoluteLeft();
            int absY = event.getY() + getAbsoluteTop();
            // if the mouse is off the screen to the left, right, or top, don't
            // move or resize the dialog box.

            if (absX < clientWindowLeft || absX >= clientWindowRight || absY < clientWindowTop || absY >= clientWindowBottom) {
                return;
            }

            int offsetWidth = 0;
            int offsetHeight = 0;

            switch (resizeZoneType) {
            case RESIZE_E:
                offsetWidth = getOffsetWidth() + event.getX() - dragStartX;
                offsetHeight = getOffsetHeight();
                break;
            case RESIZE_W:
                offsetWidth = getOffsetWidth() - event.getX() + dragStartX;
                offsetHeight = getOffsetHeight();
                setPopupPosition(absX - dragStartX, getAbsoluteTop());
                break;
            case RESIZE_S:
                offsetWidth = getOffsetWidth();
                offsetHeight = getOffsetHeight() + event.getY() - dragStartY;
                break;
            case RESIZE_N:
                offsetWidth = getOffsetWidth();
                offsetHeight = getOffsetHeight() - event.getY() + dragStartY;
                setPopupPosition(getAbsoluteLeft(), absY - dragStartY);
                break;
            case RESIZE_SE:
                offsetWidth = getOffsetWidth() + event.getX() - dragStartX;
                offsetHeight = getOffsetHeight() + event.getY() - dragStartY;
                break;
            case RESIZE_SW:
                offsetWidth = getOffsetWidth() - event.getX() + dragStartX;
                offsetHeight = getOffsetHeight() + event.getY() - dragStartY;
                setPopupPosition(absX - dragStartX, getAbsoluteTop());
                break;
            case RESIZE_NE:
                offsetWidth = getOffsetWidth() + event.getX() - dragStartX;
                offsetHeight = getOffsetHeight() - event.getY() + dragStartY;
                setPopupPosition(getAbsoluteLeft(), absY - dragStartY);
                break;
            case RESIZE_NW:
                offsetWidth = getOffsetWidth() - event.getX() + dragStartX;
                offsetHeight = getOffsetHeight() - event.getY() + dragStartY;
                setPopupPosition(absX - dragStartX, absY - dragStartY);
                break;

            default:
                break;
            }

            setPixelSize(offsetWidth - 2 * DRAG_ZONE_WIDTH, offsetHeight - 2 * DRAG_ZONE_WIDTH);

            if (contentPanel.getWidget() instanceof RequiresResize) {
                ((RequiresResize) contentPanel.getWidget()).onResize();
            }

            dragStartX = event.getX();
            dragStartY = event.getY();

        }
    }

    protected void endDragging(MouseUpEvent event) {
        dragging = false;
        resizeZoneType = ResizeZoneType.NONE;
        DOM.releaseCapture(getElement());
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        if (dragging) {
            continueDragging(event);
        } else {
            ResizeZoneType dragZoneType = getResizeZoneType(event);
            setCursor(dragZoneType);
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        if (!ResizeZoneType.NONE.equals(getResizeZoneType(event))) {
            beginDragging(event);
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        if (dragging) {
            endDragging(event);
        }
    }

    private void setCursor(ResizeZoneType dragZoneType) {
        DOM.setStyleAttribute(this.getElement(), "cursor", dragZoneType.getCursor());
    }

    private ResizeZoneType getResizeZoneType(MouseEvent<?> mouseEvent) {
        Event event = Event.as(mouseEvent.getNativeEvent());
        int eventY = DOM.eventGetClientY(event);
        int boxY = this.getAbsoluteTop();
        int height = this.getOffsetHeight();

        int eventX = DOM.eventGetClientX(event);
        int boxX = this.getAbsoluteLeft();
        int width = this.getOffsetWidth();

        int y = eventY - boxY;
        int x = eventX - boxX;
        if (y <= DRAG_ZONE_WIDTH) {
            if (x <= DRAG_ZONE_WIDTH) {
                return ResizeZoneType.RESIZE_NW;
            } else if (x >= width - DRAG_ZONE_WIDTH) {
                return ResizeZoneType.RESIZE_NE;
            } else {
                return ResizeZoneType.RESIZE_N;
            }
        } else if (y >= height - DRAG_ZONE_WIDTH) {
            if (x <= DRAG_ZONE_WIDTH) {
                return ResizeZoneType.RESIZE_SW;
            } else if (x >= width - DRAG_ZONE_WIDTH) {
                return ResizeZoneType.RESIZE_SE;
            } else {
                return ResizeZoneType.RESIZE_S;
            }
        } else {
            if (x <= DRAG_ZONE_WIDTH) {
                return ResizeZoneType.RESIZE_W;
            } else if (x >= width - DRAG_ZONE_WIDTH) {
                return ResizeZoneType.RESIZE_E;
            } else {
                return ResizeZoneType.NONE;
            }
        }

    }

}
