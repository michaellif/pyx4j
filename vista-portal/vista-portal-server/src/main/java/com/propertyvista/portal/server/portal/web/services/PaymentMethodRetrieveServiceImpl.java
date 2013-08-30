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
package com.propertyvista.portal.server.portal.web.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.web.dto.PaymentMethodDTO;
import com.propertyvista.portal.rpc.portal.web.services.PaymentMethodRetrieveService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class PaymentMethodRetrieveServiceImpl extends EntityDtoBinder<LeasePaymentMethod, PaymentMethodDTO> implements PaymentMethodRetrieveService {

    public PaymentMethodRetrieveServiceImpl() {
        super(LeasePaymentMethod.class, PaymentMethodDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDtoMember(dtoProto.paymentMethod());
    }

    @Override
    public void retrieve(AsyncCallback<PaymentMethodDTO> callback, Key entityId) {
        LeasePaymentMethod dbo = Persistence.secureRetrieve(LeasePaymentMethod.class, entityId);
        PaymentMethodDTO dto = createDTO(dbo);

        // enhance dto:
        Lease lease = TenantAppContext.getCurrentUserLease();
        Persistence.service().retrieve(lease.unit());
        Persistence.service().retrieve(lease.unit().building());

        callback.onSuccess(dto);
    }
}
