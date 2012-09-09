/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2012-09-08
 * @author Alex
 * @version $Id$
 */
package com.pyx4j.svg.gwt;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.svg.basic.Shape;

public class MouseEventImpl extends SimplePanel implements HasMouseDownHandlers, HasMouseUpHandlers, HasMouseMoveHandlers, NativePreviewHandler {

    private final DraggableMouseListener listener;

    public MouseEventImpl(Shape inner) {
        DOM.appendChild(getElement(), SvgDOM.getElementById(inner.getId()));
        DOM.setStyleAttribute(getElement(), "position", "absolute");
        DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) | Event.MOUSEEVENTS);
        Event.addNativePreviewHandler(this);
        listener = new DraggableMouseListener();
        addMouseDownHandler(listener);
        addMouseUpHandler(listener);
        addMouseMoveHandler(listener);
    }

    @Override
    public void onBrowserEvent(Event event) {
        switch (DOM.eventGetType(event)) {
        case Event.ONMOUSEDOWN:
        case Event.ONMOUSEUP:
        case Event.ONMOUSEMOVE:
            DomEvent.fireNativeEvent(event, this);
            break;
        }
    }

    private class DraggableMouseListener implements MouseDownHandler, MouseUpHandler, MouseMoveHandler {

        private boolean dragging = false;

        private int dragStartX;

        private int dragStartY;

        @Override
        public void onMouseDown(MouseDownEvent event) {
            dragging = true;

            //capturing the mouse to the dragged widget.
            DOM.setCapture(getElement());
            dragStartX = event.getRelativeX(getElement());
            dragStartY = event.getRelativeY(getElement());
        }

        @Override
        public void onMouseUp(MouseUpEvent event) {
            dragging = false;
            DOM.releaseCapture(getElement());
        }

        @Override
        public void onMouseMove(MouseMoveEvent event) {
            if (dragging) {
                // we don’t want the widget to go off-screen, so the top/left
                // values should always remain be positive.
                int newX = Math.max(0, event.getRelativeX(getElement()) + getAbsoluteLeft() - dragStartX);
                int newY = Math.max(0, event.getRelativeY(getElement()) + getAbsoluteTop() - dragStartY);
                DOM.setStyleAttribute(getElement(), "left", "" + newX);
                DOM.setStyleAttribute(getElement(), "top", "" + newY);
            }
        }
    }

    @Override
    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return addDomHandler(handler, MouseDownEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return addDomHandler(handler, MouseUpEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return addDomHandler(handler, MouseMoveEvent.getType());
    }

    @Override
    public void onPreviewNativeEvent(NativePreviewEvent event) {
        Event e = Event.as(event.getNativeEvent());
        if (DOM.eventGetType(e) == Event.ONMOUSEDOWN && DOM.isOrHasChild(getElement(), DOM.eventGetTarget(e))) {
            DOM.eventPreventDefault(e);
        }
    }

}
