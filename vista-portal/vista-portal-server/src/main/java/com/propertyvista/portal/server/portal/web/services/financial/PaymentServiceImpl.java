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
import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.web.dto.PaymentMethodDTO;
import com.propertyvista.portal.rpc.portal.web.dto.PaymentMethodInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.PaymentMethodSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.services.financial.PaymentService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.LeaseParticipantUtils;

public class PaymentServiceImpl implements PaymentService {

    @Override
    public void retrievePaymentMethod(AsyncCallback<PaymentMethodDTO> callback, LeasePaymentMethod itemId) {
        PaymentMethodDTO dto = new PaymentMethodDtoBinder().createDTO(Persistence.secureRetrieve(LeasePaymentMethod.class, itemId.getPrimaryKey()));
    
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

    class PaymentMethodDtoBinder extends EntityDtoBinder<LeasePaymentMethod, PaymentMethodDTO> {

        protected PaymentMethodDtoBinder() {
            super(LeasePaymentMethod.class, PaymentMethodDTO.class);
        }

        @Override
        protected void bind() {
            bindCompleteDtoMember(dtoProto.paymentMethod());
        }
    }
}
