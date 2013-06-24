/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-12
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PreauthorizedPaymentSubmittedService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.AddressConverter;
import com.propertyvista.server.common.util.AddressRetriever;

public class PreauthorizedPaymentSubmittedServiceImpl extends EntityDtoBinder<PreauthorizedPayment, PreauthorizedPaymentDTO> implements
        PreauthorizedPaymentSubmittedService {

    public PreauthorizedPaymentSubmittedServiceImpl() {
        super(PreauthorizedPayment.class, PreauthorizedPaymentDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    public void retrieve(AsyncCallback<PreauthorizedPaymentDTO> callback, Key entityId) {
        PreauthorizedPayment dbo = Persistence.secureRetrieve(PreauthorizedPayment.class, entityId);
        PreauthorizedPaymentDTO dto = createDTO(dbo);

        // enhance dto:
        Lease lease = TenantAppContext.getCurrentUserLease();
        Persistence.service().retrieve(lease.unit().building());

        dto.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsSetup(lease.billingAccount()));
        dto.allowedPaymentTypes().setCollectionValue(
                ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(lease.billingAccount(), VistaApplication.residentPortal));

        new AddressConverter.StructuredToSimpleAddressConverter().copyDBOtoDTO(AddressRetriever.getLeaseAddress(lease), dto.address());

        dto.propertyCode().set(lease.unit().building().propertyCode());
        dto.unitNumber().set(lease.unit().info().number());

        dto.leaseId().set(lease.leaseId());
        dto.leaseStatus().set(lease.status());

        dto.nextScheduledPaymentDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextScheduledPreauthorizedPaymentDate(lease));
        dto.paymentCutOffDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getPreauthorizedPaymentCutOffDate(lease));

        dto.total().setValue(BigDecimal.ZERO);
        for (PreauthorizedPaymentCoveredItem item : dto.coveredItems()) {
            dto.total().setValue(dto.total().getValue().add(item.amount().getValue()));
        }

        callback.onSuccess(dto);
    }
}
