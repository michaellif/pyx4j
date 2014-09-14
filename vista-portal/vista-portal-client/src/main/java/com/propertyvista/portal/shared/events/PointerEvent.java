/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.events;

import com.google.gwt.event.shared.GwtEvent;

import com.propertyvista.portal.shared.ui.PointerId;

public class PointerEvent extends GwtEvent<PointerHandler> {

    private static Type<PointerHandler> TYPE = new Type<PointerHandler>();

    private final PointerId pointerId;

    public static Type<PointerHandler> getType() {
        return TYPE;
    }

    public PointerEvent(PointerId pointerId) {
        this.pointerId = pointerId;
    }

    @Override
    public final Type<PointerHandler> getAssociatedType() {
        return TYPE;
    }

    public PointerId getPointerId() {
        return pointerId;
    }

    @Override
    protected void dispatch(PointerHandler handler) {
        handler.showPointer(this);
    }
}