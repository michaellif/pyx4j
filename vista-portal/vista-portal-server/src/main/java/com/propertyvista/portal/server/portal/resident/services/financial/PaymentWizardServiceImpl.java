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
package com.propertyvista.portal.server.portal.resident.services.financial;

import java.math.BigDecimal;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.payment.PaymentException;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.payment.ConvenienceFeeCalulationResponseTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentConvenienceFeeDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentDTO;
import com.propertyvista.portal.rpc.portal.resident.services.financial.PaymentWizardService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
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
        Lease lease = ResidentPortalContext.getCurrentUserLease();
        Persistence.service().retrieve(lease.unit());
        Persistence.service().retrieve(lease.unit().building());

        PaymentDTO dto = EntityFactory.create(PaymentDTO.class);

        dto.billingAccount().set(lease.billingAccount());
        dto.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsSetup(lease.billingAccount()));
        dto.allowedPaymentTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(lease.billingAccount(), VistaApplication.resident));
        dto.allowedCardTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedCardTypes(lease.billingAccount(), VistaApplication.resident));
        dto.convenienceFeeApplicableCardTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getConvenienceFeeApplicableCardTypes(lease.billingAccount(), VistaApplication.resident));

        new AddressConverter.StructuredToSimpleAddressConverter().copyBOtoTO(AddressRetriever.getLeaseAddress(lease), dto.address());

        dto.propertyCode().set(lease.unit().building().propertyCode());
        dto.unitNumber().set(lease.unit().info().number());

        dto.leaseId().set(lease.leaseId());
        dto.leaseStatus().set(lease.status());

        dto.leaseTermParticipant().set(ResidentPortalContext.getCurrentUserTenantInLease());

        // some default values:
        dto.createdDate().setValue(new LogicalDate(SystemDateManager.getDate()));

        // calculate current balance:
        dto.amount().setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(lease.billingAccount()));
        if (dto.amount().isNull() || dto.amount().getValue().signum() == -1) {
            dto.amount().setValue(new BigDecimal("0.00"));
        }

        dto.currentAutoPayments().addAll(BillingServiceImpl.retrieveCurrentAutoPayments(lease));

        return dto;
    }

    @Override
    protected void persist(PaymentRecord bo, PaymentDTO to) {
        Lease lease = Persistence.service().retrieve(Lease.class, ResidentPortalContext.getCurrentUserLeaseIdStub().getPrimaryKey());

        bo.paymentMethod().customer().set(ResidentPortalContext.getCurrentUserCustomer());

        // Do not change profile methods
        if (bo.paymentMethod().id().isNull()) {
            if (to.storeInProfile().isBooleanTrue() && PaymentType.availableInProfile().contains(to.paymentMethod().type().getValue())) {
                bo.paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);
            } else {
                bo.paymentMethod().isProfiledMethod().setValue(Boolean.FALSE);
            }

            // some corrections for particular method types:
            if (to.paymentMethod().type().getValue() == PaymentType.Echeck) {
                bo.paymentMethod().isProfiledMethod().setValue(Boolean.TRUE);
            }
        }

        ServerSideFactory.create(PaymentFacade.class).validatePaymentMethod(lease.billingAccount(), bo.paymentMethod(), VistaApplication.resident);
        ServerSideFactory.create(PaymentFacade.class).persistPayment(bo);

        Persistence.service().commit(); // this commit is necessary (before processing next)

        try {
            ServerSideFactory.create(PaymentFacade.class).processPayment(bo, null);
        } catch (PaymentException e) {
            throw new UserRuntimeException(i18n.tr("Payment processing has been Failed!"), e);
        }
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressSimple> callback) {
        callback.onSuccess(AddressRetriever.getLeaseParticipantCurrentAddressSimple(ResidentPortalContext.getCurrentUserTenant()));
    }

    @Override
    public void getProfiledPaymentMethods(AsyncCallback<Vector<LeasePaymentMethod>> callback) {
        callback.onSuccess(new Vector<LeasePaymentMethod>(LeaseParticipantUtils.getProfiledPaymentMethods(ResidentPortalContext.getCurrentUserTenantInLease())));
    }

    @Override
    public void getConvenienceFee(AsyncCallback<PaymentConvenienceFeeDTO> callback, PaymentConvenienceFeeDTO inData) {
        if (inData.paymentMethod().details().isInstanceOf(CreditCardInfo.class)) {
            Lease lease = ResidentPortalContext.getCurrentUserLease();

            CreditCardType ccType = inData.paymentMethod().details().<CreditCardInfo> cast().cardType().getValue();
            if (ServerSideFactory.create(PaymentFacade.class).getConvenienceFeeApplicableCardTypes(lease.billingAccount(), VistaApplication.resident)
                    .contains(ccType)) {
                ConvenienceFeeCalulationResponseTO result = ServerSideFactory.create(PaymentFacade.class).getConvenienceFee(lease.billingAccount(), ccType,
                        inData.amount().getValue());

                inData.fee().setValue(result.feeAmount().getValue());
                inData.total().setValue(inData.amount().getValue().add(result.feeAmount().getValue()));
                inData.transactionNumber().setValue(result.transactionNumber().getValue());
            }
        }

        callback.onSuccess(inData);
    }
}
