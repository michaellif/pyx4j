/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.crm.rpc.services.lease.LeaseCrudService;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication;
import com.propertyvista.dto.ApplicationUserDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.server.common.ptapp.ApplicationManager;

public class LeaseCrudServiceImpl extends LeaseCrudServiceBaseImpl<LeaseDTO> implements LeaseCrudService {

    public LeaseCrudServiceImpl() {
        super(LeaseDTO.class);
    }

    @Override
    public void notice(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate date, LogicalDate moveOut) {
        ServerSideFactory.create(LeaseFacade.class).createCompletionEvent(entityId, CompletionType.Notice, date, moveOut);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void cancelNotice(AsyncCallback<VoidSerializable> callback, Key entityId) {
        ServerSideFactory.create(LeaseFacade.class).cancelCompletionEvent(entityId);
        Persistence.service().commit();
        callback.onSuccess(null);

    }

    @Override
    public void evict(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate date, LogicalDate moveOut) {
        ServerSideFactory.create(LeaseFacade.class).createCompletionEvent(entityId, CompletionType.Eviction, date, moveOut);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void cancelEvict(AsyncCallback<VoidSerializable> callback, Key entityId) {
        ServerSideFactory.create(LeaseFacade.class).cancelCompletionEvent(entityId);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void sendMail(AsyncCallback<VoidSerializable> callback, Key entityId, Vector<ApplicationUserDTO> users, EmailTemplateType emailType) {
        Lease lease = Persistence.service().retrieve(dboClass, entityId.asDraftKey());
        if ((lease == null) || (lease.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(dboClass).getCaption() + "' " + entityId + " NotFound");
        }

        MasterOnlineApplication app = lease.leaseApplication().onlineApplication();
        Persistence.service().retrieve(app);

        for (ApplicationUserDTO user : users) {
            if (user.user().isValueDetached()) {
                Persistence.service().retrieve(user.user());
            }
            switch (user.userType().getValue()) {
            case Applicant:
                ApplicationManager.ensureProspectiveTenantUser(user.user(), user.person(), VistaCustomerBehavior.TenantPrimary);
                break;
            case CoApplicant:
                ApplicationManager.ensureProspectiveTenantUser(user.user(), user.person(), VistaCustomerBehavior.TenantSecondary);
                break;
            case Guarantor:
                ApplicationManager.ensureProspectiveTenantUser(user.user(), user.person(), VistaCustomerBehavior.Guarantor);
                break;
            }
// TODO : implement            
//            ServerSideFactory.create(CommunicationFacade.class).sendCustomerMessage(user.user());
        }

        Persistence.service().commit();
        callback.onSuccess(null);
    }

}