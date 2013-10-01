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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.PaymentRecordDTO;
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
}
