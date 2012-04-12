/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-12
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease.application;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.rpc.services.lease.application.LeaseApplicationCrudService;
import com.propertyvista.crm.server.services.lease.LeaseCrudServiceBaseImpl;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication.Status;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.server.common.ptapp.ApplicationManager;
import com.propertyvista.server.common.util.LeaseManager;

public class LeaseApplicationCrudServiceImpl extends LeaseCrudServiceBaseImpl<LeaseApplicationDTO> implements LeaseApplicationCrudService {

    public LeaseApplicationCrudServiceImpl() {
        super(LeaseApplicationDTO.class);
    }

    @Override
    public void startOnlineApplication(AsyncCallback<VoidSerializable> callback, Key entityId) {
        Lease lease = Persistence.secureRetrieveDraft(dboClass, entityId);
        MasterOnlineApplication ma = ApplicationManager.createMasterApplication(lease);
        ApplicationManager.sendMasterApplicationEmail(ma);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void applicationAction(AsyncCallback<VoidSerializable> callback, LeaseApplicationActionDTO actionDTO) {
        Lease lease = Persistence.service().retrieve(Lease.class, actionDTO.leasePk().getValue());

        // TODO this is wrong!
        Status currentStatus = lease.application().status().getValue();

        //TODO set status base on action.
        lease.leaseApplication().decidedBy().set(CrmAppContext.getCurrentUserEmployee());
        lease.leaseApplication().decisionReason().setValue(actionDTO.decisionReason().getValue());
        lease.leaseApplication().decisionDate().setValue(new LogicalDate());
        Persistence.secureSave(lease);

        switch (actionDTO.action().getValue()) {
        case Approve:
            Lease approvedLease = new LeaseManager().approveApplication(lease.getPrimaryKey());
            if (currentStatus != MasterOnlineApplication.Status.Incomplete) {
                ApplicationManager.sendApproveDeclineApplicationEmail(approvedLease, true);
            }
            break;
        case Decline:
            Lease declinedLease = new LeaseManager().declineApplication(lease.getPrimaryKey());
            if (currentStatus != MasterOnlineApplication.Status.Incomplete) {
                ApplicationManager.sendApproveDeclineApplicationEmail(declinedLease, false);
            }
            break;
        case Cancel:
            new LeaseManager().cancelApplication(lease.getPrimaryKey());
            break;
        }
        Persistence.service().commit();
        callback.onSuccess(null);
    }
}
