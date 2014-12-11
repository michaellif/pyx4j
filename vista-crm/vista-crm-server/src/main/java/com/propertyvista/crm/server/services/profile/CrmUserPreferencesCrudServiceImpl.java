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
package com.propertyvista.crm.server.services.profile;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.Context;

import com.propertyvista.crm.rpc.CrmUserVisit;
import com.propertyvista.crm.rpc.services.profile.CrmUserPreferencesCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.preferences.CrmUserPreferences;

public class CrmUserPreferencesCrudServiceImpl implements CrmUserPreferencesCrudService {

    @Override
    public void persist(AsyncCallback<Key> callback, CrmUserPreferences bo) {
        if (bo.getPrimaryKey() == null) {
            CrmUserPreferences cp = EntityFactory.create(CrmUserPreferences.class);
            cp.deliveryPreferences().set(bo.deliveryPreferences());
            bo = cp;
        }
        // Force ownership security
        bo.crmUser().set(CrmAppContext.getCurrentUser());
        Persistence.secureSave(bo);
        Persistence.service().commit();

        Context.visit(CrmUserVisit.class).setPreferences(bo);
        callback.onSuccess(bo.getPrimaryKey());
    }
}
