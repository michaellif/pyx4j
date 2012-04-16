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
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailDeliveryStatus;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.biz.communication.mail.MessageTemplates;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.crm.rpc.services.lease.LeaseCrudService;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.server.common.ptapp.ApplicationManager;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.domain.security.TenantUserCredential;

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
    public void sendMail(AsyncCallback<VoidSerializable> callback, Key entityId, Vector<Tenant> tenants, EmailTemplateType emailType) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, entityId);
        for (Tenant tenant : tenants) {
            tenant = Persistence.service().retrieve(Tenant.class, tenant.getPrimaryKey());
            TenantUser user = tenant.customer().user();
            if (user.isValueDetached()) {
                Persistence.service().retrieve(user);
            }
            switch (tenant.role().getValue()) {
            case Applicant:
                ApplicationManager.ensureProspectiveTenantUser(tenant.customer(), tenant.customer().person(), VistaTenantBehavior.TenantPrimary);
                break;
            case CoApplicant:
                ApplicationManager.ensureProspectiveTenantUser(tenant.customer(), tenant.customer().person(), VistaTenantBehavior.TenantSecondary);
                break;
            }

            String token = AccessKey.createAccessToken(user, TenantUserCredential.class, 10);
            if (token == null) {
                throw new UserRuntimeException("Invalid user account");
            }
            MailMessage m = MessageTemplates.createTenantInvitationEmail(user, lease, emailType, token);
            if (MailDeliveryStatus.Success != Mail.send(m)) {
                throw new UserRuntimeException("Mail delivery failed: " + user.email().getValue());
            }
        }
        callback.onSuccess(null);
    }
}