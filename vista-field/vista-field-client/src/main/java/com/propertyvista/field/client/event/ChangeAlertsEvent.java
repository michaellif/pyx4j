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

public class ChangeAlertsEvent extends GwtEvent<ChangeAlertsHandler> {

    private static Type<ChangeAlertsHandler> TYPE;

    private final AlertsAction action;

    public ChangeAlertsEvent(AlertsAction action) {
        assert action != null : "action can not be null";
        this.action = action;
    }

    public AlertsAction getAction() {
        return action;
    }

    public static Type<ChangeAlertsHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<ChangeAlertsHandler>();
        }
        return TYPE;
    }

    @Override
    public final Type<ChangeAlertsHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ChangeAlertsHandler handler) {
        handler.onChangeAlerts(this);
    }
}