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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentRetrieveService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.AddressConverter;
import com.propertyvista.server.common.util.AddressRetriever;

public class PaymentRetrieveServiceImpl extends EntityBinder<PaymentRecord, PaymentRecordDTO> implements PaymentRetrieveService {

    public PaymentRetrieveServiceImpl() {
        super(PaymentRecord.class, PaymentRecordDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    public void retrieve(AsyncCallback<PaymentRecordDTO> callback, Key entityId) {
        PaymentRecord dbo = Persistence.secureRetrieve(PaymentRecord.class, entityId);
        PaymentRecordDTO dto = createTO(dbo);

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
}
