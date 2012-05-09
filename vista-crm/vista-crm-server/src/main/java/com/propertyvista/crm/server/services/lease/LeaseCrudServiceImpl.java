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

import java.math.BigDecimal;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.crm.rpc.services.lease.LeaseCrudService;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.misc.VistaTODO;

public class LeaseCrudServiceImpl extends LeaseCrudServiceBaseImpl<LeaseDTO> implements LeaseCrudService {

    private final static I18n i18n = I18n.get(LeaseCrudServiceImpl.class);

    public LeaseCrudServiceImpl() {
        super(LeaseDTO.class);
    }

    @Override
    protected void enhanceRetrieved(Lease in, LeaseDTO dto) {
        super.enhanceRetrieved(in, dto);

        dto.transactionHistory().set(retrieveTransactions(dto));
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
    public void activate(AsyncCallback<VoidSerializable> callback, Key entityId) {
        ServerSideFactory.create(LeaseFacade.class).activate(entityId);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void sendMail(AsyncCallback<String> callback, Key entityId, Vector<LeaseParticipant> users, EmailTemplateType emailType) {
        Lease lease = Persistence.service().retrieve(dboClass, entityId.asCurrentKey());
        if ((lease == null) || (lease.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(dboClass).getCaption() + "' " + entityId + " NotFound");
        }
        if (!Lease.Status.current().contains(lease.version().status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Can't send tenant email for inactive Lease"));
        }
        if (users.isEmpty()) {
            throw new UserRuntimeException(i18n.tr("No customer was selected to send email"));
        }

        MasterOnlineApplication app = lease.leaseApplication().onlineApplication();
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

    private TransactionHistoryDTO retrieveTransactions(LeaseDTO dto) {
        if (false) {
            TransactionHistoryDTO history = EntityFactory.create(TransactionHistoryDTO.class);
            if (!VistaTODO.removedForProduction) {
                // TODO DUMMY DATA FOR TESTING THE UI

                // some mockup transactions
                for (int i = 0; i < 5; ++i) {
                    InvoiceLineItem transaction = null;
                    if (i % 2 == 0) {
                        transaction = EntityFactory.create(InvoiceAccountCharge.class);
                        transaction.description().setValue("debit #" + String.valueOf(i));
                        transaction.amount().setValue(new BigDecimal("500.67"));
                    } else {
                        transaction = EntityFactory.create(InvoicePayment.class);
                        transaction.description().setValue("credit #" + String.valueOf(i));
                        transaction.amount().setValue(new BigDecimal("500.33"));
                    }
                    transaction.postDate().setValue(new LogicalDate());
                    history.lineItems().add(transaction);
                }

                // some mockup arrears
                {
                    AgingBuckets arrears = history.agingBuckets().$();
                    arrears.debitType().setValue(DebitType.parking);
                    arrears.current().setValue(new BigDecimal("100.0"));
                    arrears.bucket30().setValue(new BigDecimal("11"));
                    arrears.bucket60().setValue(new BigDecimal("0"));
                    arrears.bucket90().setValue(new BigDecimal("15"));
                    history.agingBuckets().add(arrears);
                }
                {
                    AgingBuckets arrears = history.agingBuckets().$();
                    arrears.debitType().setValue(DebitType.locker);
                    arrears.current().setValue(new BigDecimal("0"));
                    arrears.bucket30().setValue(new BigDecimal("99"));
                    arrears.bucket60().setValue(new BigDecimal("999"));
                    arrears.bucket90().setValue(new BigDecimal("9999"));
                    history.agingBuckets().add(arrears);
                }
                {
                    AgingBuckets arrears = history.agingBuckets().$();
                    arrears.debitType().setValue(DebitType.addOn);
                    arrears.current().setValue(new BigDecimal("1"));
                    arrears.bucket30().setValue(new BigDecimal("2"));
                    arrears.bucket60().setValue(new BigDecimal("3"));
                    arrears.bucket90().setValue(new BigDecimal("4"));
                    history.agingBuckets().add(arrears);
                }

            }

        }
        TransactionHistoryDTO history = ServerSideFactory.create(ARFacade.class).getTransactionHistory(dto.billingAccount());
        return history;
    }

}