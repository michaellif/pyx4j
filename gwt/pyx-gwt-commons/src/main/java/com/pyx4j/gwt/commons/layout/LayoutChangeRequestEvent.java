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
 * Created on Apr 14, 2010
 * @author vlads
 */
package com.pyx4j.gwt.commons.layout;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class LayoutChangeRequestEvent extends GwtEvent<LayoutChangeRequestHandler> {

    public static Type<LayoutChangeRequestHandler> TYPE = new Type<LayoutChangeRequestHandler>();

    public static enum ChangeType {

        hideSideMenu, toggleSideMenu, toggleSideComm, togglePopupComm, resizeComponents;
    }

    private final ChangeType type;

    private Widget popupCommunicationAnchor;

    public LayoutChangeRequestEvent(ChangeType type) {
        this.type = type;
    }

    public LayoutChangeRequestEvent(Widget popupCommunicationAnchor) {
        this.type = ChangeType.togglePopupComm;
        this.popupCommunicationAnchor = popupCommunicationAnchor;
    }

    @Override
    public GwtEvent.Type<LayoutChangeRequestHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(LayoutChangeRequestHandler handler) {
        handler.onLayoutChangeRequest(this);
    }

    public ChangeType getChangeType() {
        return type;
    }

    public Widget getPopupCommAnchor() {
        return popupCommunicationAnchor;
    }
}
