/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 */
package com.propertyvista.crm.server.services.billing;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.docs.sheet.AbstractDownloadableCrudServiceDtoImpl;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodTarget;
import com.propertyvista.biz.system.yardi.YardiARFacade;
import com.propertyvista.crm.rpc.services.billing.PaymentRecordCrudService;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.server.common.util.AddressRetriever;
import com.propertyvista.shared.config.VistaFeatures;

public class PaymentRecordCrudServiceImpl extends AbstractDownloadableCrudServiceDtoImpl<PaymentRecord, PaymentRecordDTO> implements PaymentRecordCrudService {

    private static final I18n i18n = I18n.get(PaymentRecordCrudServiceImpl.class);

    public PaymentRecordCrudServiceImpl() {
        super(PaymentRecord.class, PaymentRecordDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected Path convertPropertyDTOPathToDBOPath(String path, PaymentRecord boProto, PaymentRecordDTO toProto) {
        if (path.equals(toProto.rejectedWithNSF().getPath().toString())) {
            return boProto.invoicePaymentBackOut().applyNSF().getPath();
        }
        return super.convertPropertyDTOPathToDBOPath(path, boProto, toProto);
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<PaymentRecord> boCriteria, EntityListCriteria<PaymentRecordDTO> toCriteria) {
        PropertyCriterion nsfCriteria = toCriteria.getCriterion(toCriteria.proto().rejectedWithNSF());
        if (nsfCriteria != null) {
            toCriteria.getFilters().remove(nsfCriteria);
            boCriteria.eq(boCriteria.proto().invoicePaymentBackOut().applyNSF(), nsfCriteria.getValue());
        }
        super.enhanceListCriteria(boCriteria, toCriteria);
    }

    @Override
    protected void enhanceRetrieved(PaymentRecord bo, PaymentRecordDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);
        enhanceListRetrieved(bo, to);

        Persistence.ensureRetrieve(to.batch(), AttachLevel.ToStringMembers);

        to.allowedPaymentsSetup().set(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentsSetup(to.billingAccount(), PaymentMethodTarget.OneTimePayment,
                        VistaApplication.crm));

        to.participants().addAll(retrievePayableUsers(to.billingAccount().lease()));

        if (VistaFeatures.instance().yardiIntegration()) {
            to.externalBatchNumber().setValue(ServerSideFactory.create(YardiARFacade.class).getExternalBatchNumber(bo, false));
            to.externalBatchNumberReversal().setValue(ServerSideFactory.create(YardiARFacade.class).getExternalBatchNumber(bo, true));
        }
    }

    @Override
    protected void enhanceListRetrieved(PaymentRecord bo, PaymentRecordDTO to) {
        super.enhanceListRetrieved(bo, to);

        Persistence.ensureRetrieve(to.batch(), AttachLevel.ToStringMembers);
        Persistence.ensureRetrieve(to.billingAccount().lease().unit().building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(to.paymentMethod().customer(), AttachLevel.Attached);
        Persistence.ensureRetrieve(to.leaseTermParticipant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(to.preauthorizedPayment(), AttachLevel.Attached);

        to.leaseId().set(to.billingAccount().lease().leaseId());
        to.leaseStatus().set(to.billingAccount().lease().status());
        to.propertyCode().set(to.billingAccount().lease().unit().building().propertyCode());
        to.unitNumber().set(to.billingAccount().lease().unit().info().number());
        to.storeInProfile().setValue(bo.paymentMethod().isProfiledMethod().getValue());

        Persistence.ensureRetrieve(bo.invoicePaymentBackOut(), AttachLevel.Attached);
        if (!bo.invoicePaymentBackOut().isNull()) {
            to.rejectedWithNSF().setValue(bo.invoicePaymentBackOut().applyNSF().getValue());
        }

        if (VistaFeatures.instance().yardiIntegration()) {
            to.externalBatchNumber().setValue(ServerSideFactory.create(YardiARFacade.class).getExternalBatchNumber(bo, false));
            to.externalBatchNumberReversal().setValue(ServerSideFactory.create(YardiARFacade.class).getExternalBatchNumber(bo, true));
        }
    }

    @Override
    protected PaymentRecordDTO init(InitializationData initializationData) {
        PaymentInitializationData initData = (PaymentInitializationData) initializationData;
        BillingAccount billingAccount = Persistence.service().retrieve(BillingAccount.class, initData.parent().getPrimaryKey());
        if ((billingAccount == null) || (billingAccount.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(BillingAccount.class).getCaption() + "' " + initData.parent().getPrimaryKey()
                    + " NotFound");
        }

        if (!ServerSideFactory.create(PaymentFacade.class).isPaymentsAllowed(billingAccount)) {
            throw new UserRuntimeException(i18n.tr("No merchantAccount assigned to building to create the payment"));
        }

        Persistence.ensureRetrieve(billingAccount.lease().unit().building(), AttachLevel.Attached);

        PaymentRecordDTO dto = EntityFactory.create(PaymentRecordDTO.class);

        dto.billingAccount().set(billingAccount);
        dto.allowedPaymentsSetup().set(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentsSetup(dto.billingAccount(), PaymentMethodTarget.OneTimePayment,
                        VistaApplication.crm));

        dto.leaseId().set(billingAccount.lease().leaseId());
        dto.leaseStatus().set(billingAccount.lease().status());
        dto.propertyCode().set(billingAccount.lease().unit().building().propertyCode());
        dto.unitNumber().set(billingAccount.lease().unit().info().number());

        dto.participants().addAll(retrievePayableUsers(billingAccount.lease()));

        // some default values:
        dto.created().setValue(SystemDateManager.getDate());

        // calculate current balance:
        dto.amount().setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(billingAccount));
        if (dto.amount().isNull() || dto.amount().getValue().signum() == -1) {
            dto.amount().setValue(new BigDecimal("0.00"));
        }

        return dto;
    }

    @Override
    protected boolean persist(PaymentRecord bo, PaymentRecordDTO to) {
        bo.paymentMethod().customer().set(to.leaseTermParticipant().leaseParticipant().customer());

        // Do not change profile methods
        if (bo.paymentMethod().id().isNull()) {
            if (to.storeInProfile().getValue(false) && PaymentType.availableInProfile().contains(to.paymentMethod().type().getValue())) {
                bo.paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);
            } else {
                bo.paymentMethod().isProfiledMethod().setValue(Boolean.FALSE);
            }

            // some corrections for particular method types:
            if (to.paymentMethod().type().getValue() == PaymentType.Echeck) {
                bo.paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);
            }
        }

        ServerSideFactory.create(PaymentFacade.class).validatePaymentMethod(bo.billingAccount(), to.paymentMethod(), PaymentMethodTarget.OneTimePayment,
                VistaApplication.crm);
        ServerSideFactory.create(PaymentFacade.class).validatePayment(bo, VistaApplication.crm);
        ServerSideFactory.create(PaymentFacade.class).persistPayment(bo);

        return true;
    }

    @Override
    public void getCurrentAddress(AsyncCallback<InternationalAddress> callback, LeaseTermParticipant<? extends LeaseParticipant<?>> participant) {
        callback.onSuccess(AddressRetriever.getLeaseParticipantCurrentAddress(participant));
    }

    @Override
    public void getProfiledPaymentMethods(AsyncCallback<Vector<LeasePaymentMethod>> callback, LeaseTermParticipant<? extends LeaseParticipant<?>> payer) {
        List<LeasePaymentMethod> methods = ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(payer,
                PaymentMethodTarget.OneTimePayment, VistaApplication.crm);
        callback.onSuccess(new Vector<LeasePaymentMethod>(methods));
    }

    // Payment operations:

    @Override
    public void schedulePayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId) {
        ServerSideFactory.create(PaymentFacade.class).schedulePayment(EntityFactory.createIdentityStub(PaymentRecord.class, entityId));
        Persistence.service().commit();
        retrieve(callback, entityId, RetrieveTarget.View);
    }

    @Override
    public void processPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId) {
        Persistence.service().endTransaction();
        ServerSideFactory.create(PaymentFacade.class).processPaymentUnitOfWork(EntityFactory.createIdentityStub(PaymentRecord.class, entityId), false);
        retrieve(callback, entityId, RetrieveTarget.View);
    }

    @Override
    public void clearPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId) {
        ServerSideFactory.create(PaymentFacade.class).clear(EntityFactory.createIdentityStub(PaymentRecord.class, entityId));
        Persistence.service().commit();
        retrieve(callback, entityId, RetrieveTarget.View);
    }

    @Override
    public void rejectPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId, boolean applyNSF) {
        ServerSideFactory.create(PaymentFacade.class).reject(EntityFactory.createIdentityStub(PaymentRecord.class, entityId), applyNSF);
        Persistence.service().commit();
        retrieve(callback, entityId, RetrieveTarget.View);
    }

    @Override
    public void cancelPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId) {
        ServerSideFactory.create(PaymentFacade.class).cancel(EntityFactory.createIdentityStub(PaymentRecord.class, entityId));
        Persistence.service().commit();
        retrieve(callback, entityId, RetrieveTarget.View);
    }

    // internals:
    @SuppressWarnings("incomplete-switch")
    private List<LeaseTermParticipant<? extends LeaseParticipant<?>>> retrievePayableUsers(Lease lease) {
        List<LeaseTermParticipant<? extends LeaseParticipant<?>>> users = new LinkedList<LeaseTermParticipant<? extends LeaseParticipant<?>>>();

        Persistence.service().retrieve(lease.currentTerm());
        if (lease.currentTerm().version().isNull()) {
            lease.currentTerm().set(Persistence.service().retrieve(LeaseTerm.class, lease.currentTerm().getPrimaryKey().asDraftKey()));
        }

        // add payable tenants:
        Persistence.service().retrieve(lease.currentTerm().version().tenants());
        for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
            switch (tenant.role().getValue()) {
            case Applicant:
            case CoApplicant:
                users.add(tenant);
            }
        }

        // add guarantors:
        Persistence.service().retrieve(lease.currentTerm().version().guarantors());
        users.addAll(lease.currentTerm().version().guarantors());

        return users;
    }
}
