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

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.security.shared.Context;

import com.propertyvista.domain.tenant.CustomerPreferences;
import com.propertyvista.portal.rpc.portal.CustomerUserVisit;
import com.propertyvista.portal.rpc.portal.resident.services.profile.ResidentPreferencesCrudService;

public class ResidentPreferencesCrudServiceImpl extends AbstractCrudServiceImpl<CustomerPreferences> implements ResidentPreferencesCrudService {

    public ResidentPreferencesCrudServiceImpl() {
        super(CustomerPreferences.class);
    }

    @Override
    protected boolean persist(CustomerPreferences bo, CustomerPreferences to) {
        boolean rc = super.persist(bo, to);
        Context.visit(CustomerUserVisit.class).setPreferences(bo);
        return rc;
    }

    @Override
    protected CustomerPreferences init(com.pyx4j.entity.rpc.AbstractCrudService.InitializationData initializationData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<CustomerPreferences>> callback, EntityListCriteria<CustomerPreferences> dtoCriteria) {
        throw new UnsupportedOperationException();
    }

}
