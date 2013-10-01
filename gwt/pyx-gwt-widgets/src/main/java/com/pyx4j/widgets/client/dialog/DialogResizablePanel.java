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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.widgets.client.PopupPanel;

public class DialogResizablePanel extends PopupPanel implements ProvidesResize, MouseMoveHandler, MouseUpHandler, MouseDownHandler {

    private static final int DRAG_ZONE_WIDTH = 5;

    enum DragZoneType {

        NONE("default"),

        MOVE("move"),

        RESIZE_E("e-resize"),

        RESIZE_W("w-resize"),

        RESIZE_S("s-resize"),

        RESIZE_N("n-resize"),

        RESIZE_NE("ne-resize"),

        RESIZE_NW("nw-resize"),

        RESIZE_SE("se-resize"),

        RESIZE_SW("sw-resize");

        private String cursor;

        DragZoneType(String cursor) {
            this.cursor = cursor;
        }

        public String getCursor() {
            return cursor;
        }
    }

    private final DockPanel container;

//    private final SimplePanel contentHolder;

    private int clientWindowLeft;

    private int clientWindowRight;

    private int clientWindowTop;

    private int clientWindowBottom;

    private HandlerRegistration resizeHandlerRegistration;

    private final CaptionPanel captionPanel;

    private boolean dragging = false;

    private DragZoneType dragZoneType;

    private int dragStartX;

    private int dragStartY;

    private int dragStartLeft;

    private int dragStartTop;

    private int dragStartWidth;

    private int dragStartHeight;

    public DialogResizablePanel(boolean autoHide, boolean modal) {
        super(autoHide, modal);
        setStylePrimaryName(DefaultDialogTheme.StyleName.Dialog.name());

        getElement().getStyle().setProperty("zIndex", "20");

        getElement().getStyle().setProperty("padding", DRAG_ZONE_WIDTH + "px");

        updateClientWindowPosition();

        container = new DockPanel();
        DOM.setStyleAttribute(container.getElement(), "cursor", "default");
        captionPanel = new CaptionPanel();
        container.add(captionPanel, DockPanel.NORTH);

        setWidget(container);

        addDomHandler(this, MouseMoveEvent.getType());
        addDomHandler(this, MouseUpEvent.getType());
        addDomHandler(this, MouseDownEvent.getType());

    }

    public void setContentWidget(Widget widget) {
        container.add(widget, DockPanel.CENTER);
        container.setCellHeight(widget, "100%");
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
                @Override
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
        if (!event.isCanceled() && (event.getTypeInt() == Event.ONMOUSEDOWN)) {
            int x = event.getNativeEvent().getClientX() - this.getAbsoluteLeft();
            int y = event.getNativeEvent().getClientY() - this.getAbsoluteTop();
            if (!DragZoneType.NONE.equals(getDragZoneType(x, y))) {
                nativeEvent.preventDefault();
                return;
            }
        }
        super.onPreviewNativeEvent(event);
    }

    protected void beginDragging(MouseDownEvent event) {
        dragging = true;
        dragZoneType = getDragZoneType(event.getX(), event.getY());
        DOM.setCapture(getElement());
        dragStartX = event.getX() + getAbsoluteLeft();
        dragStartY = event.getY() + getAbsoluteTop();
        dragStartLeft = getAbsoluteLeft();
        dragStartTop = getAbsoluteTop();
        dragStartWidth = getOffsetWidth();
        dragStartHeight = getOffsetHeight();
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

            int width = dragStartWidth;
            int height = dragStartHeight;
            int left = dragStartLeft;
            int top = dragStartTop;

            switch (dragZoneType) {
            case RESIZE_E:
                width = dragStartWidth - dragStartX + absX;
                break;
            case RESIZE_W:
                width = dragStartWidth + dragStartX - absX;
                left = dragStartLeft - dragStartX + absX;
                break;
            case RESIZE_S:
                height = dragStartHeight - dragStartY + absY;
                break;
            case RESIZE_N:
                height = dragStartHeight + dragStartY - absY;
                top = dragStartTop - dragStartY + absY;
                break;
            case RESIZE_SE:
                width = dragStartWidth - dragStartX + absX;
                height = dragStartHeight - dragStartY + absY;
                break;
            case RESIZE_SW:
                width = dragStartWidth + dragStartX - absX;
                height = dragStartHeight - dragStartY + absY;
                left = dragStartLeft - dragStartX + absX;
                break;
            case RESIZE_NE:
                width = dragStartWidth - dragStartX + absX;
                height = dragStartHeight + dragStartY - absY;
                top = dragStartTop - dragStartY + absY;
                break;
            case RESIZE_NW:
                width = dragStartWidth + dragStartX - absX;
                height = dragStartHeight + dragStartY - absY;
                left = dragStartLeft - dragStartX + absX;
                top = dragStartTop - dragStartY + absY;
                break;
            case MOVE:
                left = dragStartLeft - dragStartX + absX;
                top = dragStartTop - dragStartY + absY;
                break;
            default:
                break;
            }

            setPixelSize(width - 2 * DRAG_ZONE_WIDTH, height - 2 * DRAG_ZONE_WIDTH);
            setPopupPosition(left, top);

//            if (contentHolder.getWidget() instanceof RequiresResize) {
//                ((RequiresResize) contentHolder.getWidget()).onResize();
//            }

        }
    }

    protected void endDragging(MouseUpEvent event) {
        dragging = false;
        dragZoneType = DragZoneType.NONE;
        DOM.releaseCapture(getElement());
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        if (dragging) {
            continueDragging(event);
        } else {
            setCursor(getDragZoneType(event.getX(), event.getY()));
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        if (!DragZoneType.NONE.equals(getDragZoneType(event.getX(), event.getY()))) {
            beginDragging(event);
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        if (dragging) {
            endDragging(event);
        }
    }

    private void setCursor(DragZoneType dragZoneType) {
        if (DragZoneType.MOVE.equals(dragZoneType)) {
            DOM.setStyleAttribute(captionPanel.getElement(), "cursor", dragZoneType.getCursor());
        } else {
            DOM.setStyleAttribute(this.getElement(), "cursor", dragZoneType.getCursor());
        }
    }

    private DragZoneType getDragZoneType(int x, int y) {
        if (BrowserType.isIE8()) {
            return DragZoneType.NONE;
        }

        int boxY = this.getAbsoluteTop();
        int height = this.getOffsetHeight();

        int boxX = this.getAbsoluteLeft();
        int width = this.getOffsetWidth();

        if ((x > captionPanel.getAbsoluteLeft() - boxX) && (x < captionPanel.getAbsoluteLeft() + captionPanel.getOffsetWidth() - boxX)
                && (y > captionPanel.getAbsoluteTop() - boxY) && (y < captionPanel.getAbsoluteTop() + captionPanel.getOffsetHeight() - boxY)) {
            return DragZoneType.MOVE;
        } else if (y <= DRAG_ZONE_WIDTH) {
            if (x <= DRAG_ZONE_WIDTH) {
                return DragZoneType.RESIZE_NW;
            } else if (x >= width - DRAG_ZONE_WIDTH) {
                return DragZoneType.RESIZE_NE;
            } else {
                return DragZoneType.RESIZE_N;
            }
        } else if (y >= height - DRAG_ZONE_WIDTH) {
            if (x <= DRAG_ZONE_WIDTH) {
                return DragZoneType.RESIZE_SW;
            } else if (x >= width - DRAG_ZONE_WIDTH) {
                return DragZoneType.RESIZE_SE;
            } else {
                return DragZoneType.RESIZE_S;
            }
        } else {
            if (x <= DRAG_ZONE_WIDTH) {
                return DragZoneType.RESIZE_W;
            } else if (x >= width - DRAG_ZONE_WIDTH) {
                return DragZoneType.RESIZE_E;
            } else {
                return DragZoneType.NONE;
            }
        }

    }

    class CaptionPanel extends HTML {

        public CaptionPanel() {
            setWordWrap(false);
            setStylePrimaryName(DefaultDialogTheme.StyleName.DialogCaption.name());
            getElement().getStyle().setHeight(1.5, Unit.EM);
            getElement().getStyle().setLineHeight(1.5, Unit.EM);
        }

    }

}
