/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 12, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.web.services.financial;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.AutoPayDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.AutoPayInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.AutoPaySummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.PaymentMethodDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.PaymentMethodInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.PaymentMethodSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.services.financial.PaymentService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.AddressConverter;
import com.propertyvista.server.common.util.AddressRetriever;
import com.propertyvista.server.common.util.LeaseParticipantUtils;

public class PaymentServiceImpl implements PaymentService {

    @Override
    public void retrievePayment(AsyncCallback<PaymentRecordDTO> callback, PaymentRecord itemId) {
        PaymentRecord dbo = Persistence.secureRetrieve(PaymentRecord.class, itemId.getPrimaryKey());
        PaymentRecordDTO dto = new EntityBinder<PaymentRecord, PaymentRecordDTO>(PaymentRecord.class, PaymentRecordDTO.class) {
            @Override
            protected void bind() {
                bindCompleteObject();
            }
        }.createTO(dbo);

        // enhance dto:
        Lease lease = TenantAppContext.getCurrentUserLease();
        Persistence.service().retrieve(lease.unit());
        Persistence.service().retrieve(lease.unit().building());

        dto.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsSetup(lease.billingAccount()));
        dto.allowedPaymentTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(lease.billingAccount(), VistaApplication.portal));

        new AddressConverter.StructuredToSimpleAddressConverter().copyBOtoTO(AddressRetriever.getLeaseAddress(lease), dto.address());

        dto.propertyCode().set(lease.unit().building().propertyCode());
        dto.unitNumber().set(lease.unit().info().number());

        dto.leaseId().set(lease.leaseId());
        dto.leaseStatus().set(lease.status());

        callback.onSuccess(dto);
    }

    @Override
    public void retrievePaymentMethod(AsyncCallback<PaymentMethodDTO> callback, LeasePaymentMethod itemId) {
        PaymentMethodDTO dto = new PaymentMethodDtoBinder().createTO(Persistence.secureRetrieve(LeasePaymentMethod.class, itemId.getPrimaryKey()));

        // enhance dto:
        Lease lease = TenantAppContext.getCurrentUserLease();
        Persistence.service().retrieve(lease.unit());
        Persistence.service().retrieve(lease.unit().building());

        callback.onSuccess(dto);
    }

    @Override
    public void deletePaymentMethod(AsyncCallback<Boolean> callback, LeasePaymentMethod itemId) {
        ServerSideFactory.create(PaymentMethodFacade.class).deleteLeasePaymentMethod(itemId);
        Persistence.service().commit();

        callback.onSuccess(true);
    }

    @Override
    public void getPaymentMethodSummary(AsyncCallback<PaymentMethodSummaryDTO> callback) {
        PaymentMethodSummaryDTO summary = EntityFactory.create(PaymentMethodSummaryDTO.class);

        Lease lease = TenantAppContext.getCurrentUserLease();

        summary.paymentMethods().addAll(retrievePaymentMethods(lease));

        callback.onSuccess(summary);
    }

    @Override
    public void deleteAutoPay(AsyncCallback<Boolean> callback, PreauthorizedPayment itemId) {
        ServerSideFactory.create(PaymentMethodFacade.class).deletePreauthorizedPayment(itemId);
        Persistence.service().commit();

        callback.onSuccess(true);
    }

    @Override
    public void retreiveAutoPay(AsyncCallback<AutoPayDTO> callback, PreauthorizedPayment entityId) {
        PreauthorizedPayment dbo = Persistence.secureRetrieve(PreauthorizedPayment.class, entityId.getPrimaryKey());
        AutoPayDTO dto = new AutoPayDtoBinder().createTO(dbo);

        // enhance dto:
        Lease lease = TenantAppContext.getCurrentUserLease();
        Persistence.service().retrieve(lease.unit().building());

        dto.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsSetup(lease.billingAccount()));
        dto.allowedPaymentTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(lease.billingAccount(), VistaApplication.portal));

        new AddressConverter.StructuredToSimpleAddressConverter().copyBOtoTO(AddressRetriever.getLeaseAddress(lease), dto.address());

        dto.propertyCode().set(lease.unit().building().propertyCode());
        dto.unitNumber().set(lease.unit().info().number());

        dto.leaseId().set(lease.leaseId());
        dto.leaseStatus().set(lease.status());

        dto.nextScheduledPaymentDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextPreauthorizedPaymentDate(lease));
        dto.paymentCutOffDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getPreauthorizedPaymentCutOffDate(lease));

        dto.total().setValue(BigDecimal.ZERO);
        for (PreauthorizedPaymentCoveredItem item : dto.coveredItems()) {
            dto.total().setValue(dto.total().getValue().add(item.amount().getValue()));
        }

        callback.onSuccess(dto);
    }

    @Override
    public void getAutoPaySummary(AsyncCallback<AutoPaySummaryDTO> callback) {
        AutoPaySummaryDTO summary = EntityFactory.create(AutoPaySummaryDTO.class);

        Lease lease = TenantAppContext.getCurrentUserLease();

        summary.currentAutoPayments().addAll(retrieveCurrentAutoPayments(lease));
        summary.currentAutoPayDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getCurrentPreauthorizedPaymentDate(lease));
        summary.nextAutoPayDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextPreauthorizedPaymentDate(lease));
        summary.modificationsAllowed().setValue(!ServerSideFactory.create(LeaseFacade.class).isMoveOutWithinNextBillingCycle(lease));

        callback.onSuccess(summary);
    }

    // Internals:

    private static List<PaymentMethodInfoDTO> retrievePaymentMethods(Lease lease) {
        List<PaymentMethodInfoDTO> paymentMethods = new ArrayList<PaymentMethodInfoDTO>();

        for (LeasePaymentMethod pm : LeaseParticipantUtils.getProfiledPaymentMethods(TenantAppContext.getCurrentUserTenantInLease())) {
            PaymentMethodInfoDTO pmi = EntityFactory.create(PaymentMethodInfoDTO.class);

            pmi.id().setValue(pm.id().getValue());
            pmi.paymentMethod().set(pm);

            paymentMethods.add(pmi);
        }

        return paymentMethods;
    }

    class PaymentMethodDtoBinder extends EntityBinder<LeasePaymentMethod, PaymentMethodDTO> {

        protected PaymentMethodDtoBinder() {
            super(LeasePaymentMethod.class, PaymentMethodDTO.class);
        }

        @Override
        protected void bind() {
            bindCompleteDtoMember(toProto.paymentMethod());
        }
    }

    // internals:

    private static List<AutoPayInfoDTO> retrieveCurrentAutoPayments(Lease lease) {
        List<AutoPayInfoDTO> currentAutoPayments = new ArrayList<AutoPayInfoDTO>();
        LogicalDate excutionDate = ServerSideFactory.create(PaymentMethodFacade.class).getCurrentPreauthorizedPaymentDate(lease);
        for (PreauthorizedPayment pap : ServerSideFactory.create(PaymentMethodFacade.class).retrieveCurrentPreauthorizedPayments(lease)) {
            AutoPayInfoDTO autoPayInfo = EntityFactory.create(AutoPayInfoDTO.class);
            autoPayInfo.id().setValue(pap.id().getValue());

            autoPayInfo.amount().setValue(BigDecimal.ZERO);
            for (PreauthorizedPaymentCoveredItem ci : pap.coveredItems()) {
                autoPayInfo.amount().setValue(autoPayInfo.amount().getValue().add(ci.amount().getValue()));
            }

            autoPayInfo.paymentDate().setValue(excutionDate);
            autoPayInfo.payer().set(pap.tenant());
            Persistence.ensureRetrieve(autoPayInfo.payer(), AttachLevel.ToStringMembers);
            if (autoPayInfo.payer().equals(TenantAppContext.getCurrentUserTenant())) {
                autoPayInfo.paymentMethod().set(pap.paymentMethod());
            }
            autoPayInfo.expiring().setValue(pap.expiring().getValue());

            currentAutoPayments.add(autoPayInfo);
        }

        return currentAutoPayments;
    }

    class AutoPayDtoBinder extends EntityBinder<PreauthorizedPayment, AutoPayDTO> {

        protected AutoPayDtoBinder() {
            super(PreauthorizedPayment.class, AutoPayDTO.class);
        }

        @Override
        protected void bind() {
            bindCompleteObject();
        }

    }

}
