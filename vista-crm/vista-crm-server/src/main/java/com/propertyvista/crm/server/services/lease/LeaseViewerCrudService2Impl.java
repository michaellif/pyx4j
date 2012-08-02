/*
 *
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
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
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.tenant.LeaseFacade2;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService2;
import com.propertyvista.crm.server.services.lease.common.LeaseViewerCrudServiceBase2Impl;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease2;
import com.propertyvista.domain.tenant.lease.Lease2.CompletionType;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication2;
import com.propertyvista.dto.LeaseDTO2;

public class LeaseViewerCrudService2Impl extends LeaseViewerCrudServiceBase2Impl<LeaseDTO2> implements LeaseViewerCrudService2 {

    private final static I18n i18n = I18n.get(LeaseViewerCrudService2Impl.class);

    public LeaseViewerCrudService2Impl() {
        super(LeaseDTO2.class);
    }

    @Override
    protected void enhanceRetrieved(Lease2 in, LeaseDTO2 dto) {
        super.enhanceRetrieved(in, dto);

//        dto.transactionHistory().set(ServerSideFactory.create(ARFacade.class).getTransactionHistory(dto.billingAccount()));
    }

    @Override
    public void notice(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate date, LogicalDate moveOut) {
        ServerSideFactory.create(LeaseFacade2.class).createCompletionEvent(entityId, CompletionType.Notice, date, moveOut);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void cancelNotice(AsyncCallback<VoidSerializable> callback, Key entityId) {
        ServerSideFactory.create(LeaseFacade2.class).cancelCompletionEvent(entityId);
        Persistence.service().commit();
        callback.onSuccess(null);

    }

    @Override
    public void evict(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate date, LogicalDate moveOut) {
        ServerSideFactory.create(LeaseFacade2.class).createCompletionEvent(entityId, CompletionType.Eviction, date, moveOut);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void cancelEvict(AsyncCallback<VoidSerializable> callback, Key entityId) {
        ServerSideFactory.create(LeaseFacade2.class).cancelCompletionEvent(entityId);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void activate(AsyncCallback<VoidSerializable> callback, Key entityId) {
        ServerSideFactory.create(LeaseFacade2.class).approveExistingLease(EntityFactory.createIdentityStub(Lease2.class, entityId));
        ServerSideFactory.create(LeaseFacade2.class).activate(entityId);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void sendMail(AsyncCallback<String> callback, Key entityId, Vector<LeaseParticipant> users, EmailTemplateType emailType) {
        Lease2 lease = Persistence.service().retrieve(dboClass, entityId.asCurrentKey());
        if ((lease == null) || (lease.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(dboClass).getCaption() + "' " + entityId + " NotFound");
        }
        if (!Lease2.Status.current().contains(lease.status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Can't send tenant email for inactive Lease2"));
        }
        if (users.isEmpty()) {
            throw new UserRuntimeException(i18n.tr("No customer was selected to send email"));
        }

        MasterOnlineApplication2 app = lease.leaseApplication().onlineApplication();
        Persistence.service().retrieve(app);

        // check that all lease participants have an associated user entity (email)
        for (LeaseParticipant user : users) {
            if (user.customer().user().isNull()) {
                throw new UserRuntimeException(i18n.tr("''Send Email'' operation failed, email of lease participant {0} was not found", user.customer()
                        .person().name().getStringView()));
            }
        }

        if (emailType == EmailTemplateType.TenantInvitation) {
            // check that selected users can be used for this template
            for (LeaseParticipant user : users) {
                if (user.isInstanceOf(Guarantor.class)) {
                    throw new UserRuntimeException(i18n.tr(
                            "''Send Mail'' operation failed: can''t send \"{0}\" for Guarantor. Please re-send e-mail for all valid recipients.",
                            EmailTemplateType.TenantInvitation));
                }
            }

            // send e-mails
            CommunicationFacade commFacade = ServerSideFactory.create(CommunicationFacade.class);
            for (LeaseParticipant user : users) {
                if (user.isInstanceOf(Tenant.class)) {
                    Tenant tenant = user.duplicate(Tenant.class);
                    commFacade.sendTenantInvitation(tenant);
                }
            }
        } else {
            new Error(SimpleMessageFormat.format("sending mails for {0} is not yet implemented", emailType));
        }

        Persistence.service().commit();

        String message = users.size() > 1 ? i18n.tr("Emails were sent successfully") : i18n.tr("Email was sent successfully");

        callback.onSuccess(message);
    }
}