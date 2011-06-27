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
package com.propertyvista.crm.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class NavigationUpdateEvent extends GwtEvent<NavigationUpdateHandler> {

    private static Type<NavigationUpdateHandler> TYPE;

    public static Type<NavigationUpdateHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<NavigationUpdateHandler>();
        }
        return TYPE;
    }

    @Override
    public final Type<NavigationUpdateHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(NavigationUpdateHandler handler) {
        handler.onNavigationUpdate(this);
    }
}