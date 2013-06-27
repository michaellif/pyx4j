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
 * @version $Id$
 */
package com.pyx4j.site.client.ui.layout.responsive;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class LayoutChangeRerquestEvent extends GwtEvent<LayoutChangeRerquestHandler> {

    public static Type<LayoutChangeRerquestHandler> TYPE = new Type<LayoutChangeRerquestHandler>();

    public static enum ChangeType {

        toggleSideMenu, toggleSideCommunication, togglePopupCommunication, resizeComponents;
    }

    private final ChangeType type;

    private Widget popupCommunicationAnchor;

    public LayoutChangeRerquestEvent(ChangeType type) {
        this.type = type;
    }

    public LayoutChangeRerquestEvent(Widget popupCommunicationAnchor) {
        this.type = ChangeType.togglePopupCommunication;
        this.popupCommunicationAnchor = popupCommunicationAnchor;
    }

    @Override
    public GwtEvent.Type<LayoutChangeRerquestHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(LayoutChangeRerquestHandler handler) {
        handler.onLayoutChangeRerquest(this);
    }

    public ChangeType getChangeType() {
        return type;
    }

    public Widget getPopupCommunicationAnchor() {
        return popupCommunicationAnchor;
    }
}
