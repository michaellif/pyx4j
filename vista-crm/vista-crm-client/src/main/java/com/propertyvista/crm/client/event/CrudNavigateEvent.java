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

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.site.rpc.CrudAppPlace;

public class CrudNavigateEvent extends GwtEvent<CrudNavigateHandler> {

    private static Type<CrudNavigateHandler> TYPE;

    private final CrudAppPlace place;

    private final IEntity value;

    public CrudNavigateEvent(CrudAppPlace place) {
        this(place, null);
    }

    public CrudNavigateEvent(CrudAppPlace place, IEntity value) {
        this.place = place;
        this.value = value;
    }

    public CrudAppPlace getPlace() {
        return place;
    }

    public IEntity getValue() {
        return value;
    }

    public static Type<CrudNavigateHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<CrudNavigateHandler>();
        }
        return TYPE;
    }

    @Override
    public final Type<CrudNavigateHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CrudNavigateHandler handler) {
        handler.onCrudNavigate(this);
    }
}