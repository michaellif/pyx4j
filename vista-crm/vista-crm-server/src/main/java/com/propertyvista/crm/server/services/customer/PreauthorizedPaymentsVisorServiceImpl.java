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

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade.PaymentMethodUsage;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.crm.rpc.services.customer.PreauthorizedPaymentsVisorService;
import com.propertyvista.crm.server.services.financial.PreauthorizedPaymentsCommons;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PreauthorizedPaymentDTO;

public class PreauthorizedPaymentsVisorServiceImpl implements PreauthorizedPaymentsVisorService {

    @Override
    public void retrieve(AsyncCallback<PreauthorizedPaymentsDTO> callback, Tenant tenantId) {
        PreauthorizedPaymentsDTO dto = EntityFactory.create(PreauthorizedPaymentsDTO.class);

        dto.tenant().set(tenantId);

        fillTenantInfo(dto);
        fillPreauthorizedPayments(dto);
        fillAvailablePaymentMethods(dto);

        callback.onSuccess(dto);
    }

    @Override
    public void create(AsyncCallback<PreauthorizedPaymentDTO> callback, Tenant tenantId) {
        callback.onSuccess(PreauthorizedPaymentsCommons.createNewPreauthorizedPayment(tenantId));
    }

    @Override
    public void save(AsyncCallback<VoidSerializable> callback, PreauthorizedPaymentsDTO dto) {
        PreauthorizedPaymentsCommons.savePreauthorizedPayments(dto.preauthorizedPayments(), dto.tenant());

        Persistence.service().commit();

        callback.onSuccess(null);
    }

    @Override
    public void recollect(AsyncCallback<Vector<AutopayAgreement>> callback, Tenant tenantId) {
        callback.onSuccess(new Vector<AutopayAgreement>(ServerSideFactory.create(PaymentMethodFacade.class).retrieveAutopayAgreements(tenantId)));
    }

    // Internals:

    private void fillTenantInfo(PreauthorizedPaymentsDTO dto) {
        Persistence.ensureRetrieve(dto.tenant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(dto.tenant().lease(), AttachLevel.Attached);

        dto.tenantInfo().name().set(dto.tenant().customer().person().name());

        EntityListCriteria<LeaseTermParticipant> criteria = new EntityListCriteria<LeaseTermParticipant>(LeaseTermParticipant.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant(), dto.tenant()));
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseTermV().holder(), dto.tenant().lease().currentTerm()));

        LeaseTermParticipant<?> ltp = Persistence.service().retrieve(criteria);
        if (ltp != null) {
            dto.tenantInfo().role().setValue(ltp.role().getValue());
        }

        dto.nextPaymentDate().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(dto.tenant().lease()));
    }

    private void fillAvailablePaymentMethods(PreauthorizedPaymentsDTO papDto) {
        Persistence.ensureRetrieve(papDto.tenant(), AttachLevel.Attached);

        List<LeasePaymentMethod> methods = ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(papDto.tenant(),
                PaymentMethodUsage.AutopayAgreementSetup, VistaApplication.crm);

        papDto.availablePaymentMethods().addAll(methods);
    }

    private void fillPreauthorizedPayments(PreauthorizedPaymentsDTO dto) {
        dto.preauthorizedPayments().addAll(PreauthorizedPaymentsCommons.createPreauthorizedPayments(dto.tenant(), RetrieveTarget.Edit));
    }
}
