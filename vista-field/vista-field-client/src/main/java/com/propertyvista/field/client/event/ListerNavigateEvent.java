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

public class ListerNavigateEvent extends GwtEvent<ListerNavigateHandler> {

    private static Type<ListerNavigateHandler> TYPE;

    private final NavigateAction action;

    public ListerNavigateEvent(NavigateAction action) {
        assert action != null : "action can not be null";
        this.action = action;
    }

    public NavigateAction getAction() {
        return action;
    }

    public static Type<ListerNavigateHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<ListerNavigateHandler>();
        }
        return TYPE;
    }

    @Override
    public final Type<ListerNavigateHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ListerNavigateHandler handler) {
        handler.onListerNavigate(this);
    }
}