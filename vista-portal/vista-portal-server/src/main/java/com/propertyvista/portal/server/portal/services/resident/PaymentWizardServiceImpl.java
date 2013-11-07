/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import java.math.BigDecimal;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.payment.PaymentException;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.domain.dto.financial.PaymentDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentWizardService;
import com.propertyvista.portal.server.security.TenantAppContext;
import com.propertyvista.server.common.util.AddressConverter;
import com.propertyvista.server.common.util.AddressRetriever;
import com.propertyvista.server.common.util.LeaseParticipantUtils;

public class PaymentWizardServiceImpl extends AbstractCrudServiceDtoImpl<PaymentRecord, PaymentDTO> implements PaymentWizardService {

    private static final I18n i18n = I18n.get(PaymentWizardServiceImpl.class);

    public PaymentWizardServiceImpl() {
        super(PaymentRecord.class, PaymentDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected PaymentDTO init(InitializationData initializationData) {
        Lease lease = TenantAppContext.getCurrentUserLease();
        Persistence.service().retrieve(lease.unit());
        Persistence.service().retrieve(lease.unit().building());

        PaymentDTO dto = EntityFactory.create(PaymentDTO.class);

        dto.billingAccount().set(lease.billingAccount());
        dto.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsSetup(lease.billingAccount()));
        dto.allowedPaymentTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(lease.billingAccount(), VistaApplication.resident));
        dto.allowedCardTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedCardTypes(lease.billingAccount(), VistaApplication.resident));

        new AddressConverter.StructuredToSimpleAddressConverter().copyBOtoTO(AddressRetriever.getLeaseAddress(lease), dto.address());

        dto.propertyCode().set(lease.unit().building().propertyCode());
        dto.unitNumber().set(lease.unit().info().number());

        dto.leaseId().set(lease.leaseId());
        dto.leaseStatus().set(lease.status());

        dto.leaseTermParticipant().set(TenantAppContext.getCurrentUserTenantInLease());

        // some default values:
        dto.createdDate().setValue(new LogicalDate(SystemDateManager.getDate()));

        // calculate current balance:
        dto.amount().setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(lease.billingAccount()));
        if (dto.amount().isNull() || dto.amount().getValue().signum() == -1) {
            dto.amount().setValue(new BigDecimal("0.00"));
        }

        dto.currentAutoPayments().addAll(BillSummaryServiceImpl.retrieveCurrentAutoPayments(lease));

        return dto;
    }

    @Override
    @ServiceExecution(waitCaption = "Submitting...")
    public void save(AsyncCallback<Key> callback, PaymentDTO dto) {
        PaymentRecord entity = createBO(dto);

        Lease lease = Persistence.service().retrieve(Lease.class, TenantAppContext.getCurrentUserLeaseIdStub().getPrimaryKey());
        ServerSideFactory.create(PaymentFacade.class).validatePaymentMethod(lease.billingAccount(), dto.paymentMethod(), VistaApplication.resident);

        entity.paymentMethod().customer().set(TenantAppContext.getCurrentUserCustomer());

        // Do not change profile methods
        if (entity.paymentMethod().id().isNull()) {
            if (dto.storeInProfile().isBooleanTrue() && PaymentType.availableInProfile().contains(dto.paymentMethod().type().getValue())) {
                entity.paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);
            } else {
                entity.paymentMethod().isProfiledMethod().setValue(Boolean.FALSE);
            }

            // some corrections for particular method types:
            if (dto.paymentMethod().type().getValue() == PaymentType.Echeck) {
                entity.paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);
            }
        }

        ServerSideFactory.create(PaymentFacade.class).persistPayment(entity);
        Persistence.service().commit();

        try {
            ServerSideFactory.create(PaymentFacade.class).processPayment(entity, null);
        } catch (PaymentException e) {
            throw new UserRuntimeException(i18n.tr("Payment processing has been Failed!"), e);
        }

        Persistence.service().commit();
        callback.onSuccess(entity.getPrimaryKey());
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressSimple> callback) {
        callback.onSuccess(AddressRetriever.getLeaseParticipantCurrentAddressSimple(TenantAppContext.getCurrentUserTenant()));
    }

    @Override
    public void getProfiledPaymentMethods(AsyncCallback<Vector<LeasePaymentMethod>> callback) {
        callback.onSuccess(new Vector<LeasePaymentMethod>(LeaseParticipantUtils.getProfiledPaymentMethods(TenantAppContext.getCurrentUserTenantInLease())));
    }
}
