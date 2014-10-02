/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 */
package com.propertyvista.portal.resident.events;

import com.google.gwt.event.shared.GwtEvent;

import com.propertyvista.domain.tenant.CustomerPreferencesPortalHidable;

public class PortalHidableEvent extends GwtEvent<PortalHidableHandler> {

    private static Type<PortalHidableHandler> TYPE = new Type<PortalHidableHandler>();

    private final CustomerPreferencesPortalHidable.Type preferenceType;

    private final boolean preferenceValue;

    public static Type<PortalHidableHandler> getType() {
        return TYPE;
    }

    public PortalHidableEvent(CustomerPreferencesPortalHidable.Type preferenceType, boolean preferenceValue) {
        this.preferenceType = preferenceType;
        this.preferenceValue = preferenceValue;
    }

    @Override
    public final Type<PortalHidableHandler> getAssociatedType() {
        return TYPE;
    }

    public CustomerPreferencesPortalHidable.Type getPreferenceType() {
        return preferenceType;
    }

    public boolean getPreferenceValue() {
        return preferenceValue;
    }

    @Override
    protected void dispatch(PortalHidableHandler handler) {
        handler.onUpdate(this);
    }
}