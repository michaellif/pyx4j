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
package com.propertyvista.common.client.events;

import com.google.gwt.event.shared.GwtEvent;

import com.pyx4j.site.client.PageOrientation;

public class ChangePageOrientationEvent extends GwtEvent<ChangePageOrientationHandler> {

    private static Type<ChangePageOrientationHandler> TYPE;

    private final PageOrientation pageOrientation;

    public ChangePageOrientationEvent(PageOrientation newOrientation) {
        assert newOrientation != null : "page orientation can not be null";
        this.pageOrientation = newOrientation;
    }

    public PageOrientation getPageOrientation() {
        return pageOrientation;
    }

    public static Type<ChangePageOrientationHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<ChangePageOrientationHandler>();
        }
        return TYPE;
    }

    @Override
    public final Type<ChangePageOrientationHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ChangePageOrientationHandler handler) {
        handler.onChangePageOrientation(this);
    }
}