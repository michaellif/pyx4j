/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 2, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.utils;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.Context;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.tenant.CustomerPreferences;
import com.propertyvista.domain.tenant.CustomerPreferencesPortalHidable;
import com.propertyvista.portal.resident.events.PortalHidableEvent;
import com.propertyvista.portal.rpc.portal.resident.services.profile.ResidentPreferencesCrudService;

public class PortalHidablePreferenceManager {

    public static void updatePreference(final CustomerPreferencesPortalHidable.Type preferenceType, final boolean preferenceValue) {
        CustomerPreferences cp = Context.userPreferences(CustomerPreferences.class);
        if (preferenceValue) {
            CustomerPreferencesPortalHidable hiddenProperty = EntityFactory.create(CustomerPreferencesPortalHidable.class);
            hiddenProperty.type().setValue(preferenceType);
            cp.hiddenPortalElements().add(hiddenProperty);
        } else {
            CustomerPreferencesPortalHidable preference = getGettingStarted(cp);
            if (preference != null) {
                cp.hiddenPortalElements().remove(preference);
            }
        }

        ((ResidentPreferencesCrudService) GWT.create(ResidentPreferencesCrudService.class)).persist(new DefaultAsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                AppSite.getEventBus().fireEvent(new PortalHidableEvent(preferenceType, preferenceValue));
            }
        }, cp);

    }

    private static CustomerPreferencesPortalHidable getGettingStarted(CustomerPreferences cp) {
        ISet<CustomerPreferencesPortalHidable> hiddenElements = cp.hiddenPortalElements();
        for (CustomerPreferencesPortalHidable member : hiddenElements) {
            if (CustomerPreferencesPortalHidable.Type.GettingStartedGadget.equals(member.type().getValue())) {
                return member;
            }
        }
        return null;
    }

    public static boolean isHidden(CustomerPreferencesPortalHidable.Type preferenceType) {
        CustomerPreferences cp = Context.userPreferences(CustomerPreferences.class);
        ISet<CustomerPreferencesPortalHidable> hiddenElements = cp.hiddenPortalElements();
        for (CustomerPreferencesPortalHidable member : hiddenElements) {
            if (preferenceType.equals(member.type().getValue())) {
                return true;
            }
        }
        return false;
    }
}
