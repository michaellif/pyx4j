/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.prospect.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationWizardService;
import com.propertyvista.portal.server.portal.prospect.ProspectPortalContext;

public class ApplicationWizardServiceImpl implements ApplicationWizardService {

    public ApplicationWizardServiceImpl() {

    }

    @Override
    public void init(AsyncCallback<OnlineApplicationDTO> callback) {
        OnlineApplication bo = ProspectPortalContext.getOnlineApplication();

        Persistence.service().retrieve(bo.masterOnlineApplication());
        Persistence.service().retrieve(bo.masterOnlineApplication().leaseApplication().lease());
        Persistence.service().retrieve(bo.masterOnlineApplication().leaseApplication().lease().unit().building());
        Persistence.service().retrieve(bo.masterOnlineApplication().leaseApplication().lease().unit().floorplan());

        assert bo.masterOnlineApplication().leaseApplication().lease().currentTerm().version().isNull();
        LeaseTerm term = Persistence.retrieveDraftForEdit(LeaseTerm.class, bo.masterOnlineApplication().leaseApplication().lease().currentTerm()
                .getPrimaryKey());

        OnlineApplicationDTO to = EntityFactory.create(OnlineApplicationDTO.class);
        to.unit().set(bo.masterOnlineApplication().leaseApplication().lease().unit());
        to.leaseFrom().setValue(bo.masterOnlineApplication().leaseApplication().lease().leaseFrom().getValue());
        to.leaseTo().setValue(bo.masterOnlineApplication().leaseApplication().lease().leaseTo().getValue());
        to.leasePrice().set(term.version().leaseProducts().serviceItem().agreedPrice());

        callback.onSuccess(to);
    }

    @Override
    public void save(AsyncCallback<Key> callback, OnlineApplicationDTO editableEntity) {
        callback.onSuccess(null);
    }

    @Override
    public void submit(AsyncCallback<Key> callback, OnlineApplicationDTO editableEntity) {
        callback.onSuccess(null);
    }
}
