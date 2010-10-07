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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetAccess;

public class DnDAdapter {

    private final Widget widget;

    public DnDAdapter(Widget widget) {
        this.widget = widget;
        sinkDnDEvents(widget.getElement());
    }

    private native void sinkDnDEvents(Element elt)
    /*-{
        elt.addEventListener('dragstart', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, true);
        elt.addEventListener('drag', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, true);
        elt.addEventListener('dragenter', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, true);
        elt.addEventListener('dragleave', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, true);
        elt.addEventListener('dragover', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, true);
        elt.addEventListener('drop', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, true);
        elt.addEventListener('dragend', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, true);
    }-*/;

    public HandlerRegistration addDragStartHandler(DragStartHandler handler) {
        return WidgetAccess.ensureHandlers(widget).addHandler(DragStartEvent.getType(), handler);
    }

    public HandlerRegistration addDragEndHandler(DragEndHandler handler) {
        return WidgetAccess.ensureHandlers(widget).addHandler(DragEndEvent.getType(), handler);
    }

    public HandlerRegistration addDragEnterHandler(DragEnterHandler handler) {
        return WidgetAccess.ensureHandlers(widget).addHandler(DragEnterEvent.getType(), handler);
    }

    public HandlerRegistration addDragLeaveHandler(DragLeaveHandler handler) {
        return WidgetAccess.ensureHandlers(widget).addHandler(DragLeaveEvent.getType(), handler);
    }

    public HandlerRegistration addDragOverHandler(DragOverHandler handler) {
        return WidgetAccess.ensureHandlers(widget).addHandler(DragOverEvent.getType(), handler);
    }

    public HandlerRegistration addDragHandler(DragHandler handler) {
        return WidgetAccess.ensureHandlers(widget).addHandler(DragEvent.getType(), handler);
    }

    public HandlerRegistration addDropHandler(DropHandler handler) {
        return WidgetAccess.ensureHandlers(widget).addHandler(DropEvent.getType(), handler);
    }

}
