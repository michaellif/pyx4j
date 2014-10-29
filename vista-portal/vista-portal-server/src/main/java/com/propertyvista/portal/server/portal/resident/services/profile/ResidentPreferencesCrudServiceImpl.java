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
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services.profile;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.Context;

import com.propertyvista.domain.tenant.CustomerPreferences;
import com.propertyvista.portal.rpc.portal.CustomerUserVisit;
import com.propertyvista.portal.rpc.portal.resident.services.profile.ResidentPreferencesCrudService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class ResidentPreferencesCrudServiceImpl implements ResidentPreferencesCrudService {

    @Override
    public void persist(AsyncCallback<Key> callback, CustomerPreferences bo) {
        if (bo.getPrimaryKey() == null || Persistence.secureRetrieve(CustomerPreferences.class, bo.getPrimaryKey()) == null) {
            CustomerPreferences cp = EntityFactory.create(CustomerPreferences.class);
            cp.customerUser().set(ResidentPortalContext.getCurrentUser());
            cp.hiddenPortalElements().set(bo.hiddenPortalElements());
            bo = cp;
        }

        Persistence.secureSave(bo);
        Persistence.service().commit();

        Context.visit(CustomerUserVisit.class).setPreferences(bo);
        callback.onSuccess(bo.getPrimaryKey());
    }
}
