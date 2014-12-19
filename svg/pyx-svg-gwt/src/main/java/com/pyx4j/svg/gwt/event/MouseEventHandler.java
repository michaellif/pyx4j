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
 */
package com.pyx4j.svg.gwt.event;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.svg.basic.Shape;

public abstract class MouseEventHandler implements MouseMoveHandler, MouseUpHandler, MouseDownHandler {

    protected boolean dragging = false;
    protected Widget dragHandle;
    protected int dragStartX;
    protected int dragStartY;
    
    public MouseEventHandler(Widget dragHandle) {
        this.dragHandle = dragHandle;
        dragHandle.addDomHandler(this, MouseDownEvent.getType());
        dragHandle.addDomHandler(this, MouseUpEvent.getType());
        dragHandle.addDomHandler(this, MouseMoveEvent.getType());
    }

    public abstract void handleDrag(int absX, int absY);

    public void onMouseDown(MouseDownEvent event) {
     	dragging = true;
        DOM.setCapture(dragHandle.getElement());
        dragStartX = event.getX();
        dragStartY = event.getY();
        DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
      }
   
      public void onMouseMove(MouseMoveEvent event) {
    	  
          if (dragging) {
              handleDrag(event.getX(), event.getY());
              dragStartX = event.getX();
              dragStartY = event.getY();
          }
      }
      
      public void onMouseUp(MouseUpEvent event) {
        dragging = false;
        DOM.releaseCapture(dragHandle.getElement());
      }
}
