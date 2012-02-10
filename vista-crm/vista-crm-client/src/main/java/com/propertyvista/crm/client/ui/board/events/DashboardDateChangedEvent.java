/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.board.events;

import com.google.gwt.event.shared.GwtEvent;

import com.pyx4j.commons.LogicalDate;

public class DashboardDateChangedEvent extends GwtEvent<DashboardDateChangedEventHandler> {

    public static final Type<DashboardDateChangedEventHandler> TYPE = new Type<DashboardDateChangedEventHandler>();

    private final LogicalDate newDate;

    public DashboardDateChangedEvent(LogicalDate newDate) {
        this.newDate = new LogicalDate(newDate);
    }

    @Override
    public Type<DashboardDateChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DashboardDateChangedEventHandler handler) {
        handler.onDashboardDateChanged(this);
    }

    public LogicalDate getNewDate() {
        // unfortunately LogicalDate is mutable type,
        // hence since we don't want some stupid handler to mess with the date we keep here for other ones 
        return new LogicalDate(newDate);
    }

}
