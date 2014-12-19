/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 1, 2014
 * @author vlads
 */
package com.propertyvista.portal.server.portal.shared.services.profile;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.Context;

import com.propertyvista.domain.tenant.CustomerPreferences;
import com.propertyvista.portal.rpc.portal.CustomerUserVisit;
import com.propertyvista.portal.rpc.portal.shared.services.profile.CustomerPreferencesCrudService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class CustomerPreferencesCrudServiceImpl implements CustomerPreferencesCrudService {

    @Override
    public void persist(AsyncCallback<Key> callback, CustomerPreferences bo) {
        if (bo.getPrimaryKey() == null) {
            CustomerPreferences cp = EntityFactory.create(CustomerPreferences.class);
            cp.hiddenPortalElements().set(bo.hiddenPortalElements());
            cp.deliveryPreferences().set(bo.deliveryPreferences());
            bo = cp;
        }
        // Force ownership security
        bo.customerUser().set(ResidentPortalContext.getCurrentUser());
        Persistence.secureSave(bo);
        Persistence.service().commit();

        Context.visit(CustomerUserVisit.class).setPreferences(bo);
        callback.onSuccess(bo.getPrimaryKey());
    }
}
