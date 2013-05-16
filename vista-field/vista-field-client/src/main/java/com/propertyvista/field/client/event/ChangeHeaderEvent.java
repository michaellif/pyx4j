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
package com.propertyvista.field.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ChangeHeaderEvent extends GwtEvent<ChangeHeaderHandler> {

    private static Type<ChangeHeaderHandler> TYPE;

    private final HeaderAction action;

    public ChangeHeaderEvent(HeaderAction action) {
        assert action != null : "action can not be null";
        this.action = action;
    }

    public HeaderAction getAction() {
        return action;
    }

    public static Type<ChangeHeaderHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<ChangeHeaderHandler>();
        }
        return TYPE;
    }

    @Override
    public final Type<ChangeHeaderHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ChangeHeaderHandler handler) {
        handler.onChangeHeader(this);
    }
}