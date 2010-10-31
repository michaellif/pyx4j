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
 * Created on 2010-10-07
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.dnd.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetAccess;

public abstract class DnDAdapter {

    private DnDAdapter() {
    }

    private static native void addEventListener(String type, Element elt) /*-{
        if ($doc.addEventListener) {
        elt.addEventListener(type, @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, true);
        } else {
        var ieDispatcher = $entry(function() { @com.google.gwt.user.client.impl.DOMImplTrident::callDispatchEvent.call(elt, $wnd.event); });    
        elt.attachEvent("on"+ type, ieDispatcher);
        }
    }-*/;

    private static <H extends EventHandler> HandlerRegistration addHandler(Widget widget, DomEvent.Type<H> type, final H handler) {
        HandlerManager hm = WidgetAccess.ensureHandlers(widget);
        if (hm.getHandlerCount(type) == 0) {
            addEventListener(type.getName(), widget.getElement());
        }
        return hm.addHandler(type, handler);
    }

    public static HandlerRegistration addDragStartHandler(Widget widget, DragStartHandler handler) {
        return addHandler(widget, DragStartEvent.getType(), handler);
    }

    public static HandlerRegistration addDragEndHandler(Widget widget, DragEndHandler handler) {
        return addHandler(widget, DragEndEvent.getType(), handler);
    }

    public static HandlerRegistration addDragEnterHandler(Widget widget, DragEnterHandler handler) {
        return addHandler(widget, DragEnterEvent.getType(), handler);
    }

    public static HandlerRegistration addDragLeaveHandler(Widget widget, DragLeaveHandler handler) {
        return addHandler(widget, DragLeaveEvent.getType(), handler);
    }

    public static HandlerRegistration addDragOverHandler(Widget widget, DragOverHandler handler) {
        return addHandler(widget, DragOverEvent.getType(), handler);
    }

    public static HandlerRegistration addDragHandler(Widget widget, DragHandler handler) {
        return addHandler(widget, DragEvent.getType(), handler);
    }

    public static HandlerRegistration addDropHandler(Widget widget, DropHandler handler) {
        return addHandler(widget, DropEvent.getType(), handler);
    }

}
