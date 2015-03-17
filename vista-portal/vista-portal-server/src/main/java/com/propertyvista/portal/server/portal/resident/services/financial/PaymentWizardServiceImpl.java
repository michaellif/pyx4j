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
 */
package com.propertyvista.portal.server.portal.resident.services.financial;

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodTarget;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.config.VistaSystemMaintenance;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.dto.payment.ConvenienceFeeCalculationResponseTO;
import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.portal.rpc.portal.resident.ResidentUserVisit;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentDTO;
import com.propertyvista.portal.rpc.portal.resident.services.financial.PaymentWizardService;
import com.propertyvista.portal.rpc.portal.shared.dto.PaymentConvenienceFeeDTO;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
import com.propertyvista.server.VistaTermsUtils;
import com.propertyvista.server.common.util.AddressRetriever;

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
        Lease lease = ResidentPortalContext.getLease();
        Persistence.service().retrieve(lease.unit());
        Persistence.service().retrieve(lease.unit().building());

        final PaymentDTO dto = EntityFactory.create(PaymentDTO.class);

        dto.billingAccount().set(lease.billingAccount());

        dto.allowedPaymentsSetup().set(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentsSetup(lease.billingAccount(), PaymentMethodTarget.OneTimePayment,
                        VistaApplication.resident));

        dto.convenienceFeeSignedTerm().signature().signatureFormat().setValue(SignatureFormat.AgreeBox);

        if (VistaSystemMaintenance.getApplicationsState().tenantsPaymentsDisabled().getValue()) {
            dto.allowedPaymentsSetup().set(null);
        }

        dto.address().set(AddressRetriever.getLeaseAddress(lease));

        dto.propertyCode().set(lease.unit().building().propertyCode());
        dto.unitNumber().set(lease.unit().info().number());

        dto.leaseId().set(lease.leaseId());
        dto.leaseStatus().set(lease.status());

        dto.leaseTermParticipant().set(ResidentPortalContext.getLeaseTermTenant());

        // some default values:
        dto.created().setValue(SystemDateManager.getDate());

        // calculate current balance:
        dto.amount().setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(lease.billingAccount()));
        if (!dto.amount().isNull() && dto.amount().getValue().compareTo(BigDecimal.ZERO) <= 0) {
            dto.amount().setValue(null);
        }

        dto.currentAutoPayments().addAll(BillingServiceImpl.retrieveCurrentAutoPayments(lease));

        return dto;
    }

    @Override
    protected boolean persist(PaymentRecord bo, PaymentDTO to) {
        if (VistaSystemMaintenance.getApplicationsState().tenantsPaymentsDisabled().getValue()) {
            throw new UserRuntimeException(true, i18n.tr("Application is Unavailable due to short maintenance.\nPlease try again in one hour"));
        }

        Lease lease = Persistence.service().retrieve(Lease.class, ResidentPortalContext.getLeaseIdStub().getPrimaryKey());

        bo.paymentMethod().customer().set(ResidentPortalContext.getCustomer());
        bo.billingAccount().set(lease.billingAccount());

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

        VistaTerms terms = VistaTermsUtils.retrieveVistaTerms(VistaTerms.Target.TenantPaymentWebPaymentFeeTerms);
        bo.convenienceFeeSignedTerm().term().setValue(terms.getPrimaryKey());
        bo.convenienceFeeSignedTerm().termFor().setValue(terms.version().fromDate().getValue());

        ServerSideFactory.create(PaymentFacade.class).validatePaymentMethod(lease.billingAccount(), bo.paymentMethod(), PaymentMethodTarget.OneTimePayment,
                VistaApplication.resident);
        ServerSideFactory.create(PaymentFacade.class).validatePayment(bo, VistaApplication.resident);

        ServerContext.visit(ResidentUserVisit.class).setPaymentDeferredCorrelationId(
                DeferredProcessRegistry.fork(new PaymentDeferredProcess(bo), ThreadPoolNames.PAYMENTS));

        return true;
    }

    @Override
    public void getCurrentAddress(AsyncCallback<InternationalAddress> callback) {
        callback.onSuccess(AddressRetriever.getLeaseParticipantCurrentAddress(ResidentPortalContext.getTenant()));
    }

    @Override
    public void getProfiledPaymentMethods(AsyncCallback<Vector<LeasePaymentMethod>> callback) {
        List<LeasePaymentMethod> methods = ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(
                ResidentPortalContext.getLeaseTermTenant(), PaymentMethodTarget.OneTimePayment, VistaApplication.resident);
        callback.onSuccess(new Vector<LeasePaymentMethod>(methods));
    }

    @Override
    public void getConvenienceFee(AsyncCallback<ConvenienceFeeCalculationResponseTO> callback, PaymentConvenienceFeeDTO to) {
        ConvenienceFeeCalculationResponseTO result = null;
        if (to.paymentMethod().details().isInstanceOf(CreditCardInfo.class)) {
            Lease lease = ResidentPortalContext.getLease();
            CreditCardType ccType = to.paymentMethod().details().<CreditCardInfo> cast().cardType().getValue();
            if (ServerSideFactory.create(PaymentFacade.class).getConvenienceFeeApplicableCardTypes(lease.billingAccount(), VistaApplication.resident)
                    .contains(ccType)) {
                result = ServerSideFactory.create(PaymentFacade.class).getConvenienceFee(lease.billingAccount(), ccType,
                        DomainUtil.roundMoney(to.amount().getValue()));
            }
        }
        callback.onSuccess(result);
    }
}
