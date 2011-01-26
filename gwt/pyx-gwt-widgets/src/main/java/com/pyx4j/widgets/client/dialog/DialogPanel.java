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

import static com.google.gwt.user.client.ui.HasHorizontalAlignment.ALIGN_DEFAULT;
import static com.google.gwt.user.client.ui.HasVerticalAlignment.ALIGN_TOP;

import java.util.HashMap;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.style.CSSClass;
import com.pyx4j.widgets.client.util.BrowserType;

public class DialogPanel extends PopupPanel implements ProvidesResize {

    private final static int BORDER_WIDTH = 1;

    enum DragZoneType {
        NONE(""),

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

    private final DialogDecorator decoratorPanel;

    private DialogDecorator.Caption captionPanel;

    private final SimplePanel contentPanel;

    private HandlerRegistration resizeHandlerRegistration;

    private boolean dragging;

    private int dragStartX;

    private int dragStartY;

    private int clientWindowLeft;

    private int clientWindowRight;

    private int clientWindowTop;

    private int clientWindowBottom;

    public DialogPanel(boolean autoHide, boolean modal) {
        super(autoHide, modal);

        setStylePrimaryName(CSSClass.pyx4j_Dialog.name());
        //Don't move it to styles because width of border is used in calculation of resizing
        if (BrowserType.isIE6()) {
            getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
            getElement().getStyle().setProperty("borderWidth", BORDER_WIDTH + "px");
        } else {
            getElement().getStyle().setProperty("borderStyle", "ridge");
            getElement().getStyle().setProperty("borderWidth", BORDER_WIDTH + "px");
        }

        getElement().getStyle().setProperty("zIndex", "20");

        updateClientWindowPosition();

        contentPanel = new SimplePanel();
        contentPanel.setStylePrimaryName(CSSClass.pyx4j_Dialog_Content.name());

        if (BrowserType.isIE6()) {
            contentPanel.setWidth("100%");
        } else {
            contentPanel.setSize("100%", "100%");
        }

        decoratorPanel = new DialogDecorator();

        super.setWidget(decoratorPanel);

    }

    @Override
    public void setWidget(Widget widget) {
        contentPanel.setWidget(widget);
    }

    protected void beginDragging(DragZone dragZone, MouseDownEvent event) {
        dragging = true;
        DOM.setCapture(dragZone.getElement());
        dragStartX = event.getX();
        dragStartY = event.getY();
    }

    protected void continueDragging(DragZone dragZone, MouseMoveEvent event) {
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

            switch (dragZone.getDragZoneType()) {
            case MOVE:
                setPopupPosition(absX - dragStartX, absY - dragStartY);
                offsetWidth = getOffsetWidth();
                offsetHeight = getOffsetHeight();
                break;
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
            setPixelSize(offsetWidth - 2 * BORDER_WIDTH, offsetHeight - 2 * BORDER_WIDTH);

            if (contentPanel.getWidget() instanceof RequiresResize) {
                ((RequiresResize) contentPanel.getWidget()).onResize();
            }
        }
    }

    protected void endDragging(DragZone dragZone, MouseUpEvent event) {
        dragging = false;
        DOM.releaseCapture(dragZone.getElement());
    }

    @Override
    public void hide() {
        if (resizeHandlerRegistration != null) {
            resizeHandlerRegistration.removeHandler();
            resizeHandlerRegistration = null;
        }
        super.hide();
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
            String className = Element.as(target).getClassName();
            if (!event.isCanceled() && (event.getTypeInt() == Event.ONMOUSEDOWN)
                    && (CSSClass.pyx4j_Dialog_Resizer.name().equals(className) || CSSClass.pyx4j_Dialog_Caption.name().equals(className))) {
                nativeEvent.preventDefault();
            }
        }
        super.onPreviewNativeEvent(event);
    }

    interface DragZone extends HasAllMouseHandlers {

        public String getWidth();

        public String getHeight();

        public Element getElement();

        public DragZoneType getDragZoneType();
    }

    class DialogDecorator extends CellPanel {

        private final HorizontalAlignmentConstant horzAlign = ALIGN_DEFAULT;

        private final VerticalAlignmentConstant vertAlign = ALIGN_TOP;

        private final HashMap<DragZone, DragZoneType> resizers = new HashMap<DragZone, DragZoneType>();

        class Caption extends HTML implements DragZone {

            Caption() {

                setStylePrimaryName(CSSClass.pyx4j_Dialog_Caption.name());

                DOM.setStyleAttribute(getElement(), "cursor", DragZoneType.MOVE.getCursor());

                MouseHandler mouseHandler = new MouseHandler(this);
                addDomHandler(mouseHandler, MouseDownEvent.getType());
                addDomHandler(mouseHandler, MouseUpEvent.getType());
                addDomHandler(mouseHandler, MouseMoveEvent.getType());
                addDomHandler(mouseHandler, MouseOverEvent.getType());
                addDomHandler(mouseHandler, MouseOutEvent.getType());

                setSize("100%", "22px");
            }

            public String getWidth() {
                return "100%";
            }

            public String getHeight() {
                return "22px";
            }

            public DragZoneType getDragZoneType() {
                return DragZoneType.MOVE;
            }

            public void setCaption(String caption) {
                setHTML(caption);
            }
        }

        class Resizer extends Widget implements DragZone {

            private final String width;

            private final String height;

            private final DragZoneType dragZoneType;

            Resizer(DragZoneType dragZoneType, String width, String height) {
                this.dragZoneType = dragZoneType;
                this.width = width;
                this.height = height;

                setElement(Document.get().createDivElement());

                setStylePrimaryName(CSSClass.pyx4j_Dialog_Resizer.name());

                DOM.setStyleAttribute(getElement(), "fontSize", "0");

                if (!BrowserType.isIE6()) {
                    DOM.setStyleAttribute(getElement(), "cursor", dragZoneType.getCursor());

                    MouseHandler mouseHandler = new MouseHandler(this);
                    addDomHandler(mouseHandler, MouseDownEvent.getType());
                    addDomHandler(mouseHandler, MouseUpEvent.getType());
                    addDomHandler(mouseHandler, MouseMoveEvent.getType());
                    addDomHandler(mouseHandler, MouseOverEvent.getType());
                    addDomHandler(mouseHandler, MouseOutEvent.getType());
                }

                resizers.put(this, dragZoneType);

                setSize(width, height);
            }

            public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
                return addDomHandler(handler, MouseDownEvent.getType());
            }

            public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
                return addDomHandler(handler, MouseMoveEvent.getType());
            }

            public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
                return addDomHandler(handler, MouseOutEvent.getType());
            }

            public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
                return addDomHandler(handler, MouseOverEvent.getType());
            }

            public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
                return addDomHandler(handler, MouseUpEvent.getType());
            }

            public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
                return addDomHandler(handler, MouseWheelEvent.getType());
            }

            public String getWidth() {
                return width;
            }

            public String getHeight() {
                return height;
            }

            public DragZoneType getDragZoneType() {
                return dragZoneType;
            }

        }

        DialogDecorator() {

            DOM.setElementPropertyInt(getTable(), "cellSpacing", 0);
            DOM.setElementPropertyInt(getTable(), "cellPadding", 0);

            //firstRow
            Element row = DOM.createTR();
            DOM.appendChild(getBody(), row);

            if (!BrowserType.isIE6()) {
                appendTd(new Resizer(DragZoneType.RESIZE_NW, "5px", "5px"), row);
                appendTd(new Resizer(DragZoneType.RESIZE_N, "100%", "5px"), row);
                appendTd(new Resizer(DragZoneType.RESIZE_NE, "5px", "5px"), row);
            }

            //secondRow
            row = DOM.createTR();
            DOM.appendChild(getBody(), row);

            captionPanel = new Caption();

            if (!BrowserType.isIE6()) {
                appendTd(new Resizer(DragZoneType.RESIZE_W, "5px", "100%"), row, 2, 1);
            }

            appendTd(captionPanel, row, "100%", "5px");

            if (!BrowserType.isIE6()) {
                appendTd(new Resizer(DragZoneType.RESIZE_E, "5px", "100%"), row, 2, 1);
            }

            //thirdRow
            row = DOM.createTR();
            DOM.appendChild(getBody(), row);

            appendTd(contentPanel, row, "100%", "100%");

            //fourthRow
            row = DOM.createTR();
            DOM.appendChild(getBody(), row);

            if (!BrowserType.isIE6()) {
                appendTd(new Resizer(DragZoneType.RESIZE_SW, "5px", "5px"), row);
                appendTd(new Resizer(DragZoneType.RESIZE_S, "100%", "5px"), row);
                appendTd(new Resizer(DragZoneType.RESIZE_SE, "5px", "5px"), row);
            }

        }

        private void appendTd(Resizer r, Element row, int rowSpan, int colSpan) {
            appendTd(r, row, r.getWidth(), r.getHeight(), rowSpan, colSpan);
        }

        private void appendTd(DragZone dragZone, Element row) {
            appendTd((Widget) dragZone, row, dragZone.getWidth(), dragZone.getHeight(), 1, 1);
        }

        private void appendTd(Widget w, Element row, String width, String height) {
            appendTd(w, row, width, height, 1, 1);
        }

        private void appendTd(Widget w, Element row, String width, String height, int rowSpan, int colSpan) {
            Element td = DOM.createTD();
            setCellHorizontalAlignment(td, horzAlign);
            setCellVerticalAlignment(td, vertAlign);
            DOM.setElementProperty(td, "width", width);
            DOM.setElementProperty(td, "height", height);
            DOM.appendChild(row, td);
            DOM.setElementPropertyInt(td, "rowSpan", rowSpan);
            DOM.setElementPropertyInt(td, "colSpan", colSpan);
            super.insert(w, td, 0, false);
        }

        private class MouseHandler implements MouseDownHandler, MouseUpHandler, MouseOutHandler, MouseOverHandler, MouseMoveHandler {

            private final DragZone dragZone;

            MouseHandler(DragZone dragZone) {
                this.dragZone = dragZone;
            }

            public void onMouseDown(MouseDownEvent event) {
                beginDragging(dragZone, event);
            }

            public void onMouseMove(MouseMoveEvent event) {
                continueDragging(dragZone, event);
            }

            public void onMouseOut(MouseOutEvent event) {
            }

            public void onMouseOver(MouseOverEvent event) {
            }

            public void onMouseUp(MouseUpEvent event) {
                endDragging(dragZone, event);
            }
        }
    }

    public void setCaption(String caption) {
        this.captionPanel.setCaption(caption);
    }
}
