/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-15
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.crm.rpc.services.customer.PreauthorizedPaymentsVisorService;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;

public class PreauthorizedPaymentsVisorServiceImpl implements PreauthorizedPaymentsVisorService {

    @Override
    public void retrieve(AsyncCallback<PreauthorizedPaymentsDTO> callback, Tenant tenantId) {
        PreauthorizedPaymentsDTO dto = EntityFactory.create(PreauthorizedPaymentsDTO.class);

        dto.tenant().set(tenantId);
        dto.preauthorizedPayments().addAll(ServerSideFactory.create(PaymentMethodFacade.class).retrievePreauthorizedPayments(tenantId));

        fillTenantInfo(dto, tenantId);
        fillAvailablePaymentMethods(dto, tenantId);

        callback.onSuccess(dto);
    }

    private void fillTenantInfo(PreauthorizedPaymentsDTO pads, Tenant tenant) {
        Persistence.ensureRetrieve(tenant, AttachLevel.Attached);
        Persistence.ensureRetrieve(tenant.lease(), AttachLevel.Attached);

        pads.tenantInfo().name().set(tenant.customer().person().name());

        EntityListCriteria<LeaseTermParticipant> criteria = new EntityListCriteria<LeaseTermParticipant>(LeaseTermParticipant.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant(), tenant));
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseTermV().holder(), tenant.lease().currentTerm()));

        LeaseTermParticipant<?> ltp = Persistence.service().retrieve(criteria);
        if (ltp != null) {
            pads.tenantInfo().role().setValue(ltp.role().getValue());
        }
    }

    private void fillAvailablePaymentMethods(PreauthorizedPaymentsDTO pads, Tenant tenant) {
        Persistence.ensureRetrieve(tenant, AttachLevel.Attached);

        EntityListCriteria<LeasePaymentMethod> criteria = new EntityListCriteria<LeasePaymentMethod>(LeasePaymentMethod.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().customer(), tenant.customer()));
        criteria.add(PropertyCriterion.eq(criteria.proto().isProfiledMethod(), Boolean.TRUE));
        criteria.add(PropertyCriterion.eq(criteria.proto().isDeleted(), Boolean.FALSE));

        pads.availablePaymentMethods().addAll(Persistence.service().query(criteria));
    }

    @Override
    public void save(AsyncCallback<VoidSerializable> callback, PreauthorizedPaymentsDTO pads) {
        // delete payment methods removed in UI:
        for (PreauthorizedPayment pad : ServerSideFactory.create(PaymentMethodFacade.class).retrievePreauthorizedPayments(pads.tenant())) {
            if (!pads.preauthorizedPayments().contains(pad)) {
                ServerSideFactory.create(PaymentMethodFacade.class).deletePreauthorizedPayment(pad);
            }
        }

        // save new/edited ones:
        for (PreauthorizedPayment pad : pads.preauthorizedPayments()) {
            ServerSideFactory.create(PaymentMethodFacade.class).persistPreauthorizedPayment(pad, pads.tenant());
        }

        Persistence.service().commit();

        callback.onSuccess(null);
    }

    @Override
    public void delete(AsyncCallback<VoidSerializable> callback, PreauthorizedPayment pad) {
        ServerSideFactory.create(PaymentMethodFacade.class).deletePreauthorizedPayment(pad);
        Persistence.service().commit();

        callback.onSuccess(null);
    }
}
